FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

COPY pom.xml .
COPY ruoyi-admin/pom.xml ruoyi-admin/pom.xml
COPY ruoyi-common/pom.xml ruoyi-common/pom.xml
COPY ruoyi-framework/pom.xml ruoyi-framework/pom.xml
COPY ruoyi-system/pom.xml ruoyi-system/pom.xml
COPY pet-system/pom.xml pet-system/pom.xml

RUN mvn -pl ruoyi-admin -am dependency:go-offline -DskipTests

COPY . .
RUN mvn -pl ruoyi-admin -am clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app
ENV TZ=Asia/Shanghai

COPY --from=build /build/ruoyi-admin/target/ruoyi-admin.jar /app/ruoyi-admin.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/ruoyi-admin.jar"]
