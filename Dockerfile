FROM openjdk
WORKDIR /
ADD build/fuzzy.jar fuzzy.jar
RUN mkdir res
ADD res /res
EXPOSE 8080
CMD java -jar fuzzy.jar
