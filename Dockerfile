FROM openjdk
WORKDIR /
ADD build/fuzzy.jar fuzzy.jar
RUN mkdir res
COPY res /res
EXPOSE 8080
CMD java -jar fuzzy.jar
