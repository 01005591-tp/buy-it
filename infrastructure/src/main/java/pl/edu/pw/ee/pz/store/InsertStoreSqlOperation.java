package pl.edu.pw.ee.pz.store;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.City;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.FlatNo;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.HouseNo;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.Street;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.StreetName;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.ZipCode;

@RequiredArgsConstructor(access = PACKAGE)
class InsertStoreSqlOperation {

  private final PgPool client;
  private final InsertVariationPiecesSqlOperation insertVariationPiecesSqlOperation;

  Uni<Void> execute(Store store) {
    return client.withTransaction(sqlConnection ->
        sqlConnection.preparedQuery("""
                INSERT INTO stores(id, code, street_name, house_no, flat_no, city, zip_code, country) 
                VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
                ON CONFLICT (id)
                DO UPDATE SET 
                  code = $2
                  ,street_name = $3
                  ,house_no = $4
                  ,flat_no = $5
                  ,city = $6
                  ,zip_code = $7
                  ,country = $8
                """)
            .execute(toSqlParams(store))
            .replaceWithVoid()
            .onItem().transformToUni(success -> insertVariationPiecesSqlOperation.execute(
                sqlConnection,
                store.id(),
                store.products()
            ))
    );
  }

  private Tuple toSqlParams(Store store) {
    return Tuple.from(List.of(
        store.id().value(),
        store.code().value(),
        store.address().street().map(Street::name).filter(Objects::nonNull).map(StreetName::value).getOrNull(),
        store.address().street().map(Street::house).filter(Objects::nonNull).map(HouseNo::value).getOrNull(),
        store.address().street().flatMap(Street::flatNo).filter(Objects::nonNull).map(FlatNo::value).getOrNull(),
        store.address().city().map(City::value).getOrNull(),
        store.address().zipCode().map(ZipCode::value).getOrNull(),
        store.address().country().code().value()
    ));
  }
}
