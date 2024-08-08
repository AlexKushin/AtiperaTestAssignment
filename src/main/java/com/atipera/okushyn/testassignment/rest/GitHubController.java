package com.atipera.okushyn.testassignment.rest;

import com.atipera.okushyn.testassignment.model.User;
import com.atipera.okushyn.testassignment.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/")
public class GitHubController {

    private final GitHubService gitHubService;

    @Autowired
    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("users/{username}/repos_info")
    public ResponseEntity<User> getGitHubUserRepo(@PathVariable final String username) {
        User user = gitHubService.getGitHubUser(username);
        return ResponseEntity.ok().body(user);
    }
}
