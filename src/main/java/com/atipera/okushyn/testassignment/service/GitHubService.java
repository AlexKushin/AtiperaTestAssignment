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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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


    public Repository[] getUserRepos(final String username) {
        String reposUrl = String.format(getUserReposUrl, username);
        log.info("Fetching repositories for user: {} from URL: {}", username, reposUrl);
        Repository[] repos = restClient.get()
                .uri(reposUrl)
                .retrieve()
                .body(Repository[].class);
        if (repos == null || repos.length == 0) {
            log.info("No repositories found for user: {}", username);
            return new Repository[0];
        }
        log.info("Found {} repositories for user: {}", repos.length, username);
        return repos;
    }

    public Branch[] getRepoBranches(final String username, final String repoName) {
        String reposBranchesUrl = String.format(getUserRepoBranchesUrl, username, repoName);
        log.info("Fetching branches for repository: {} of user: {} from URL: {}", repoName, username, reposBranchesUrl);
        Branch[] branches = restClient.get()
                .uri(reposBranchesUrl)
                .retrieve()
                .body(Branch[].class);

        if (branches == null || branches.length == 0) {
            log.info("No branches found for repository: {} of user: {}", repoName, username);
            return new Branch[0];
        }
        log.info("Found {} branches for repository: {} of user: {}", branches.length, repoName, username);
        return branches;
    }

    public List<UserRepoInfo> getUsersRepoInfoList(final String username) {
        log.info("Fetching repository information for user: {}", username);
        try {
            Repository[] userRepos = getUserRepos(username);

            List<UserRepoInfo> userRepoInfos = Arrays.stream(userRepos).filter(repo -> !repo.fork())
                    .map(repo -> {
                        Branch[] branches = getRepoBranches(username, repo.name());
                        return new UserRepoInfo(repo.name(), username, branches);
                    })
                    .collect(Collectors.toList());

            log.info("Successfully fetched repository information for user: {}. Total non fork repositories: {}",
                    username, userRepoInfos.size());

            return userRepoInfos;
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
