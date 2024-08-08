package com.atipera.okushyn.testassignment.service;

import com.atipera.okushyn.testassignment.model.User;
import com.atipera.okushyn.testassignment.util.RestTemplateUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        String userUrl = String.format(getUserUrl, baseUrl, username);
        return restTemplateUtilService.getResource(User.class, userUrl);


    }

}
