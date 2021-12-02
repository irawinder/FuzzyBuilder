FuzzyIO
=========
A Server for generating point clouds of real estate configurations via Http POST Requests in JSON format

Software Requirements
---------------
1. install [Docker](https://www.docker.com/get-started)
2. (Optional) install [Eclipse](https://www.eclipse.org) (If you would like an IDE for the editing and testing the Java code)

Installation
------------

### For Mac/Linux
1. Run docker on the background
2. Open terminal
3. clone all files in this directory
    ```
    git clone https://github.com/irawinder/FuzzyBuilder.git
    ```
4. Build the docker container with the `Dockerfile` (don't forget to run docker on the background)
    ```
    cd ~/your_localFile/Eclipse/fuzzy-io/
    docker build -t fuzzy:1.0 .
    ```
5. Run the container previously created
    ```
    docker container run --name FuzzyIO -d -p 8080:8080 fuzzy:1.0
    ```
6. go to http://localhost:8080 on your browser. if you see a 405 error, it is working

### Dockerfile
CMD ["java", "FuzzyIO"] line of Dockerfile
will automatically run the server when you deploy
this docker image into the container for operational use, not for development.
