package com.atipera.okushyn.testassignment.service;

import com.atipera.okushyn.testassignment.exceptions.ResourceNotFoundException;
import com.atipera.okushyn.testassignment.model.Repository;
import com.atipera.okushyn.testassignment.model.User;
import com.atipera.okushyn.testassignment.model.Branch;
import com.atipera.okushyn.testassignment.model.UserRepoInfo;
import lombok.Getter;
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


@Service
public class GitHubService {

    @Value("${github.api.baseUrl}")
    private String baseUrl;

    @Value("${github.api.getUser}")
    private String getUserUrl;

    @Value("${github.api.getUserRepos}")
    private String getRepositoriesUrl;

    @Value("${github.api.getRepoBranches}")
    private String getBranchesUrl;


    private static final String LINK_HEADER = "link";

    private final RestTemplate restTemplate;

    @Getter
    private String linkHeader;


    @Autowired
    public GitHubService(RestTemplate restTemplate1) {
        this.restTemplate = restTemplate1;
    }


    public User getGitHubUser(String username) {
        try {
            String userUrl = String.format(getUserUrl, baseUrl, username);
            ResponseEntity<User> userResponseEntity = restTemplate.getForEntity(userUrl, User.class);
            return userResponseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatusCode.valueOf(404))) {
                throw new ResourceNotFoundException("User with name " + username + " not found");
            }
            throw ex;
        }
    }


    public Repository[] getUserReposByLogin(int perPage, int page, User user) {
        String userLogin = user.getLogin();

        String repositoriesUrl = String.format(getRepositoriesUrl, baseUrl, userLogin);
        URI uri = UriComponentsBuilder
                .fromUri(URI.create(repositoriesUrl))
                .queryParam("per_page", perPage)
                .queryParam("page", page).build().toUri();

        ResponseEntity<Repository[]> response = restTemplate.getForEntity(uri, Repository[].class);
        linkHeader = response.getHeaders().getFirst(LINK_HEADER);
        Repository[] repositories = response.getBody();

        if (repositories != null && repositories.length > 0) {
            return repositories;
        }
        return new Repository[0];
    }

    public Branch[] getUserRepoBranches(String login, String repoName) {
        String branchesUrl = String.format(getBranchesUrl, baseUrl, login, repoName);
        ResponseEntity<Branch[]> branchesResponseEntity = restTemplate.getForEntity(branchesUrl, Branch[].class);
        Branch[] branches = branchesResponseEntity.getBody();
        if (branches != null && branches.length > 0) {
            return branches;
        }
        return new Branch[0];
    }

    public List<UserRepoInfo> getNotForkRepoInfoList(Repository[] userRepos, final String login) {
        return Arrays.stream(userRepos)
                .filter(rep -> !rep.isFork())
                .map(rep -> mapToUserRepoInfo(rep, login))
                .toList();
    }

    private UserRepoInfo mapToUserRepoInfo(final Repository rep, final String login) {
        String repoName = rep.getName();
        Branch[] brRes = getUserRepoBranches(login, repoName);
        return new UserRepoInfo(repoName, login, brRes);
    }

}
