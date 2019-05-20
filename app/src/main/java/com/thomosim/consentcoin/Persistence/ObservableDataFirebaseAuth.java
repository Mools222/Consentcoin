package com.thomosim.consentcoin.Persistence;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.thomosim.consentcoin.ObserverPattern.MyObservable;

public class ObservableDataFirebaseAuth extends MyObservable<FirebaseAuth> {
    private MyAuthStateListener myAuthStateListener;
    private FirebaseAuth firebaseAuth;

    public ObservableDataFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        myAuthStateListener = new MyAuthStateListener();
    }

    public void addAuthStateListener() {
        firebaseAuth.addAuthStateListener(myAuthStateListener);
        Log.i("ZZZ", "addAuthStateListener " + firebaseAuth + " " + myAuthStateListener);
    }

    public void removeAuthStateListener() {
        firebaseAuth.removeAuthStateListener(myAuthStateListener);
        Log.i("ZZZ", "removeAuthStateListener " + firebaseAuth + " " + myAuthStateListener);
    }

    private class MyAuthStateListener implements FirebaseAuth.AuthStateListener {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            setValue(firebaseAuth);
        }
    }
}
