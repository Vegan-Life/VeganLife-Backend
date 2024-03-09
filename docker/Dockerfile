FROM amazoncorretto:17
# /bulid/libs에 존재하는 빌드 파일을 app.jar라는 이름으로 COPY
COPY /build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "app.jar"]