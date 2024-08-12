package com.atipera.okushyn.testassignment.service;

import com.atipera.okushyn.testassignment.exceptions.ResourceNotFoundException;
import com.atipera.okushyn.testassignment.model.Branch;
import com.atipera.okushyn.testassignment.model.Repository;
import com.atipera.okushyn.testassignment.model.User;
import com.atipera.okushyn.testassignment.model.UserRepoInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GitHubServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GitHubService gitHubService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(gitHubService, "baseUrl", "https://api.github.com");
        ReflectionTestUtils.setField(gitHubService, "getUserUrl", "/users/%s");
    }

    @Test
    public void testGetGitHubUser_Success() {
        String username = "octocat";
        User mockUser = new User();
        mockUser.setLogin(username);

        when(restTemplate.getForEntity(anyString(), eq(User.class)))
                .thenReturn(new ResponseEntity<>(mockUser, HttpStatus.OK));

        User result = gitHubService.getGitHubUser(username);

        assertNotNull(result);
        assertEquals(username, result.getLogin());
    }

    @Test
    public void testGetGitHubUser_UserNotFound() {
        String username = "nonexistentuser";

        when(restTemplate.getForEntity(anyString(), eq(User.class)))
                .thenThrow(new HttpClientErrorException(HttpStatusCode.valueOf(404)));

        ResourceNotFoundException exception
                = assertThrows(ResourceNotFoundException.class, () -> gitHubService.getGitHubUser(username));

        assertEquals("User with name nonexistentuser not found", exception.getMessage());
    }

    @Test
    public void testGetUserReposByLogin_Success() {
        User mockUser = new User();
        mockUser.setRepos_url("https://api.github.com/users/octocat/repos");

        Repository[] mockRepos = new Repository[1];
        mockRepos[0] = new Repository();

        URI uri = URI.create("https://api.github.com/users/octocat/repos?per_page=10&page=1");
        when(restTemplate.getForEntity(uri, Repository[].class))
                .thenReturn(new ResponseEntity<>(mockRepos, HttpStatus.OK));

        Repository[] result = gitHubService.getUserReposByLogin(10, 1, mockUser);

        assertNotNull(result);
        assertEquals(1, result.length);
    }

    @Test
    public void testGetUserRepoBranches_Success() {
        Repository mockRepo = new Repository();
        mockRepo.setBranches_url("https://api.github.com/repos/octocat/Hello-World/branches{/branch}");

        Branch[] mockBranches = new Branch[1];
        mockBranches[0] = new Branch();

        String branchesUrl = "https://api.github.com/repos/octocat/Hello-World/branches";
        when(restTemplate.getForEntity(branchesUrl, Branch[].class))
                .thenReturn(new ResponseEntity<>(mockBranches, HttpStatus.OK));

        Branch[] result = gitHubService.getUserRepoBranches(mockRepo);

        assertNotNull(result);
        assertEquals(1, result.length);
    }

    @Test
    public void testGetNotForkRepoInfoList() {
        String login = "octocat";
        Repository mockRepo = new Repository();

        mockRepo.setFork(false);
        mockRepo.setName("Hello-World");
        mockRepo.setBranches_url("https://api.github.com/repos/octocat/Hello-World/branches{/branch}");
        Branch[] mockBranches = new Branch[1];
        mockBranches[0] = new Branch();

        when(restTemplate.getForEntity(anyString(), eq(Branch[].class)))
                .thenReturn(new ResponseEntity<>(mockBranches, HttpStatus.OK));

        List<UserRepoInfo> result = gitHubService.getNotForkRepoInfoList(new Repository[]{mockRepo}, login);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hello-World", result.getFirst().getName());
    }

    @Test
    public void testMapToUserRepoInfo() {
        String login = "octocat";
        Repository mockRepo = new Repository();
        mockRepo.setName("Hello-World");
        mockRepo.setBranches_url("https://api.github.com/repos/octocat/Hello-World/branches{/branch}");

        Branch[] mockBranches = new Branch[1];
        mockBranches[0] = new Branch();

        when(restTemplate.getForEntity(anyString(), eq(Branch[].class)))
                .thenReturn(new ResponseEntity<>(mockBranches, HttpStatus.OK));

        UserRepoInfo result = gitHubService.getNotForkRepoInfoList(new Repository[]{mockRepo}, login).getFirst();

        assertNotNull(result);
        assertEquals("Hello-World", result.getName());
        assertEquals(1, result.getBranches().length);
    }
}
