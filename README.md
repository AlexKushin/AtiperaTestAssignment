 <h1 align="center">Atipera Test Assignment</h1>
  This repository contains the implementation of a REST service that, with a single request, helps find information about a user's public repositories 
that are not forks. It also provides information about the branches of each repository, including the SHA code of the latest commit.

<h2> ⚡ Project information</h2>





<h3>🔧 Technologies used in the project:</h3>

- Java: 21.0.4 LTS
- Spring Boot: 3.3.2
- Testing frameworks: JUnit, Mockito
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

2. Go to the ```target``` folder and launch the application with the command: ```java -jar TestAssignment-0.0.1.jar```

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

- For interaction with the external GitHub API, RestTemplate is used instead of WebClient:

[Avoid concurrent requests](https://docs.github.com/en/rest/using-the-rest-api/best-practices-for-using-the-rest-api?apiVersion=2022-11-28#avoid-concurrent-requests)



- To request subsequent necessary information and avoid the need to parse or manually input URLs, the service uses URLs provided in the response object:

[Do not manually parse urls](https://docs.github.com/en/rest/using-the-rest-api/best-practices-for-using-the-rest-api?apiVersion=2022-11-28#do-not-manually-parse-urls)


- Pagination has been implemented for convenient display of repository lists.

To use pagination, you need to add request parameters to the endpoint: ```"per_page"```(the number of items displayed on one page) and unnecessary request param  ```"page"``` (the page number). 

Example:
http://localhost:8080/api/users/alexkshin/repos_info?per_page=2&page=1

![pagination](https://github.com/user-attachments/assets/8575bc47-ceef-40a1-b580-9b2535fa9b67)


The default value of request param ```per_page``` = 20.

To display links to the ```"first/next/previous/last"``` pages, links from the response header under the ```"link"``` header are used when requesting a user's repositories.

<h4>📣 important note:</h4>
Pagination works for retreiving all of public user's repositories, which means that number of displayed repository info objects may be less after filtering on fork repositories







