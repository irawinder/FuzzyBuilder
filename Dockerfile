FROM openjdk
WORKDIR /
ADD build/fuzzy.jar fuzzy.jar
RUN mkdir res
COPY res /res
EXPOSE 2222
CMD java -jar fuzzy.jar
