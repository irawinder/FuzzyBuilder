FROM openjdk
WORKDIR /
ADD build/FuzzyIO.jar FuzzyIO.jar
ADD 404.txt 404.txt
ADD index.txt index.txt
RUN mkdir data
ADD data /data
EXPOSE 8080
CMD java -jar FuzzyIO.jar
