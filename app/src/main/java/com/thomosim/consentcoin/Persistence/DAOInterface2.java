package com.thomosim.consentcoin.Persistence;

import android.content.Context;

import com.thomosim.consentcoin.Testing.MyObservable;

import java.net.URL;
import java.util.ArrayList;

public interface DAOInterface2 {
    <T> MyObservable<T> getLogin();

    void addUser(String userType, String uid, String userEmail, String userDisplayName);

    MyObservable<User> getUser();

    MyObservable<ArrayList<User>> getUsers();

    void updateUser(String uid, User user);

    void removeUser(User user);

    void addPermissionRequest(String organizationEmail, String memberEmail, String permissionType);

    MyObservable<ArrayList<PermissionRequest>> getPermissionRequests();

    void removePermissionRequest(String id);

    MyObservable<ArrayList<ConsentcoinReference>> getConsentcoinReferences();

    void addConsentcoinReference(String id, String member, String organization, String storageUrl);

    void addConsentcoin(Context context, String id, String contractType, String organization, String member);

    Consentcoin getConsentcoin(URL url);

    ArrayList<Consentcoin> getConsentcoins();

    void removeConsentcoin(Consentcoin consentcoin);

    MyObservable<ArrayList<InviteRequest>> getInviteRequests();

    void addInviteRequest(ArrayList<String> members, final String organization);

    void removeInviteRequest(String id);

}
