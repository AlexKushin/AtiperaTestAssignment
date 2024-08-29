package com.atipera.okushyn.testassignment.service;

import com.atipera.okushyn.testassignment.exceptions.ExceededRateLimitException;
import com.atipera.okushyn.testassignment.exceptions.ResourceNotFoundException;
import com.atipera.okushyn.testassignment.model.Repository;
import com.atipera.okushyn.testassignment.model.Branch;
import com.atipera.okushyn.testassignment.model.UserRepoInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GitHubService {


    @Value("${github.api.getRepos}")
    private String getUserReposUrl;

    @Value("${github.api.getRepoBranches}")
    private String getUserRepoBranchesUrl;

    private final RestClient restClient;


    @Autowired
    public GitHubService(RestClient restClient) {
        this.restClient = restClient;
    }


    public Repository[] getUserRepos(String username) {

        String reposUrl = String.format(getUserReposUrl, username);
        return restClient.get()
                .uri(reposUrl)
                .retrieve()
                .body(Repository[].class);
    }

    public Branch[] getRepoBranches(String username, String repoName) {
        String reposBranchesUrl = String.format(getUserRepoBranchesUrl, username, repoName);
        return restClient.get()
                .uri(reposBranchesUrl)
                .retrieve()
                .body(Branch[].class);
    }

    public List<UserRepoInfo> getUsersRepoInfoList(String username) {
        try {
            Repository[] userRepos = getUserRepos(username);
            List<UserRepoInfo> userReposList = new ArrayList<>();
            for (Repository repo : userRepos) {
                if (!repo.fork()) {
                    Branch[] branches = getRepoBranches(username, repo.name());
                    userReposList.add(new UserRepoInfo(repo.name(), username, branches));
                }
            }
            return userReposList;
        } catch (HttpClientErrorException ex) {
            log.error("Error fetching GitHub user with username: {}. Status code: {}", username, ex.getStatusCode());

            if (ex.getStatusCode().equals(HttpStatusCode.valueOf(404))) {
                log.warn("GitHub user with username {} not found", username);
                throw new ResourceNotFoundException("User with name " + username + " not found");
            }
            if (ex.getStatusCode().equals(HttpStatusCode.valueOf(403))) {
                log.warn("GitHub API rate limit exceeded");
                throw new ExceededRateLimitException("GitHub Api calls Rate limit exceeded");
            }
            throw ex;
        }
    }
}
