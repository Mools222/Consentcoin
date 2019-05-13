package com.thomosim.consentcoin.Persistence;

import java.io.Serializable;
import java.util.Date;

public class PermissionRequest implements Serializable {

    private String id;
    private String organization;
    private String member;
    private String permissionType;
    private Date date;

    public PermissionRequest() {
    }

    public PermissionRequest(String id, String organization, String member, String permissionType) {
        this.id = id;
        this.organization = organization;
        this.member = member;
        this.permissionType = permissionType;
        date = new Date();
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

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
