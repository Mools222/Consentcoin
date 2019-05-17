package com.thomosim.consentcoin.Testing;

import com.google.firebase.auth.FirebaseAuth;
import com.thomosim.consentcoin.Persistence.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.DAOFirebase;
import com.thomosim.consentcoin.Persistence.DAOInterface;
import com.thomosim.consentcoin.Persistence.InviteRequest;
import com.thomosim.consentcoin.Persistence.PermissionRequest;
import com.thomosim.consentcoin.Persistence.User;

import java.util.ArrayList;

public class MyViewModel {
    private ObservableDataFirebaseAuth observableDataFirebaseAuth;
    private ObservableDataUser observableDataUser;
    private ObservableDataDataUsers observableDataDataUsers;
    private ObservableDataPermissionRequests observableDataPermissionRequests;
    private ObservableDataConsentcoinReferences observableDataConsentcoinReferences;
    private ObservableDataInviteRequests observableDataInviteRequests;

//    private MyObservable<FirebaseAuth> logins;
//    private MyObservable<User> user;
//    private MyObservable<ArrayList<User>> users;
//    private MyObservable<ArrayList<PermissionRequest>> permissionRequests;
//    private MyObservable<ArrayList<ConsentcoinReference>> consentcoinReferences;
//    private MyObservable<ArrayList<InviteRequest>> inviteRequests;

    public MyViewModel() {
        FirebaseUtilities firebaseUtilities = FirebaseUtilities.getInstance();
        observableDataFirebaseAuth = new ObservableDataFirebaseAuth(firebaseUtilities.getFirebaseAuth());
        observableDataUser = new ObservableDataUser();
        observableDataDataUsers = new ObservableDataDataUsers(firebaseUtilities.getDatabaseReferenceUsers());
        observableDataPermissionRequests = new ObservableDataPermissionRequests(firebaseUtilities.getDatabaseReferencePermissionRequests());
        observableDataConsentcoinReferences = new ObservableDataConsentcoinReferences(firebaseUtilities.getDatabaseReferenceConsentcoinReferences());
        observableDataInviteRequests = new ObservableDataInviteRequests(firebaseUtilities.getDatabaseReferenceInviteRequests());

//        DAOInterface dao = new DAOFirebase();
//        logins = dao.getLogin();
//        user = dao.getUser();
//        users = dao.getUsers();
//        permissionRequests = dao.getPermissionRequests();
//        consentcoinReferences = dao.getConsentcoinReferences();
//        inviteRequests = dao.getInviteRequests();
    }

//    public MyObservable<FirebaseAuth> getLogins() {
//        return logins;
//    }
//
//    public MyObservable<User> getUser() {
//        return user;
//    }
//
//    public MyObservable<ArrayList<User>> getUsers() {
//        return users;
//    }
//
//    public MyObservable<ArrayList<PermissionRequest>> getPermissionRequests() {
//        return permissionRequests;
//    }
//
//    public MyObservable<ArrayList<ConsentcoinReference>> getConsentcoinReferences() {
//        return consentcoinReferences;
//    }
//
//    public MyObservable<ArrayList<InviteRequest>> getInviteRequests() {
//        return inviteRequests;
//    }

    public ObservableDataFirebaseAuth getObservableDataFirebaseAuth() {
        return observableDataFirebaseAuth;
    }

    public void addAuthStateListener() {
        observableDataFirebaseAuth.addAuthStateListener();
    }

    public void removeAuthStateListener() {
        observableDataFirebaseAuth.removeAuthStateListener();
    }

    public ObservableDataUser getObservableDataUser() {
        return observableDataUser;
    }

    public ObservableDataDataUsers getObservableDataDataUsers() {
        return observableDataDataUsers;
    }

    public ObservableDataPermissionRequests getObservableDataPermissionRequests() {
        return observableDataPermissionRequests;
    }

    public ObservableDataConsentcoinReferences getObservableDataConsentcoinReferences() {
        return observableDataConsentcoinReferences;
    }

    public ObservableDataInviteRequests getObservableDataInviteRequests() {
        return observableDataInviteRequests;
    }

    public void addDatabaseListener() {
        observableDataUser.addDatabaseListener();
        observableDataDataUsers.addDatabaseListener();
        observableDataPermissionRequests.addDatabaseListener();
        observableDataConsentcoinReferences.addDatabaseListener();
        observableDataInviteRequests.addDatabaseListener();
    }

    public void removeDatabaseListener() {
        observableDataUser.removeDatabaseListener();
        observableDataDataUsers.removeDatabaseListener();
        observableDataPermissionRequests.removeDatabaseListener();
        observableDataConsentcoinReferences.removeDatabaseListener();
        observableDataInviteRequests.removeDatabaseListener();
    }
}
