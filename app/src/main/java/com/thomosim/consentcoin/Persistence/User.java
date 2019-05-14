package com.thomosim.consentcoin.Persistence;

import java.util.ArrayList;

public class User {
    private String uid;
    private String email;
    private String type;
    private String firstName;
    private String middleName;
    private String lastName;
    private ArrayList<String> associatedUsersUid;

    public User() {
    }

    public User(String uid, String email, String type, String firstName, String middleName, String lastName, ArrayList<String> associatedUsersUid) {
        this.uid = uid;
        this.email = email;
        this.type = type;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.associatedUsersUid = associatedUsersUid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<String> getAssociatedUsersUid() {
        return associatedUsersUid;
    }

    public void setAssociatedUsersUid(ArrayList<String> associatedUsersUid) {
        this.associatedUsersUid = associatedUsersUid;
    }
}
