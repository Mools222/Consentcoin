package com.thomosim.consentcoin.Persistens;

import java.io.Serializable;

public class InviteRequest implements Serializable {

    private String id;
    private String organization;
    private String member;

    public InviteRequest() {
    }

    public InviteRequest(String id, String organization, String member) {
        this.id = id;
        this.organization = organization;
        this.member = member;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}
