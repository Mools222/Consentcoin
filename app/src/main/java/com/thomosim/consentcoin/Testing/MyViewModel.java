package com.thomosim.consentcoin.Testing;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.thomosim.consentcoin.Persistence.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.PermissionRequest;
import com.thomosim.consentcoin.Persistence.User;

import java.util.ArrayList;

public class MyViewModel extends AndroidViewModel {
    private FirebaseLiveDataFirebaseAuth firebaseLiveDataFirebaseAuth;
    private FirebaseLiveDataUser firebaseLiveDataUser;
    private FirebaseLiveDataUsers firebaseLiveDataUsers;
    private FirebaseLiveDataPermissionRequests firebaseLiveDataPermissionRequests;
    private FirebaseLiveDataConsentcoinReferences firebaseLiveDataConsentcoinReferences;

    public MyViewModel(@NonNull Application application) {
        super(application);
        FirebaseUtilities firebaseUtilities = FirebaseUtilities.getInstance();
        firebaseLiveDataFirebaseAuth = new FirebaseLiveDataFirebaseAuth(firebaseUtilities.getFirebaseAuth());
        firebaseLiveDataUser = new FirebaseLiveDataUser();
        firebaseLiveDataUsers = new FirebaseLiveDataUsers(firebaseUtilities.getDatabaseReferenceUsers());
        firebaseLiveDataPermissionRequests = new FirebaseLiveDataPermissionRequests(firebaseUtilities.getDatabaseReferencePermissionRequests());
        firebaseLiveDataConsentcoinReferences = new FirebaseLiveDataConsentcoinReferences(firebaseUtilities.getDatabaseReferenceConsentcoinReferences());
    }

    public LiveData<FirebaseAuth> getLiveDataFirebaseAuth() {
        return firebaseLiveDataFirebaseAuth;
    }

    public void addAuthStateListener() {
        firebaseLiveDataFirebaseAuth.addAuthStateListener();
    }

    public void removeAuthStateListener() {
        firebaseLiveDataFirebaseAuth.removeAuthStateListener();
    }

    public FirebaseLiveDataUser getFirebaseLiveDataUser() {
        return firebaseLiveDataUser;
    }

    public LiveData<User> getLiveDataUser() {
        return firebaseLiveDataUser;
    }

    public LiveData<ArrayList<User>> getLiveDataUsers() {
        return firebaseLiveDataUsers;
    }

    public LiveData<ArrayList<PermissionRequest>> getLiveDataPermissionRequests() {
        return firebaseLiveDataPermissionRequests;
    }

    public LiveData<ArrayList<ConsentcoinReference>> getLiveDataConsentcoinReferences() {
        return firebaseLiveDataConsentcoinReferences;
    }

    public void addDatabaseListener() {
        firebaseLiveDataUser.addDatabaseListener();
        firebaseLiveDataUsers.addDatabaseListener();
        firebaseLiveDataPermissionRequests.addDatabaseListener();
        firebaseLiveDataConsentcoinReferences.addDatabaseListener();
    }

    public void removeDatabaseListener() {
        firebaseLiveDataUser.removeDatabaseListener();
        firebaseLiveDataUsers.removeDatabaseListener();
        firebaseLiveDataPermissionRequests.removeDatabaseListener();
        firebaseLiveDataConsentcoinReferences.removeDatabaseListener();
    }


}
