package com.atipera.okushyn.testassignment.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Repository {
    private String name;
    private boolean fork;
    private Branch[] branches;
    private String branches_url;
}
