package com.thomosim.consentcoin.Persistence;

import android.content.Context;

import com.thomosim.consentcoin.ObserverPattern.MyObservable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public interface DAOInterface {
    <T> MyObservable<T> getLogin(); // Firebase authentication comes in a FirebaseAuth object. When switching DAO, we'd probably use a different object. Therefore the generic type T is used to accommodate all possibilities.

    void logOut(Context context);

    void setDatabaseReferenceCurrentUser();

    void addUser(String userType, String uid, String userEmail, String userDisplayName, String organizationName);

    MyObservable<User> getUser();

    MyObservable<ArrayList<User>> getUsers();

    void updateUser(String uid, User user);

    void removeUser(User user);

    void addPermissionRequest(String organizationName, String organizationUid, String memberName, String memberUid, String permissionType, Date creationDate, Date permissionStartDate, Date permissionEndDate);

    MyObservable<ArrayList<PermissionRequest>> getPermissionRequests();

    void removePermissionRequest(String id);

    MyObservable<ArrayList<ConsentcoinReference>> getConsentcoinReferences();

    void addConsentcoinReference(String contractId, String memberUid, String organizationUid, String storageUrl);

    void setConsentcoinUrl(String storageUrl);

    void addConsentcoin(Context context, String contractId, String permissionType, String organizationUid, String memberUid, Date creationDate, Date permissionStartDate, Date permissionEndDate);

    MyObservable<Consentcoin> getConsentcoin();

    MyObservable<ArrayList<Consentcoin>> getConsentcoins();

    void removeConsentcoin(Consentcoin consentcoin);

    MyObservable<ArrayList<InviteRequest>> getInviteRequests();

    void addInviteRequest(ArrayList<String> members, final String organization);

    void removeInviteRequest(String id);

}
