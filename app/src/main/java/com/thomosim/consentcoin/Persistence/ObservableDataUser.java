package com.thomosim.consentcoin.Persistence;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thomosim.consentcoin.ObserverPattern.MyObservable;
import com.thomosim.consentcoin.Persistence.ModelClass.User;

public class ObservableDataUser extends MyObservable<User> {
    private DatabaseReference databaseReference;
    private MyValueEventListener myValueEventListener;

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public void addDatabaseListener() {
        if (myValueEventListener == null) {
            myValueEventListener = new MyValueEventListener();
            databaseReference.addValueEventListener(myValueEventListener);
        }
    }

    public void removeDatabaseListener() {
        if (myValueEventListener != null) {
            databaseReference.removeEventListener(myValueEventListener);
            myValueEventListener = null;
        }
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot){
            setValue(dataSnapshot.getValue(User.class));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }
}
