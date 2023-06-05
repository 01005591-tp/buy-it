package pl.edu.pw.ee.pz.product;

import static java.util.stream.Collectors.groupingBy;
import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.product.SearchProductQuery.SearchProductByBasicCriteriaQuery;
import pl.edu.pw.ee.pz.query.PageRecord;
import pl.edu.pw.ee.pz.query.PageRecords;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;

@RequiredArgsConstructor(access = PACKAGE)
class FindProductsByBasicCriteriaSqlOperation {

  private final PgPool client;
  private final ProductDbMapper productDbMapper;

  Uni<PageResult<Product>> execute(SearchProductByBasicCriteriaQuery query) {
    return client.withTransaction(sqlConnection ->
        findProductsPage(sqlConnection, query)
            .onItem().transformToUni(rows ->
                rows.isEmpty()
                    ? Uni.createFrom().item(PageRecords.<Product>empty(query.page()).toResult())
                    : findProducts(query, sqlConnection, rows)
            )
    );
  }

  private Uni<List<Row>> findProductsPage(
      SqlConnection sqlConnection,
      SearchProductByBasicCriteriaQuery query
  ) {
    return sqlConnection.preparedQuery("""
            SELECT
              p.keyset_id
              ,p.id
              ,COUNT(1) OVER (PARTITION BY NULL) AS all_count
            FROM
              products p
            WHERE
              (CAST($1 as VARCHAR(255)) IS NULL OR p.code ILIKE '%'||CAST($1 as VARCHAR(255))||'%')
              AND (CAST($2 as VARCHAR(36)) IS NULL OR p.brand_id = CAST($2 as VARCHAR(36)))
              AND p.keyset_id > $3
            ORDER BY
              p.keyset_id
            LIMIT $4
            """)
        .execute(Tuple.of(
            query.code().map(ProductCode::value).getOrNull(),
            query.brand().map(BrandId::value).getOrNull(),
            query.page().keySetItemId(),
            query.page().size()
        ))
        .onItem().transformToMulti(RowSet::toMulti)
        .collect().asList();
  }

  private Uni<PageResult<Product>> findProducts(
      SearchProductByBasicCriteriaQuery query,
      SqlConnection sqlConnection,
      List<Row> rows
  ) {
    var keySetIdRecords = rows.stream()
        .map(row -> PageRecord.of(
            row,
            row.getString("id")
        ))
        .collect(Collectors.toMap(PageRecord::value, Function.identity()));
    var keySetIds = keySetIdRecords.values().stream()
        .map(PageRecord::elementId)
        .toArray(Long[]::new);
    return sqlConnection.preparedQuery("""
            SELECT
              p.keyset_id
              ,p.id
              ,p.code
              ,p.brand_id
              ,pva.variation_id
              ,pva.type
              ,pva.value
              ,pva.value_type
            FROM
              products p
              LEFT JOIN product_variation_attributes pva ON
                pva.product_id = p.id
            WHERE
              p.keyset_id = ANY ($1)
            """)
        .execute(Tuple.of(keySetIds))
        .onItem().transformToMulti(RowSet::toMulti)
        .collect().asList()
        .onItem().transform(attributeRows -> toPageRecords(query, keySetIdRecords, attributeRows));
  }

  private PageResult<Product> toPageRecords(
      SearchProductByBasicCriteriaQuery query,
      Map<String, PageRecord<String>> keySetIdRecords,
      List<Row> attributeRows
  ) {
    var productsWithAttributes = attributeRows.stream()
        .map(ProductVariationAttributeView::ofRow)
        .toList();
    var productsById = productsWithAttributes.stream()
        .collect(groupingBy(it -> new ProductId(UUID.fromString(it.productId()))));
    var pageRecords = productsById.values().stream()
        .map(productAttributes -> {
          var pageRecord = keySetIdRecords.get(productAttributes.get(0).productId());
          return productDbMapper.toProductPageRecord(pageRecord, productAttributes);
        })
        .toList();
    return new PageRecords<>(query.page(), pageRecords).toResult();
  }
}
