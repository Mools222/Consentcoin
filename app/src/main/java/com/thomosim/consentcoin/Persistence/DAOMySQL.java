package com.thomosim.consentcoin.Persistence;

import java.util.ArrayList;

public class DAOMySQL implements DAOInterface {
    @Override
    public void addUser(String userType, String uid, String userEmail, String userDisplayName) {

    }

    @Override
    public void updateUser(String uid, User user) {

    }

    @Override
    public void removeUser(User user) {

    }

    @Override
    public void addPermissionRequest(String organizationEmail, String memberEmail, String permissionType) {

    }

    @Override
    public void removePermissionRequest(String id) {

    }

    @Override
    public void addConsentcoin(String id, String contractType, String organization, String member) {

    }

    @Override
    public void removeConsentcoin(Consentcoin consentcoin) {

    }

    @Override
    public void addInviteRequest(ArrayList<String> members, String organization) {

    }

    @Override
    public void removeInviteRequest(String id) {

    }
}
