package com.thomosim.consentcoin.Persistence;

import java.util.ArrayList;

public interface DAOInterface {
//    <T> T getLogins();

    void addUser(String userType, String uid, String userEmail, String userDisplayName);

//    User getUser(String uid);

//    ArrayList<User> getUsers();

    void updateUser(String uid, User user);

    void removeUser(User user);

    void addPermissionRequest(String organizationEmail, String memberEmail, String permissionType);

//    ArrayList<PermissionRequest> getPermissionRequests();

    void removePermissionRequest(String id);

    void addConsentcoinReference(String id, String member, String organization, String storageUrl);

    void addConsentcoin(String id, String contractType, String organization, String member);

//    Consentcoin getConsentcoin();

//    ArrayList<Consentcoin> getConsentcoins();

    void removeConsentcoin(Consentcoin consentcoin);

    void addInviteRequest(ArrayList<String> members, final String organization);

    void removeInviteRequest(String id);

}
