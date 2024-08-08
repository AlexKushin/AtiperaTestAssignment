package com.atipera.okushyn.testassignment.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Branch {
    String name;

    Commit commit;
}
