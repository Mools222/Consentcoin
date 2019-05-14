package com.thomosim.consentcoin.Persistence;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DAO {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference inviteRequestDatabaseReference;

    private String inviteID;


    public void invite(ArrayList<String> members, final String organization) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("inviteRequests");


        for (String uid : members) {

            inviteRequestDatabaseReference = databaseReference.push();
            inviteID = inviteRequestDatabaseReference.getKey();
            final String userId = uid;

            InviteRequest inviteRequest = new InviteRequest(inviteID, organization, uid);
            inviteRequestDatabaseReference.setValue(inviteRequest);

        }


    }
}
