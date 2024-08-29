package com.atipera.okushyn.testassignment.integration;


import com.atipera.okushyn.testassignment.model.UserRepoInfo;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8181)
public class GitHubControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;


    @BeforeEach
    public void setupWireMock() {
        // Reset WireMock before each test to avoid conflicts
        WireMock.reset();
    }

    @Test
    void testGetGitHubUserRepoInfoList_Success() {

        String username = "alexkushin";

        this.webTestClient.get()
                .uri("/api/users/{username}/repos_info", username)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserRepoInfo.class)
                .consumeWith(response -> {
                    List<UserRepoInfo> userRepoInfos = response.getResponseBody();
                    assert userRepoInfos != null;
                    assertEquals(2, userRepoInfos.size());

                    UserRepoInfo repoInfo = userRepoInfos.getFirst();
                    assertEquals("AtiperaTestAssignment", repoInfo.name());
                    assertEquals(username, repoInfo.ownerLogin());
                    assertEquals(14, repoInfo.branches().length);
                    assertTrue(Arrays.asList("configs", "development", "dtos_fixing").containsAll(
                            Arrays.asList(
                                    repoInfo.branches()[0].name(),
                                    repoInfo.branches()[1].name(),
                                    repoInfo.branches()[2].name())
                    ));
                });
    }

    @Test
    void testGetGitHubUserRepoInfoList_NotFound() {
        String username = "nonexist";

        this.webTestClient.get()
                .uri("/api/users/{username}/repos_info", username)
                .exchange()
                .expectStatus().isNotFound();

    }

}