PROJECT_ROOT_DIR=$(shell git rev-parse --show-toplevel)
APPLICATION_DIR=${PROJECT_ROOT_DIR}/application

run-local:
	./mvnw compile quarkus:dev -pl application -Dquarkus.profile=local --also-make

clean:
	./mvnw clean

package:
	./mvnw package

clean-package: clean package

check-vulnerabilities:
	./mvnw org.owasp:dependency-check-maven:aggregate
