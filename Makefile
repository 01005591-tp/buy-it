run-local:
	./mvnw quarkus:dev -pl :application -Dquarkus.profile=local

clean:
	./mvnw clean

package:
	./mvnw package

clean-package: clean package
