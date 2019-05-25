package com.thomosim.consentcoin.Persistence.ModelClass;

public enum ContractScopeEnum {
    MYSELF("myself"), MYSELF_AND_WARDS("myself and my wards");

    private final String SCOPE;

    ContractScopeEnum(String scope) {
        SCOPE = scope;

    }

    public String getScope(){
        return SCOPE;
    }
}
