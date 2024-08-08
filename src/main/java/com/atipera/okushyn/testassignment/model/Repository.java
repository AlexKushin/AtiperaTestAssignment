package com.atipera.okushyn.testassignment.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Repository {
    String name;
    boolean fork;
    Branch[] branches;
}
