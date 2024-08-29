package com.atipera.okushyn.testassignment.exceptions;

import org.springframework.http.HttpStatusCode;


public record ApiError(HttpStatusCode statusCode, String message) {

}