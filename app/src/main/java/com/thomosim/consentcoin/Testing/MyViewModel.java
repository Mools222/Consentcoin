package com.thomosim.consentcoin.Testing;

public class MyViewModel extends MyObservable {
    private ObservableDataFirebaseAuth observableDataFirebaseAuth;
    private ObservableDataUser observableDataUser;
    private ObservableDataDataUsers observableDataDataUsers;
    private ObservableDataPermissionRequests observableDataPermissionRequests;
    private ObservableDataConsentcoinReferences observableDataConsentcoinReferences;
    private ObservableDataInviteRequests observableDataInviteRequests;

    public MyViewModel() {
        FirebaseUtilities firebaseUtilities = FirebaseUtilities.getInstance();
        observableDataFirebaseAuth = new ObservableDataFirebaseAuth(firebaseUtilities.getFirebaseAuth());
        observableDataUser = new ObservableDataUser();
        observableDataDataUsers = new ObservableDataDataUsers(firebaseUtilities.getDatabaseReferenceUsers());
        observableDataPermissionRequests = new ObservableDataPermissionRequests(firebaseUtilities.getDatabaseReferencePermissionRequests());
        observableDataConsentcoinReferences = new ObservableDataConsentcoinReferences(firebaseUtilities.getDatabaseReferenceConsentcoinReferences());
        observableDataInviteRequests = new ObservableDataInviteRequests(firebaseUtilities.getDatabaseReferenceInviteRequests());
    }

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
