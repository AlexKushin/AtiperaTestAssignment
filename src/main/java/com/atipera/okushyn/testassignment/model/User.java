package com.atipera.okushyn.testassignment.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class User {
    @NotNull
    @Size(max = 255)
    private String login;
}
