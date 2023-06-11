package pl.edu.pw.ee.pz.store;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vavr.control.Option;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.Address;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.City;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.FlatNo;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.HouseNo;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.Street;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.StreetName;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.ZipCode;
import pl.edu.pw.ee.pz.sharedkernel.model.Country;
import pl.edu.pw.ee.pz.sharedkernel.model.CountryCode;
import pl.edu.pw.ee.pz.sharedkernel.model.Pieces;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreCode;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

@RequiredArgsConstructor(access = PACKAGE)
class FindStoreByIdSqlOperation {

  private final PgPool client;

  Uni<Store> execute(StoreId id) {
    return client.preparedQuery("""
            SELECT
              s.id
              ,s.code
              ,s.street_name
              ,s.house_no
              ,s.flat_no
              ,s.city
              ,s.zip_code
              ,s.country
              ,spp.product_id
              ,spp.variation_id
              ,spp.pieces
            FROM
              stores s
              LEFT JOIN store_product_pieces spp ON
                s.id = spp.store_id
            WHERE
              s.id = $1
            """)
        .execute(Tuple.of(id.value()))
        .onItem().transformToMulti(RowSet::toMulti)
        .onItem().transform(StoreVariationPiecesView::ofRow)
        .collect().asList()
        .onItem().transform(this::toStore);
  }

  private Store toStore(List<StoreVariationPiecesView> variationPieces) {
    var first = variationPieces.get(0);
    var storeId = new StoreId(UUID.fromString(first.id()));
    var storeCode = new StoreCode(first.code());
    var address = toAddress(first);
    var productVariationPieces = toProductVariationPieces(variationPieces);
    return new Store(
        storeId,
        storeCode,
        address,
        productVariationPieces
    );
  }

  private Map<ProductId, Map<ProductVariationId, Pieces>> toProductVariationPieces(
      List<StoreVariationPiecesView> productVariationPieces
  ) {
    var products = new HashMap<ProductId, Map<ProductVariationId, Pieces>>();
    productVariationPieces.forEach(it -> {
          if (isNull(it.productId)) {
            return;
          }
          products.compute(
              new ProductId(UUID.fromString(it.productId())),
              (productId, variationPieces) -> {
                if (isNull(variationPieces)) {
                  var pieces = new HashMap<ProductVariationId, Pieces>();
                  pieces.put(
                      new ProductVariationId(UUID.fromString(it.variationId())), Pieces.of(it.pieces()));
                  return pieces;
                } else {
                  variationPieces.put(
                      new ProductVariationId(UUID.fromString(it.variationId())), Pieces.of(it.pieces()));
                  return variationPieces;
                }
              }
          );
        }
    );
    return products;
  }

  private Address toAddress(StoreVariationPiecesView storeVariationPieces) {
    return new Address(
        new Street(
            new StreetName(storeVariationPieces.streetName()),
            new HouseNo(storeVariationPieces.houseNo()),
            Option.of(storeVariationPieces.flatNo()).map(FlatNo::new)
        ),
        new City(storeVariationPieces.city()),
        new ZipCode(storeVariationPieces.zipCode()),
        new Country(CountryCode.of(storeVariationPieces.country()))
    );
  }

  record StoreVariationPiecesView(
      String id,
      String code,
      String streetName,
      String houseNo,
      String flatNo,
      String city,
      String zipCode,
      String country,
      String productId,
      String variationId,
      Long pieces
  ) {

    static StoreVariationPiecesView ofRow(Row row) {
      return new StoreVariationPiecesView(
          row.getString("id"),
          row.getString("code"),
          row.getString("street_name"),
          row.getString("house_no"),
          row.getString("flat_no"),
          row.getString("city"),
          row.getString("zip_code"),
          row.getString("country"),
          row.getString("product_id"),
          row.getString("variation_id"),
          row.getLong("pieces")
      );
    }
  }

  record ProductVariationPiecesView(
      ProductId productId,
      Map<ProductVariationId, Pieces> variationPieces
  ) {

  }
}
