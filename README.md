# FuzzyBuilder
by Ira Winder

FuzzyBuilder is an application for generating "fuzzy" resolution development scenarios on a configurable parcel, meant for a process somewhere between site acquisition and highly resolved scenario evaluation.

![FuzzyBuilder by Ira Winder](screenshots/massing.png "FuzzyBuilder by Ira Winder")

More Recent Development is conducted using a more traditional java project. Thusly, we recommend reviewing and contributing using the Eclipse IDE.

## Quickstart
1. Install and run [Docker](https://www.docker.com/get-started)
2. Open terminal in root directory
4. Build the docker container with the `Dockerfile` (don't forget to run docker on the background)
    ```
    docker build -t fuzzy:1.0 .
    ```
5. Run the container previously created
    ```
    docker container run --name fuzzy -d -p 8080:8080 fuzzy:1.0
    ```
6. go to http://localhost:8080 on your browser

## Processing:
An alpha prototype of FuzzyBuilder was quickly developed in July 2019 using the Processing IDE at processing.org. Processing is a light-weight sketch coding environment based upon Java 8. This content is located in the "Processing/" folder, and includes a readme file that explains how to run the code on your own machine. This code is no longer updated, as development has moved into a more robust workflow based on native Java and JavaFX libraries. 