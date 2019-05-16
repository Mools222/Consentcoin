package com.thomosim.consentcoin.Testing;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

public class ObservableDataFirebaseAuth extends MyObservable<FirebaseAuth> {
    private MyAuthStateListener myAuthStateListener;
    private FirebaseAuth firebaseAuth;

    public ObservableDataFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        myAuthStateListener = new MyAuthStateListener();
    }

    public void addAuthStateListener() {
        firebaseAuth.addAuthStateListener(myAuthStateListener);
    }

    public void removeAuthStateListener() {
        if (myAuthStateListener != null) {
            firebaseAuth.removeAuthStateListener(myAuthStateListener);
        }
    }

    private class MyAuthStateListener implements FirebaseAuth.AuthStateListener {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            setValue(firebaseAuth);
        }
    }
}