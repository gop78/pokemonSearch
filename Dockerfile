FROM eclipse-temurin:17
WORKDIR /Users/wancheullim/Desktop/study/pokeAPI
COPY /build/libs/pokeAPI-0.0.1-SNAPSHOT.jar ./
CMD java -Dserver.port=8080 -Dspring.profiles.active=production -jar *.jar
