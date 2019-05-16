package com.thomosim.consentcoin.Testing;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.thomosim.consentcoin.Persistence.User;

public class FirebaseLiveDataUser extends LiveData<User> {
    public FirebaseLiveDataUser(DatabaseReference databaseReference) {
        MyValueEventListener myValueEventListener = new MyValueEventListener();
        databaseReference.addValueEventListener(myValueEventListener);
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            setValue(dataSnapshot.getValue(User.class));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }
}
