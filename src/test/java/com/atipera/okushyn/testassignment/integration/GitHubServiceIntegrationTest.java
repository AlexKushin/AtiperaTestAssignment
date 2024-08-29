package com.atipera.okushyn.testassignment.integration;


import com.atipera.okushyn.testassignment.model.Branch;
import com.atipera.okushyn.testassignment.model.Repository;
import com.atipera.okushyn.testassignment.model.UserRepoInfo;
import com.atipera.okushyn.testassignment.service.GitHubService;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8181)
public class GitHubServiceIntegrationTest {
    @Autowired
    GitHubService gitHubService;


    @Test
    void getUserReposTest() {
        Repository[] repos = gitHubService.getUserRepos("alexkushin");

        assertThat(repos).isNotNull();
        assertThat(repos.length).isEqualTo(3);
    }

    @Test
    void getUserRepoBranchesTest() {
        Branch[] branches = gitHubService.getRepoBranches("alexkushin", "AtiperaTestAssignment");

        assertThat(branches).isNotNull();
        assertThat(branches.length).isEqualTo(14);
    }


    @Test
    void getUserNotForkReposInfoTest() {
        List<UserRepoInfo> repoInfoList = gitHubService.getUsersRepoInfoList("alexkushin");

        assertThat(repoInfoList).isNotNull();
        assertThat(repoInfoList.size()).isEqualTo(2);
    }
}
