package com.atipera.okushyn.testassignment.rest;

import com.atipera.okushyn.testassignment.model.UserRepoInfo;
import com.atipera.okushyn.testassignment.service.GitHubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/")
public class GitHubController {

    private final GitHubService gitHubService;

    @Autowired
    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }


    @GetMapping("users/{username}/repos_info")
    public ResponseEntity<List<UserRepoInfo>> getGitHubUserRepoInfoList(@PathVariable final String username) {
        List<UserRepoInfo> userRepoInfoList = gitHubService.getUsersRepoInfoList(username);
        return ResponseEntity.ok().body(userRepoInfoList);
    }
}
