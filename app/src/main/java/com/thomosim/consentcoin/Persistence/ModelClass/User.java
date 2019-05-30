package com.thomosim.consentcoin.Persistence.ModelClass;

import com.thomosim.consentcoin.Persistence.ModelClass.Exception.ProfanityException;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String uid;
    private String email;
    private String type;
    private String firstName;
    private String middleName;
    private String lastName;
    private String organizationName;
    private ArrayList<String> associatedUsersUids;
    private ArrayList<UserActivity> userActivities; // Tried making this a LinkedList to add elements using the addFirst method, but Firebase won't take it

    public User() {
    }

    public User(String uid, String email, String type, String firstName, String middleName, String lastName, String organizationName, ArrayList<String> associatedUsersUids, ArrayList<UserActivity> userActivities) {
        this.uid = uid;
        this.email = email;
        this.type = type;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.organizationName = organizationName;
        this.associatedUsersUids = associatedUsersUids;
        this.userActivities = userActivities;
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

    //TODO IMPLEMENT Profanity better.
    public void setFirstName(String firstName) throws ProfanityException {
        if (firstName.toLowerCase().contains("fuck")){throw new ProfanityException(firstName);}
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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public ArrayList<String> getAssociatedUsersUids() {
        return associatedUsersUids;
    }

    public void setAssociatedUsersUids(ArrayList<String> associatedUsersUids) {
        this.associatedUsersUids = associatedUsersUids;
    }

    public ArrayList<UserActivity> getUserActivities() {
        return userActivities;
    }

    public void setUserActivities(ArrayList<UserActivity> userActivities) {
        this.userActivities = userActivities;
    }
}
