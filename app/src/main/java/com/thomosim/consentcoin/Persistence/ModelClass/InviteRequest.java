package com.thomosim.consentcoin.Persistence.ModelClass;

import java.io.Serializable;

public class InviteRequest implements Serializable {
    private String id;
    private String organizationName;
    private String organizationUID;
    private String member;

    public InviteRequest() {
    }

    public InviteRequest(String id, String organizationName, String organizationUID, String member) {
        this.id = id;
        this.organizationName = organizationName;
        this.organizationUID = organizationUID;
        this.member = member;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getOrganizationUID() {
        return organizationUID;
    }

    public void setOrganizationUID(String organizationUID) {
        this.organizationUID = organizationUID;
    }

}
