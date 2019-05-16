package com.thomosim.consentcoin.Persistence;

import java.util.ArrayList;

public interface DAOInterface {
    void addUser(String userType, String uid, String userEmail, String userDisplayName);

    void updateUser(String uid, User user);

    void removeUser(User user);

    void addPermissionRequest(String organizationEmail, String memberEmail, String permissionType);

    void removePermissionRequest(String id);

    void addConsentcoin(String id, String contractType, String organization, String member);

    void removeConsentcoin(Consentcoin consentcoin);

    void addInviteRequest(ArrayList<String> members, final String organization);

    void removeInviteRequest(String id);

}
