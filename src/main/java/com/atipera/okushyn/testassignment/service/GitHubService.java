package com.atipera.okushyn.testassignment.service;

import com.atipera.okushyn.testassignment.exceptions.ResourceNotFoundException;
import com.atipera.okushyn.testassignment.model.User;
import com.atipera.okushyn.testassignment.util.RestTemplateUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class GitHubService {

    @Value("${github.api.baseUrl}")
    private String baseUrl;

    @Value("${github.api.getUser}")
    private String getUserUrl;

    private final RestTemplateUtilService restTemplateUtilService;

    @Autowired
    public GitHubService(RestTemplateUtilService restTemplate) {
        this.restTemplateUtilService = restTemplate;
    }

    public User getGitHubUser(String username) {
        try {
            String userUrl = String.format(getUserUrl, baseUrl, username);
            return restTemplateUtilService.getResource(User.class, userUrl);
        } catch (HttpClientErrorException ex){
            if (ex.getStatusCode().equals(HttpStatusCode.valueOf(404))) {
                throw new ResourceNotFoundException("User with name " + username + " not found");
            }
            throw ex;
        }
    }

}
