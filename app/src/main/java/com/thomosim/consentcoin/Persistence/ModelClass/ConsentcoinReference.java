package com.thomosim.consentcoin.Persistence.ModelClass;

import java.io.Serializable;
import java.util.Date;

public class ConsentcoinReference implements Serializable {
    private String id;
    private String contractId;
    private String memberUid;
    private String organizationUid;
    private String storageUrl;
    private Date revokedDate;

    public ConsentcoinReference() {
    }

    public ConsentcoinReference(String id, String contractId, String memberUid, String organizationUid, String storageUrl) {
        this.id = id;
        this.contractId = contractId;
        this.memberUid = memberUid;
        this.organizationUid = organizationUid;
        this.storageUrl = storageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getMemberUid() {
        return memberUid;
    }

    public void setMemberUid(String memberUid) {
        this.memberUid = memberUid;
    }

    public String getOrganizationUid() {
        return organizationUid;
    }

    public void setOrganizationUid(String organizationUid) {
        this.organizationUid = organizationUid;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public Date getRevokedDate() {
        return revokedDate;
    }

    public void setRevokedDate(Date revokedDate) {
        this.revokedDate = revokedDate;
    }
}
