⚡Project Information
This repository contains the implementation of a REST service that, with a single request, helps find information about a user's public repositories 
that are not forks. It also provides information about the branches of each repository, including the SHA code of the latest commit.

<h2> ⚡ Project information</h2>





<h3>🔧 Technologies used in the project:</h3>

- Java: 21.0.4 LTS
- Spring Boot: 3.3.2
- Testing frameworks: JUnit, WireMock
- Maven - build automation and project management:


<h3>⚙ Project Setup and Launch</h3>

<h4>To run the service, you need to:</h4>
<h5>1. Clone the project:</h5>

```git clone https://github.com/AlexKushin/AtiperaTestAssignment.git```


  or clone via IDE, repository url: 
  
```https://github.com/AlexKushin/AtiperaTestAssignment.git```

<h5>2. Build and launch:</h5>


To start the application from IDE run the main method in ```AtiperaTestAssignmentApplication```

Or build and launch project from the command line:

1. Navigate to the project root and build the entire project using command: ```mvn clean install```

2. Go to the ```target``` folder and launch the application with the command: ```java -jar TestAssignment-0.0.2.jar```

<br>

After building and launching the application, it will be running on the server at: http://localhost:8080/

The project includes OpenApi (Swagger). The URL to view the API is: http://localhost:8080/swagger-ui/index.html#

<br>
<h3>🌐 How service works</h3>
To obtain the necessary information about a user's repositories, navigate to the URL:

```http://localhost:8080/api/users/{username}/repos_info```

Replace ```{username}``` with the desired GitHub username.

Example:
http://localhost:8080/api/users/octocat/repos_info

<h4>☎️ Call to endpoint using Postman</h4>

Screenshot demonstrates successful call with response ```200 OK``` to endpoint with header ```"Accept: application/json"``` ,
```username = octocat```

![apiCallExample](https://github.com/user-attachments/assets/6d6c9a2f-9a4c-4f53-be5a-81f8003f6dec)


Next screenshot demonstrates unsuccessful call with response ```404 NOT FOUND``` to endpoint with header ```"Accept: application/json"``` , 
```username=octocat111``` 

![apiCall404](https://github.com/user-attachments/assets/e237fa16-4ae0-40da-bd3e-fea99d721259)

<br>
The application can return responses in either json or xml formats.

By default, the request returns a response in application/json format.

<br>

To explicitly specify the desired response format, set the header "Accept: application/json" or append to the URL:
```?mediaType=json```

Example:
http://localhost:8080/api/users/octocat/repos_info?mediaType=json

<br>


To request the response in XML format, set the header "Accept: application/xml" or append to the URL:
```?mediaType=xml```

Example:
http://localhost:8080/api/users/octocat/repos_info?mediaType=xml


<h3>📋 The code follows Best Practices as outlined on the GitHub API page: </h3>

- For interaction with the external GitHub API RestClient is used, 
- For most performing efficiency Virtual Threads has been enabled:

[Avoid concurrent requests](https://docs.github.com/en/rest/using-the-rest-api/best-practices-for-using-the-rest-api?apiVersion=2022-11-28#avoid-concurrent-requests)









