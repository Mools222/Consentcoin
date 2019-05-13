package com.thomosim.consentcoin.Persistens;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DAO {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    public void invite(ArrayList<String> members, final String organization){

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("group");

        for (String uid:members) {

            final String userId= uid;

            databaseReference.child(organization)
                    .child("members")
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                databaseReference.child(organization).child("members").push().setValue(userId);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        }


    }
}
