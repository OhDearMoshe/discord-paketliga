FROM amazoncorretto:21-alpine-jdk

ADD build/libs/paketliga-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]