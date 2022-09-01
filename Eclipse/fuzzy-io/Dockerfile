FROM openjdk
WORKDIR /
ADD build/FuzzyIO.jar FuzzyIO.jar
RUN mkdir data
ADD data /data
EXPOSE 8080
CMD java -jar FuzzyIO.jar
