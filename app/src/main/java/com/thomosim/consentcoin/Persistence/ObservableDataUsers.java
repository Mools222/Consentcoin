package com.thomosim.consentcoin.Persistence;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thomosim.consentcoin.ObserverPattern.MyObservable;

import java.util.ArrayList;

public class ObservableDataUsers extends MyObservable<ArrayList<User>> {
    private DatabaseReference databaseReference;
    private MyValueEventListener myValueEventListener;

    public ObservableDataUsers(DatabaseReference databaseReference) {
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
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            ArrayList<User> users = new ArrayList<>();
            for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                users.add(dataSnapshotChild.getValue(User.class));
            }
            setValue(users);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }
}
