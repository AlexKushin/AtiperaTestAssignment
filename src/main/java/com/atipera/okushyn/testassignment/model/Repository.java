package com.atipera.okushyn.testassignment.model;



public record Repository(String name, boolean fork, Branch[] branches, String branches_url) {

}
