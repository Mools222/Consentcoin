package com.thomosim.consentcoin.Persistence.ModelClass;

import java.io.Serializable;

public class ConsentcoinReference implements Serializable {
    private String contractId;
    private String memberUid;
    private String organizationUid;
    private String storageUrl;

    public ConsentcoinReference() {
    }

    public ConsentcoinReference(String contractId, String memberUid, String organizationUid, String storageUrl) {
        this.contractId = contractId;
        this.memberUid = memberUid;
        this.organizationUid = organizationUid;
        this.storageUrl = storageUrl;
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
}
