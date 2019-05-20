package com.thomosim.consentcoin.Persistence.ModelClass;

import java.io.Serializable;
import java.util.Date;

public class PermissionRequest implements Serializable {

    private String id;
    private String organizationName;
    private String organizationUid;
    private String memberName;
    private String memberUid;
    private String permissionType;
    private Date creationDate;
    private Date permissionStartDate;
    private Date permissionEndDate;

    public PermissionRequest() {
    }

    public PermissionRequest(String id, String organizationName, String organizationUid, String memberName, String memberUid, String permissionType, Date creationDate, Date permissionStartDate, Date permissionEndDate) {
        this.id = id;
        this.organizationName = organizationName;
        this.organizationUid = organizationUid;
        this.memberName = memberName;
        this.memberUid = memberUid;
        this.permissionType = permissionType;
        this.creationDate = creationDate;
        this.permissionStartDate = permissionStartDate;
        this.permissionEndDate = permissionEndDate;
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

    public String getOrganizationUid() {
        return organizationUid;
    }

    public void setOrganizationUid(String organizationUid) {
        this.organizationUid = organizationUid;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberUid() {
        return memberUid;
    }

    public void setMemberUid(String memberUid) {
        this.memberUid = memberUid;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getPermissionStartDate() {
        return permissionStartDate;
    }

    public void setPermissionStartDate(Date permissionStartDate) {
        this.permissionStartDate = permissionStartDate;
    }

    public Date getPermissionEndDate() {
        return permissionEndDate;
    }

    public void setPermissionEndDate(Date permissionEndDate) {
        this.permissionEndDate = permissionEndDate;
    }
}
