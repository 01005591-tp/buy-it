package pl.edu.pw.ee.pz.brand;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import java.util.UUID;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.query.PageRecord;
import pl.edu.pw.ee.pz.query.PageRecords;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;

@RequiredArgsConstructor(access = PACKAGE)
class BrandRdbRepository implements BrandProjectionPort {

  private final PgPool client;

  @Override
  public Uni<Void> save(Brand brand) {
    return client.preparedQuery("""
            INSERT INTO brands (keyset_id, id, code) VALUES (nextval('brands_seq'), $1, $2)
            ON CONFLICT (id)
            DO UPDATE SET code = $2
            """)
        .execute(Tuple.of(brand.id().value(), brand.code().value()))
        .replaceWithVoid();
  }

  @Override
  public Uni<Brand> findById(BrandId id) {
    return client.preparedQuery("""
            SELECT b.id, b.code FROM brands b WHERE b.id = $1
            """)
        .execute(Tuple.of(id.value()))
        .onItem().transformToMulti(RowSet::toMulti)
        .onItem().transform(BrandRdbRepository::toBrand)
        .collect().first();
  }

  private static Brand toBrand(Row row) {
    var uuid = UUID.fromString(row.getString("id"));
    var id = new BrandId(uuid);
    var code = new BrandCode(row.getString("code"));
    return new Brand(id, code);
  }

  @Override
  public Uni<PageResult<Brand>> findByCriteria(SearchCriteria criteria) {
    return client.preparedQuery("""
            SELECT
              b.keyset_id
              ,b.id
              ,b.code
              ,COUNT(1) OVER (PARTITION BY NULL) AS all_count
            FROM
              brands b
            WHERE
              b.code ILIKE $1
              AND b.keyset_id > $2
            LIMIT $3
            """)
        .execute(Tuple.of(
            criteria.code(),
            criteria.requestedPage().keySetItemId(),
            criteria.requestedPage().size()
        ))
        .onItem().transform(rowSet -> {
          var records = StreamSupport.stream(rowSet.spliterator(), false)
              .map(row -> PageRecord.of(row, toBrand(row)))
              .toList();
          return new PageRecords<>(criteria.requestedPage(), records)
              .toResult();
        });
  }


}
