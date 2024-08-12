package com.atipera.okushyn.testassignment.rest;

import com.atipera.okushyn.testassignment.model.*;
import com.atipera.okushyn.testassignment.service.GitHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
public class GitHubControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitHubService gitHubService;

    @BeforeEach
    public void setUp() {
        // Mock User
        User mockUser = new User();
        mockUser.setLogin("octocat");
        mockUser.setId(1);
        // Mock Branch[]
        Branch[] branches = new Branch[1];
        branches[0] = new Branch("main", new Commit("dsyttk23j4g3jhg4"));
        // Mock Repository
        Repository mockRepository = new Repository();
        mockRepository.setName("Hello-World");
        mockRepository.setFork(false);
        mockRepository.setBranches(branches);


        // Mock UserRepoInfo
        UserRepoInfo mockUserRepoInfo = new UserRepoInfo("Hello-World", "octocat", branches);

        // Mocking GitHubService methods
        Mockito.when(gitHubService.getGitHubUser(anyString())).thenReturn(mockUser);
        Mockito.when(gitHubService.getUserReposByLogin(anyInt(), anyInt(), any(User.class)))
                .thenReturn(new Repository[]{mockRepository});
        Mockito.when(gitHubService.getNotForkRepoInfoList(any(Repository[].class), anyString()))
                .thenReturn(List.of(mockUserRepoInfo));
    }

    @Test
    public void testGetGitHubUserRepoInfoList_Success() throws Exception {
        mockMvc.perform(get("/api/users/octocat/repos_info")
                        .param("per_page", "10")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Hello-World"))
                .andExpect(jsonPath("$[0].ownerLogin").value("octocat"))
                .andExpect(jsonPath("$[0].branches[0].name").value("main"))
                .andExpect(jsonPath("$[0].branches[0].commit.sha").value("dsyttk23j4g3jhg4"));
    }

    @Test
    public void testGetGitHubUserRepoInfoList_WithPaginationHeader() throws Exception {
        // Mock the link header
        String mockLinkHeader = "<https://api.github.com/user/1/repos?page=2&per_page=10>; rel=\"next\"";

        Mockito.when(gitHubService.getLinkHeader()).thenReturn(mockLinkHeader);

        mockMvc.perform(get("/api/users/octocat/repos_info")
                        .param("per_page", "10")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().exists(HttpHeaders.LINK))
                .andExpect(header().string(HttpHeaders.LINK, "<http://localhost:8080/api/users/octocat/repos_info?page=2&per_page=10>; rel=\"next\""))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Hello-World"))
                .andExpect(jsonPath("$[0].ownerLogin").value("octocat"))
                .andExpect(jsonPath("$[0].branches[0].name").value("main"))
                .andExpect(jsonPath("$[0].branches[0].commit.sha").value("dsyttk23j4g3jhg4"));
    }
}