package com.atipera.okushyn.testassignment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class UserRepoInfo {
    private String name;
    private String ownerLogin;
    private Branch[] branches;
}
