package com.thomosim.consentcoin.Testing;

import android.content.Context;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtilities {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference databaseReferencePermissionRequests;
    private DatabaseReference databaseReferenceConsentcoinReferences;
    private DatabaseReference databaseReferenceInviteRequests;
    private StorageReference storageReference;

    private static final Object LOCK = new Object();
    private static FirebaseUtilities instance;

    // Singleton pattern
    public static FirebaseUtilities getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new FirebaseUtilities();
            }
        }
        return instance;
    }

    public FirebaseUtilities() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReferencePermissionRequests = FirebaseDatabase.getInstance().getReference().child("PermissionRequests");
        databaseReferenceConsentcoinReferences = FirebaseDatabase.getInstance().getReference().child("ConsentcoinReferences");
        databaseReferenceInviteRequests = FirebaseDatabase.getInstance().getReference().child("InviteRequests");
        storageReference = FirebaseStorage.getInstance().getReference().child("consentcoins");
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public DatabaseReference getDatabaseReferenceCurrentUser() {
        return databaseReferenceUsers.child(firebaseAuth.getCurrentUser().getUid());
    }

    public DatabaseReference getDatabaseReferenceUsers() {
        return databaseReferenceUsers;
    }

    public DatabaseReference getDatabaseReferencePermissionRequests() {
        return databaseReferencePermissionRequests;
    }

    public DatabaseReference getDatabaseReferenceConsentcoinReferences() {
        return databaseReferenceConsentcoinReferences;
    }

    public DatabaseReference getDatabaseReferenceInviteRequests() {
        return databaseReferenceInviteRequests;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public void signOut(Context context) {
        AuthUI.getInstance().signOut(context);
    }
}
