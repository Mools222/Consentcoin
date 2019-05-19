package com.thomosim.consentcoin.Persistence;

import android.content.Context;

import com.thomosim.consentcoin.ObserverPattern.MyObservable;

import java.util.ArrayList;
import java.util.Date;

public interface DAOInterface {

    void addAuthStateListener();

    void removeAuthStateListener();

    void setDatabaseReferenceCurrentUser();

    void addDatabaseListenerUser();

    void addDatabaseListener();

    void removeDatabaseListener();

    void logOut(Context context);

    <T> MyObservable<T> getLogin(); // Firebase authentication comes in a FirebaseAuth object. When switching DAO, we'd probably use a different object. Therefore the generic type T is used to accommodate all possibilities.

    void addUser(String userType, String uid, String userEmail, String userDisplayName, String organizationName);

    MyObservable<User> getUser();

    MyObservable<ArrayList<User>> getUsers();

    void updateUser(String uid, User user);

    void removeUser(User user);

    void addPermissionRequest(String organizationName, String organizationUid, String memberName, String memberUid, String permissionType, Date creationDate, Date permissionStartDate, Date permissionEndDate);

    MyObservable<ArrayList<PermissionRequest>> getPermissionRequests();

    void updatePermissionRequest(String id, PermissionRequest permissionRequest);

    void removePermissionRequest(String id);

    MyObservable<ArrayList<ConsentcoinReference>> getConsentcoinReferences();

    void addConsentcoinReference(String contractId, String memberUid, String organizationUid, String storageUrl);

    void updateConsentcoinReference(String id, ConsentcoinReference consentcoinReference);

    void removeConsentcoinReference(ConsentcoinReference consentcoinReference);

    void addConsentcoin(Context context, String contractId, String permissionType, String organizationUid, String memberUid, Date creationDate, Date permissionStartDate, Date permissionEndDate);

    void setConsentcoinUrl(String storageUrl);

    MyObservable<Consentcoin> getConsentcoin();

    MyObservable<ArrayList<Consentcoin>> getConsentcoins();

    void removeConsentcoin(Consentcoin consentcoin);

    MyObservable<ArrayList<InviteRequest>> getInviteRequests();

    void addInviteRequest(ArrayList<String> members, final String organization);

    void removeInviteRequest(String id);

}
