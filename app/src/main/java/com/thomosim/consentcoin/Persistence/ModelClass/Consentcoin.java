package com.thomosim.consentcoin.Persistence.ModelClass;

import java.io.Serializable;
import java.util.Date;

public class Consentcoin implements Serializable {
    private String contractId;
    private ContractTypeEnum permissionType;
    private String organizationUid;
    private String memberUid;
    private Date creationDate;
    private Date permissionStartDate;
    private Date permissionEndDate;
    private String personsIncluded;

    public Consentcoin() {
    }

    public Consentcoin(String contractId, ContractTypeEnum permissionType, String organizationUid, String memberUid, Date creationDate, Date permissionStartDate, Date permissionEndDate, String personsIncluded) {
        this.contractId = contractId;
        this.permissionType = permissionType;
        this.organizationUid = organizationUid;
        this.memberUid = memberUid;
        this.creationDate = creationDate;
        this.permissionStartDate = permissionStartDate;
        this.permissionEndDate = permissionEndDate;
        this.personsIncluded = personsIncluded;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public ContractTypeEnum getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(ContractTypeEnum permissionType) {
        this.permissionType = permissionType;
    }

    public String getOrganizationUid() {
        return organizationUid;
    }

    public void setOrganizationUid(String organizationUid) {
        this.organizationUid = organizationUid;
    }

    public String getMemberUid() {
        return memberUid;
    }

    public void setMemberUid(String memberUid) {
        this.memberUid = memberUid;
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
