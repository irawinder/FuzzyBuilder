FROM java
WORKDIR /app
ADD src/HelloHTTP.java /app
RUN javac HelloHTTP.java -d .
EXPOSE 8000
CMD ["java", "HelloHTTP"]