package com.thomosim.consentcoin.Persistence;

import android.content.Context;
import android.content.Intent;

import com.thomosim.consentcoin.ObserverPattern.MyObservable;
import com.thomosim.consentcoin.Persistence.ModelClass.Consentcoin;
import com.thomosim.consentcoin.Persistence.ModelClass.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.ModelClass.ContractTypeEnum;
import com.thomosim.consentcoin.Persistence.ModelClass.InviteRequest;
import com.thomosim.consentcoin.Persistence.ModelClass.PermissionRequest;
import com.thomosim.consentcoin.Persistence.ModelClass.User;

import java.util.ArrayList;
import java.util.Date;

public interface DAOInterface {

    void addAuthStateListener();

    void removeAuthStateListener();

    void setDatabaseReferenceCurrentUser();

    void addDatabaseListenerUser();

    void addDatabaseListener();

    void removeDatabaseListener();

    Intent getSignInIntent();

    void signOut(Context context);

    void addAuthentication();

    <T extends MyObservable> T getAuthentication(); // Firebase authentication comes in a FirebaseAuth object. When switching DAO, we'd probably use a different object. Therefore the generic type T and raw type MyObservable is used to accommodate all possibilities.

    <T> void updateAuthentication(String id, T t);

    <T> void removeAuthentication(T t);

    void addUser(String userType, String uid, String userEmail, String userDisplayName, String organizationName);

    MyObservable<User> getUser();

    MyObservable<ArrayList<User>> getUsers();

    void updateUser(String uid, User user);

    void removeUser(User user);

    void addPermissionRequest(String organizationName, String organizationUid, String memberName, String memberUid, ContractTypeEnum permissionType, Date creationDate, Date permissionStartDate, Date permissionEndDate, String personsIncluded);

    MyObservable<ArrayList<PermissionRequest>> getPermissionRequests();

    void updatePermissionRequest(String id, PermissionRequest permissionRequest);

    void removePermissionRequest(String id);

    MyObservable<ArrayList<ConsentcoinReference>> getConsentcoinReferences();

    void addConsentcoinReference(String contractId, String memberUid, String organizationUid, String storageUrl);

    void updateConsentcoinReference(String id, ConsentcoinReference consentcoinReference);

    void removeConsentcoinReference(ConsentcoinReference consentcoinReference);

    void addConsentcoin(Context context, String contractId, ContractTypeEnum permissionType, String organizationUid, String memberUid, Date creationDate, Date permissionStartDate, Date permissionEndDate);

    void setConsentcoinUrl(String storageUrl);

    MyObservable<Consentcoin> getConsentcoin();

    MyObservable<ArrayList<Consentcoin>> getConsentcoins();

    void removeConsentcoin(Consentcoin consentcoin);

    MyObservable<ArrayList<InviteRequest>> getInviteRequests();

    void addInviteRequest(ArrayList<String> members, final String organization);

    void removeInviteRequest(String id);

}
