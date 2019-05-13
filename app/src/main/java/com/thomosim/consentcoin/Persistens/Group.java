package com.thomosim.consentcoin.Persistens;

import java.util.ArrayList;

public class Group {

    private String name;
    private String uid;
    private ArrayList<String> members;

    public Group(String name, String uid, ArrayList<String> members) {
        this.name = name;
        this.uid = uid;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }
}
