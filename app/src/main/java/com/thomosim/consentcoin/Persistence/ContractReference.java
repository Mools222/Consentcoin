package com.thomosim.consentcoin.Persistence;

public class ContractReference {
    private String contractId;
    private String member;
    private String organization;
    private String storageUrl;

    public ContractReference() {
    }

    public ContractReference(String contractId, String member, String organization, String storageUrl) {
        this.contractId = contractId;
        this.member = member;
        this.organization = organization;
        this.storageUrl = storageUrl;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }
}
