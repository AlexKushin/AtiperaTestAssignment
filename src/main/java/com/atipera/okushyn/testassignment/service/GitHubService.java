package com.atipera.okushyn.testassignment.service;

import com.atipera.okushyn.testassignment.exceptions.ExceededRateLimitException;
import com.atipera.okushyn.testassignment.exceptions.ResourceNotFoundException;
import com.atipera.okushyn.testassignment.model.Repository;
import com.atipera.okushyn.testassignment.model.User;
import com.atipera.okushyn.testassignment.model.Branch;
import com.atipera.okushyn.testassignment.model.UserRepoInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class GitHubService {

    @Value("${github.api.baseUrl}")
    private String baseUrl;

    @Value("${github.api.getUser}")
    private String getUserUrl;

    private static final String LINK_HEADER = "link";

    private final RestTemplate restTemplate;

    @Getter
    private String linkHeader;


    @Autowired
    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public User getGitHubUser(String username) {
        try {
            log.info("Fetching GitHub user details for username: {}", username);
            String userUrl = String.format(getUserUrl, baseUrl, username);
            ResponseEntity<User> userResponseEntity = restTemplate.getForEntity(userUrl, User.class);
            User user = userResponseEntity.getBody();

            log.info("Successfully fetched GitHub user: {}", username);
            log.debug("GitHub user data: {}", user);

            return user;
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


    public Repository[] getUserReposByLogin(int perPage, int page, User user) {
        String repositoriesUrl = user.getRepos_url();
        URI uri = UriComponentsBuilder
                .fromUri(URI.create(repositoriesUrl))
                .queryParam("per_page", perPage)
                .queryParam("page", page).build().toUri();

        log.info("Fetching repositories for user: {} with URL: {}", user.getLogin(), uri);
        ResponseEntity<Repository[]> response = restTemplate.getForEntity(uri, Repository[].class);
        linkHeader = response.getHeaders().getFirst(LINK_HEADER);
        log.debug("Link header: {}", linkHeader);

        Repository[] repositories = response.getBody();


        if (repositories != null && repositories.length > 0) {
            log.info("Fetched {} repositories for user {}", repositories.length, user.getLogin());
            return repositories;
        } else {
            log.warn("No repositories found for user {}", user.getLogin());
            return new Repository[0];
        }
    }


    public Branch[] getUserRepoBranches(Repository repository) {
        String branchesUrl = repository.getBranches_url().replace("{/branch}", "");
        log.info("Fetching branches for repository: {} with URL: {}", repository.getName(), branchesUrl);

        ResponseEntity<Branch[]> branchesResponseEntity = restTemplate.getForEntity(branchesUrl, Branch[].class);
        Branch[] branches = branchesResponseEntity.getBody();
        if (branches != null && branches.length > 0) {
            log.info("Fetched {} branches for repository {}", branches.length, repository.getName());
            return branches;
        } else {
            log.warn("No branches found for repository {}", repository.getName());
            return new Branch[0];
        }
    }

    public List<UserRepoInfo> getNotForkRepoInfoList(Repository[] userRepos, final String login) {
        log.info("Filtering non-fork repositories for user {}", login);
         List<UserRepoInfo> notForkRepos = Arrays.stream(userRepos)
                .filter(rep -> !rep.isFork())
                .map(rep -> mapToUserRepoInfo(rep, login))
                .toList();
        log.debug("Non-fork repositories: {}", notForkRepos);
         return notForkRepos;
    }

    private UserRepoInfo mapToUserRepoInfo(final Repository rep, final String login) {
        String repoName = rep.getName();
        log.info("Mapping repository {} for user {}", repoName, login);
        Branch[] brRes = getUserRepoBranches(rep);
        log.debug("Mapped repository {} with branches {}", repoName, Arrays.toString(brRes));
        return new UserRepoInfo(repoName, login, brRes);
    }

}
