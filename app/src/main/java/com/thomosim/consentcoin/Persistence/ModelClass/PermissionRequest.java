package com.thomosim.consentcoin.Persistence.ModelClass;

import java.io.Serializable;
import java.util.Date;

public class PermissionRequest implements Serializable {

    private String id;
    private String organizationName;
    private String organizationUid;
    private String memberName;
    private String memberUid;
    private ContractTypeEnum permissionType;
    private Date creationDate;
    private Date permissionStartDate;
    private Date permissionEndDate;
    private String personsIncluded;

    public PermissionRequest() {
    }

    public PermissionRequest(String id, String organizationName, String organizationUid, String memberName, String memberUid, ContractTypeEnum permissionType, Date creationDate, Date permissionStartDate, Date permissionEndDate, String personsIncluded) {
        this.id = id;
        this.organizationName = organizationName;
        this.organizationUid = organizationUid;
        this.memberName = memberName;
        this.memberUid = memberUid;
        this.permissionType = permissionType;
        this.creationDate = creationDate;
        this.permissionStartDate = permissionStartDate;
        this.permissionEndDate = permissionEndDate;
        this.personsIncluded = personsIncluded;
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


    public ContractTypeEnum getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(ContractTypeEnum permissionType) {
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

    public String getPersonsIncluded() {
        return personsIncluded;
    }

    public void setPersonsIncluded(String personsIncluded) {
        this.personsIncluded = personsIncluded;
    }
}
