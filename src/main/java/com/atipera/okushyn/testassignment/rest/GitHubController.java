package com.atipera.okushyn.testassignment.rest;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/")
public class GitHubController {


    @GetMapping("users/{username}/repos_info")
    public String getGitHubUserRepo(@PathVariable final String username) {
        return username;
    }
}
