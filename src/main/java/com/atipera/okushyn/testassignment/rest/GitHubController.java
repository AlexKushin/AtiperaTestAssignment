package com.atipera.okushyn.testassignment.rest;

import com.atipera.okushyn.testassignment.model.Repository;
import com.atipera.okushyn.testassignment.model.User;
import com.atipera.okushyn.testassignment.model.UserRepoInfo;
import com.atipera.okushyn.testassignment.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/")
public class GitHubController {

    private final GitHubService gitHubService;

    @Autowired
    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }


    @GetMapping("users/{username}/repos_info")
    public ResponseEntity<List<UserRepoInfo>> getGitHubUserRepoInfoList(
            @PathVariable final String username,
            @RequestParam(defaultValue = "20", name = "per_page") int perPage,
            @RequestParam(defaultValue = "1") int page) {
        User user = gitHubService.getGitHubUser(username);
        String userLogin = user.getLogin();
        int userId = user.getId();

        Repository[] repRes = gitHubService.getUserReposByLogin(perPage, page, user);
        List<UserRepoInfo> notForkRepoInfoList = gitHubService.getNotForkRepoInfoList(repRes, userLogin);

        HttpHeaders headers = new HttpHeaders();
        String linkHeader = gitHubService.getLinkHeader();

        if (linkHeader != null) {
            changePageLinks(headers, linkHeader, userId, userLogin);
            return ResponseEntity.ok().headers(headers).body(notForkRepoInfoList);
        }
        return ResponseEntity.ok().body(notForkRepoInfoList);
    }

    // Non Api

    private void changePageLinks(HttpHeaders headers, String linkHeader, int userId, String userLogin) {
        String myApiPagesLink = linkHeader
                .replaceAll("https://api.github.com/user/" + userId + "/repos",
                        "http://localhost:8080/api/users/" + userLogin + "/repos_info");
        headers.add(HttpHeaders.LINK, myApiPagesLink);
    }

}
