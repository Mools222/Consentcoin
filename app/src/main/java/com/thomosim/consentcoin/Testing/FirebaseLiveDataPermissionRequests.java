package com.thomosim.consentcoin.Testing;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thomosim.consentcoin.Persistence.PermissionRequest;

import java.util.ArrayList;

public class FirebaseLiveDataPermissionRequests extends LiveData<ArrayList<PermissionRequest>> {
    private DatabaseReference databaseReference;
    private MyValueEventListener myValueEventListener;

    public FirebaseLiveDataPermissionRequests(DatabaseReference databaseReference) {
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
            ArrayList<PermissionRequest> permissionRequests = new ArrayList<>();
            for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                permissionRequests.add(dataSnapshotChild.getValue(PermissionRequest.class));
            }
            setValue(permissionRequests);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }
}
