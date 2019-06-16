package com.thomosim.consentcoin.Persistence.ModelClass;

//To make it easy to add new contract types. and more transparent in code.
public enum ContractTypeEnum {
    COMMERCIAL_USE("commercial"), NON_COMMERCIAL_USE("non commercial"),
    NON_COMMERCIAL_AND_COMMERCIAL_USE("non commercial and commercial");

    private final String TYPE;

    ContractTypeEnum(String type) {
        TYPE = type;

    }

    public String getType() {
        return TYPE;
    }
}
