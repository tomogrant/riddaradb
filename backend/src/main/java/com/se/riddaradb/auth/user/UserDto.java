package com.se.riddaradb.auth.user;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class UserDto {

    private Integer id;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastNames;

    private String password;

    public UserDto() {
    }

    public UserDto(Integer id, String username, String email, String firstName, String lastNames, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastNames = lastNames;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastNames() {
        return lastNames;
    }

    public void setLastNames(String lastNames) {
        this.lastNames = lastNames;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
