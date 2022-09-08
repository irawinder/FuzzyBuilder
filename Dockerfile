FROM openjdk
WORKDIR /
ADD build/glassmatrix.jar glassmatrix.jar
RUN mkdir res
ADD res /res
EXPOSE 8080
CMD java -jar glassmatrix.jar
