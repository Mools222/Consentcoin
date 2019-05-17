package com.thomosim.consentcoin.Testing;

import com.google.firebase.auth.FirebaseAuth;
import com.thomosim.consentcoin.Persistence.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.DAOFirebase;
import com.thomosim.consentcoin.Persistence.DAOFirebase2;
import com.thomosim.consentcoin.Persistence.DAOInterface;
import com.thomosim.consentcoin.Persistence.DAOInterface2;
import com.thomosim.consentcoin.Persistence.InviteRequest;
import com.thomosim.consentcoin.Persistence.PermissionRequest;
import com.thomosim.consentcoin.Persistence.User;

import java.util.ArrayList;

public class MyViewModel2 {
    private MyObservable<FirebaseAuth> logins;
    private MyObservable<User> user;
    private MyObservable<ArrayList<User>> users;
    private MyObservable<ArrayList<PermissionRequest>> permissionRequests;
    private MyObservable<ArrayList<ConsentcoinReference>> consentcoinReferences;
    private MyObservable<ArrayList<InviteRequest>> inviteRequests;

    public MyViewModel2() {
        FirebaseUtilities firebaseUtilities = FirebaseUtilities.getInstance();

        DAOInterface2 dao = new DAOFirebase2();
        logins = dao.getLogin();
        user = dao.getUser();
        users = dao.getUsers();
        permissionRequests = dao.getPermissionRequests();
        consentcoinReferences = dao.getConsentcoinReferences();
        inviteRequests = dao.getInviteRequests();
    }

    public MyObservable<FirebaseAuth> getLogins() {
        return logins;
    }

    public MyObservable<User> getUser() {
        return user;
    }

    public MyObservable<ArrayList<User>> getUsers() {
        return users;
    }

    public MyObservable<ArrayList<PermissionRequest>> getPermissionRequests() {
        return permissionRequests;
    }

    public MyObservable<ArrayList<ConsentcoinReference>> getConsentcoinReferences() {
        return consentcoinReferences;
    }

    public MyObservable<ArrayList<InviteRequest>> getInviteRequests() {
        return inviteRequests;
    }

}
