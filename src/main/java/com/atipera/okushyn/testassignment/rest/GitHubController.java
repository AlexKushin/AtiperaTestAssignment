package com.atipera.okushyn.testassignment.rest;

import com.atipera.okushyn.testassignment.model.Repository;
import com.atipera.okushyn.testassignment.model.User;
import com.atipera.okushyn.testassignment.model.UserRepoInfo;
import com.atipera.okushyn.testassignment.service.GitHubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<List<UserRepoInfo>> getGitHubUserRepoInfoList(
            @PathVariable final String username,
            @RequestParam(defaultValue = "20", name = "per_page") int perPage,
            @RequestParam(defaultValue = "1") int page) {
        log.info("Fetching non-fork repositories for GitHub user: {}", username);

        User user = gitHubService.getGitHubUser(username);
        String userLogin = user.login();
        int userId = user.id();

        Repository[] repRes = gitHubService.getUserReposByLogin(perPage, page, user);
        List<UserRepoInfo> notForkRepoInfoList = gitHubService.getNotForkRepoInfoList(repRes, userLogin);
        log.info("Fetched {} non-fork repositories for user: {}", notForkRepoInfoList.size(), username);

        HttpHeaders headers = new HttpHeaders();
        String linkHeader = gitHubService.getLinkHeader();

        if (linkHeader != null) {
            log.debug("Processing pagination links for user: {}", userLogin);

            changePageLinks(headers, linkHeader, userId, userLogin);
            return ResponseEntity.ok().headers(headers).body(notForkRepoInfoList);
        }
        return ResponseEntity.ok().body(notForkRepoInfoList);
    }

    // Non Api

    private void changePageLinks(HttpHeaders headers, String linkHeader, int userId, String userLogin) {
        log.debug("Changing page links in the header for fetching user {} repository info list", userLogin);
        String myApiPagesLink = linkHeader
                .replaceAll("https://api.github.com/user/" + userId + "/repos",
                        "http://localhost:8080/api/users/" + userLogin + "/repos_info");
        headers.add(HttpHeaders.LINK, myApiPagesLink);
        log.debug("Updated pagination link header: {}", myApiPagesLink);

    }

}
