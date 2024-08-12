package com.atipera.okushyn.testassignment.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {
    private HttpStatusCode statusCode;
    private String message;
}