package com.thomosim.consentcoin.Persistens;

public class PermissionRequest {

    private String id;
    private String organization;
    private String member;
    private String permissionType;

    public PermissionRequest() {
    }

    public PermissionRequest(String id, String organization, String member, String permissionType) {
        this.id = id;
        this.organization = organization;
        this.member = member;
        this.permissionType = permissionType;
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
}
