FROM gradle:8.4 as builder

COPY build.gradle.kts .
COPY gradle.properties .
COPY src ./src

RUN gradle build && gradle assemble

FROM openjdk:17
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=builder /home/gradle/build/libs/school-management-api.jar /app/school-management-api.jar
ENTRYPOINT ["java","-jar","/app/school-management-api.jar"]