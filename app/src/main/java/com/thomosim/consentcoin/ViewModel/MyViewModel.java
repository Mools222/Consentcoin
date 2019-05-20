package com.thomosim.consentcoin.ViewModel;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.thomosim.consentcoin.ObserverPattern.MyObservable;
import com.thomosim.consentcoin.Persistence.ModelClass.Consentcoin;
import com.thomosim.consentcoin.Persistence.ModelClass.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.DAOFirebase;
import com.thomosim.consentcoin.Persistence.DAOInterface;
import com.thomosim.consentcoin.Persistence.ModelClass.InviteRequest;
import com.thomosim.consentcoin.Persistence.ModelClass.PermissionRequest;
import com.thomosim.consentcoin.Persistence.ModelClass.User;

import java.util.ArrayList;

public class MyViewModel {
    private DAOInterface dao;
    private MyObservable<FirebaseAuth> authentication;
    private MyObservable<User> user;
    private MyObservable<ArrayList<User>> users;
    private MyObservable<ArrayList<PermissionRequest>> permissionRequests;
    private MyObservable<ArrayList<ConsentcoinReference>> consentcoinReferences;
    private MyObservable<ArrayList<InviteRequest>> inviteRequests;
    private MyObservable<Consentcoin> consentcoin;

    private static final Object LOCK = new Object();
    private static MyViewModel instance;

    // Singleton pattern
    public static MyViewModel getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new MyViewModel();
            }
        }
        Log.i("ZZZ", "getInstance VM " + instance);
        return instance;
    }

    public MyViewModel() {
        dao = DAOFirebase.getInstance(); // By changing this line (e.g. to "dao = DAOMySQL.getInstance();"), it is possible to change the data persistence of the app
        authentication = dao.getAuthentication();
        user = dao.getUser();
        users = dao.getUsers();
        permissionRequests = dao.getPermissionRequests();
        consentcoinReferences = dao.getConsentcoinReferences();
        inviteRequests = dao.getInviteRequests();
        consentcoin = dao.getConsentcoin();
    }

    public DAOInterface getDao() {
        return dao;
    }

    public MyObservable<FirebaseAuth> getAuthentication() {
        return authentication;
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

    public MyObservable<Consentcoin> getConsentcoin() {
        return consentcoin;
    }
}
