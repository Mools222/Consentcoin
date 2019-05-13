package com.thomosim.consentcoin.Persistence;

import java.io.Serializable;

public class Consentcoin implements Serializable {
    private String contractId;
    private String contractType;
    private String memberId;
    private String organizationId;

    public Consentcoin(String contractId, String contractType, String memberId, String organizationId) {
        this.contractId = contractId;
        this.contractType = contractType;
        this.memberId = memberId;
        this.organizationId = organizationId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
