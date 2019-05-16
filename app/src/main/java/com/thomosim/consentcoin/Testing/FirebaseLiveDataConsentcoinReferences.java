package com.thomosim.consentcoin.Testing;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thomosim.consentcoin.Persistence.ConsentcoinReference;

import java.util.ArrayList;

public class FirebaseLiveDataConsentcoinReferences extends LiveData<ArrayList<ConsentcoinReference>> {
    private DatabaseReference databaseReference;
    private MyValueEventListener myValueEventListener;

    public FirebaseLiveDataConsentcoinReferences(DatabaseReference databaseReference) {
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
            ArrayList<ConsentcoinReference> consentcoinReferences = new ArrayList<>();
            for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                consentcoinReferences.add(dataSnapshotChild.getValue(ConsentcoinReference.class));
            }
            setValue(consentcoinReferences);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }
}
