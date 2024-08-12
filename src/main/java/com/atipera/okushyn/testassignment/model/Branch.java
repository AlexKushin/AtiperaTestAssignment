package com.atipera.okushyn.testassignment.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Branch {
    private String name;

    private Commit commit;
}
