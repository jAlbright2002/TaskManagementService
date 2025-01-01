
# Task Management Service

## Getting Started

These instructions will give you a copy of the project up and running on
your local machine for development and testing purposes. 

### Prerequisites

Requirements for the software and other tools to build, test and push 
- [Java](https://www.oracle.com/java/technologies/downloads/#jdk21-windows)
	- Download Java 21
	- Run installer, keep all defaults
	- Set JAVA_HOME environment variable - [Follow answer by Taras Melnyk](https://stackoverflow.com/questions/11161248/setting-java-home)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
	- [Windows | Docker Docs](https://docs.docker.com/desktop/setup/install/windows-install/#install-docker-desktop-on-windows) follow this guide to install Docker on Windows

## Running the Application

[Download](https://github.com/jAlbright2002/TaskManagementService.git) and open the project in an IDE or locate the root directory within your terminal

Run the following command, this will pull and run the docker images for the project, RabbitMQ and Mongo

	docker-compose up

The project will now be running and you can access its endpoints ->

Each request needs a token to authenticate the action. This can be retrieved after login in the [Task Registration Service](https://github.com/jAlbright2002/TaskManagementRegistration.git), it is in the body of the response, which will look like this

	Welcome user
	Token: this-is-a-token

Copy this token

Click add header in the requests and assign it to a new header called "Authorization"

![Screenshot 2025-01-01 210053](https://github.com/user-attachments/assets/d9b96f80-9cc5-4ca6-81a5-8443a7434f6c)

GET
[Get all tasks](http://localhost:8082/allTasks/email)

POST
[Create task](http://localhost:8082/createTask)

PUT
[Update task](http://localhost:8082/updateTask/email/id)

DELETE
[Delete task](http://localhost:8082/deleteTask/email/id)

Note: Replace **email** with registered email from Task Registration Service and replace **id** with id in response body after task creation

Example JSON can be found [here](https://github.com/jAlbright2002/TaskManagementService/blob/master/src/main/resources/ExampleJSON), needed for POST/PUT requests

It is recommended to use an API testing tool such as [Postman](https://www.postman.com/downloads/) or [Talend API Extension](https://chromewebstore.google.com/detail/talend-api-tester-free-ed/aejoelaoggembcahagimdiliamlcdmfm) 
(Note: Must use a Chromium based browser to use this extension)

## Project Architecture
The **Task Management Service** is the core of this project. It is what allows the user to do CRUD (Create/Read/Update/Delete) operations for tasks. Every interaction is authenticated by using a token in the HTTP request header. All tasks are stored in the database, associated with the users email. The service communicates with the Task Notification Service to create action notifications for the user. Written in Java and built on SpringBoot. 


## Running the Tests
[Download](https://github.com/jAlbright2002/TaskManagementService.git) and open the project in an IDE or locate the root directory within your terminal and run the following command

	.\mvnw verify

This will test all unit and integration tests

## Authors
  - **James Albright** - *Project Owner* -
  - **Billie Thompson** - *Provided README Template* -
    [PurpleBooth](https://github.com/PurpleBooth)
