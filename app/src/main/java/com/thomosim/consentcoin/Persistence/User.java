package com.thomosim.consentcoin.Persistence;

import java.util.ArrayList;

public class User {
    private String email;
    private String type;
    private String firstName;
    private String lastName;
    private ArrayList<String> associatedUsers;

    public User() {
    }

    public User(String email, String type, String firstName, String lastName, ArrayList<String> associatedUsers) {
        this.email = email;
        this.type = type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.associatedUsers = associatedUsers;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
