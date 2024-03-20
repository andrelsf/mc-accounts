# Microservice Accounts

Acoes:
 - Consulta saldo
 - Transferencias

GET : /accounts/{customerId}/balance
POST: /accounts/{customerId}/transfers

### Native Image

```shell
mvn -Pnative native:compile
```

### Reference Documentation
For further reference, please consider the following sections:

* [Spring Boot Maven Native Image](https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html#native-image.developing-your-first-application.native-build-tools.maven)
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.3/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.3/maven-plugin/reference/html/#build-image)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/3.2.3/reference/htmlsingle/index.html#actuator)
* [Resilience4J](https://docs.spring.io/spring-cloud-circuitbreaker/docs/current/reference/html/#configuring-resilience4j-circuit-breakers)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.2.3/reference/htmlsingle/index.html#web.reactive)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)

