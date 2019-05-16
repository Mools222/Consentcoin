package com.thomosim.consentcoin.Testing;

import android.content.Context;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thomosim.consentcoin.AdapterCreateRequest;
import com.thomosim.consentcoin.AdapterProcessRequest;
import com.thomosim.consentcoin.Persistence.Consentcoin;
import com.thomosim.consentcoin.Persistence.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.PermissionRequest;
import com.thomosim.consentcoin.Persistence.User;
import com.thomosim.consentcoin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FirebaseUtilities {
    private DatabaseReference databaseReferenceTest;
    private DatabaseReference databaseReferenceContractReferences;
    private DatabaseReference databaseReferencePermissionRequests;
    private DatabaseReference databaseReferenceUsers;
    private ChildEventListener childEventListenerTest;
    private ChildEventListener childEventListenerContractReferences;
    private ChildEventListener childEventListenerPermissionRequests;
    private ValueEventListener valueEventListenerCurrentUser;
    private ValueEventListener valueEventListenerAllUsers;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private String userDisplayName;
    private String userEmail;
    private String uid;
    private User user;
    private int chosenUserType;
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_PROCESS_REQUEST = 2;
    private boolean sendRequestToAllMembers;
    private ArrayList<ConsentcoinReference> consentcoinReferences;
    private ArrayList<Consentcoin> consentcoins;
    private ArrayList<PermissionRequest> pendingPermissionRequests;
    private ArrayList<User> organizations;
    private ArrayList<User> members;
    private ArrayList<User> users;
    private AdapterProcessRequest adapterProcessRequest;
    private AdapterCreateRequest adapterCreateRequest;

    private final Context CONTEXT;

    private static final Object LOCK = new Object();
    private static FirebaseUtilities instance;

    public static FirebaseUtilities getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new FirebaseUtilities(context);
            }
        }
        return instance;
    }

    public FirebaseUtilities(Context context) {
        CONTEXT = context;
        storageReference = FirebaseStorage.getInstance().getReference().child("consentcoins");
        databaseReferenceTest = FirebaseDatabase.getInstance().getReference().child("test");
        databaseReferenceContractReferences = FirebaseDatabase.getInstance().getReference().child("ConsentcoinReferences");
        databaseReferencePermissionRequests = FirebaseDatabase.getInstance().getReference().child("PermissionRequests");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) { // User is signed in
                    user = null; // Set the user to null to avoid using the user details of a different user, who was logged in on the same device

                    userDisplayName = firebaseUser.getDisplayName();
                    userEmail = firebaseUser.getEmail();
                    uid = firebaseUser.getUid();

                    addDatabaseListener();
                } else { // User is signed out
                    removeDatabaseListener();
                    userEmail = null;
                    uid = null; // This value is used in removeDatabaseListener(), so it is set to null after this method is done
                    userDisplayName = null;

                    ((MainActivity2) CONTEXT).startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
//                                    .setIsSmartLockEnabled(false) // Doesn't seem to do anything
                                    .setTheme(R.style.LightTheme)
                                    .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build())) // Additional sign-in providers can be added here. See: https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
                                    .build(),
                            REQUEST_CODE_SIGN_IN);
                }
            }
        };
    }

    public DatabaseReference getDatabaseReferenceCurrentUser() {
        return databaseReferenceUsers.child(firebaseAuth.getCurrentUser().getUid());
    }

    public void changeUser() {
        Random random = new Random();
        user.setFirstName(String.valueOf(random.nextInt(500)));
        databaseReferenceUsers.child(uid).setValue(user);
    }

    public void addAuthStateListener() {
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public void removeAuthStateListener() {
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    public void addDatabaseListener() {
        if (valueEventListenerCurrentUser == null) {
            valueEventListenerCurrentUser = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);

//                    if (user == null) {
//                        chooseUserType();
//                    } else {
//                        tvNavigationHeaderName.setText(userDisplayName);
//                        tvNavigationHeaderEmail.setText(userEmail);
//
//                        if (user.getType().equals("Member")) {
//                            menuItemPendingRequests.setVisible(true); // Members can receive, but not create requests
//                            menuItemCreateRequest.setVisible(false);
//                            tvNavigationDrawerCounter.setVisibility(View.VISIBLE);
//                            menuItemAddOrganization.setVisible(true); // Members can only add and view their organizations
//                            menuItemAddMember.setVisible(false);
//                            menuItemMyOrganizations.setVisible(true);
//                            menuItemMyMembers.setVisible(false);
//                        } else if (user.getType().equals("Organization")) {
//                            menuItemPendingRequests.setVisible(false); // Organizations can create, but not receive requests
//                            menuItemCreateRequest.setVisible(true);
//                            tvNavigationDrawerCounter.setVisibility(View.GONE);
//                            menuItemAddOrganization.setVisible(false); // Organizations can only add and view their members
//                            menuItemAddMember.setVisible(true);
//                            menuItemMyOrganizations.setVisible(false);
//                            menuItemMyMembers.setVisible(true);
//                        }
//                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };

            databaseReferenceUsers.child(uid).addValueEventListener(valueEventListenerCurrentUser);
        }

        if (valueEventListenerAllUsers == null) {
            valueEventListenerAllUsers = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    users = new ArrayList<>();

                    for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                        users.add(dataSnapshotChild.getValue(User.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };

            databaseReferenceUsers.addValueEventListener(valueEventListenerAllUsers);
        }

//        if (childEventListenerTest == null) {
//            textView1.setText("");
//
//            childEventListenerTest = new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    textView1.append("Key: " + dataSnapshot.getKey() + "   Value: " + dataSnapshot.getValue() + "\n");
//                }
//
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                }
//
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                }
//
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                }
//
//                public void onCancelled(DatabaseError databaseError) {
//                }
//            };
//
//            databaseReferenceTest.addChildEventListener(childEventListenerTest);
//        }

        if (childEventListenerContractReferences == null) {
            consentcoinReferences = new ArrayList<>();

            childEventListenerContractReferences = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ConsentcoinReference consentcoinReference = dataSnapshot.getValue(ConsentcoinReference.class);
                    consentcoinReferences.add(consentcoinReference);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };

            databaseReferenceContractReferences.addChildEventListener(childEventListenerContractReferences);
        }

//        if (childEventListenerPermissionRequests == null) {
//            pendingPermissionRequests = new ArrayList<>();
//
//            childEventListenerPermissionRequests = new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    PermissionRequest permissionRequest = dataSnapshot.getValue(PermissionRequest.class);
//
//                    if (permissionRequest.getMember().equals(userEmail)) {
//                        pendingPermissionRequests.add(permissionRequest);
//                        tvNavigationDrawerCounter.setText(String.valueOf(pendingPermissionRequests.size()));
//                        tvNavigationDrawerPendingPermissionsCounter.setText(String.valueOf(pendingPermissionRequests.size()));
////                        Toast.makeText(getApplicationContext(), "Pending request detected", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                }
//
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                }
//
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                }
//
//                public void onCancelled(DatabaseError databaseError) {
//                }
//            };
//
//            databaseReferencePermissionRequests.addChildEventListener(childEventListenerPermissionRequests);
//        }

    }

//    public class FirebaseLiveDataUser extends LiveData<User> {
//        public FirebaseLiveDataUser() {
//            MyValueEventListener listener = new MyValueEventListener();
//            databaseReferenceUsers.child(uid).addValueEventListener(listener);
//        }
//
//        private class MyValueEventListener implements ValueEventListener {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                user = dataSnapshot.getValue(User.class);
//                setValue(user);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        }
//    }

    public void removeDatabaseListener() {
        if (childEventListenerTest != null) {
            databaseReferenceTest.removeEventListener(childEventListenerTest);
            childEventListenerTest = null;
        }

        if (childEventListenerContractReferences != null) {
            databaseReferenceContractReferences.removeEventListener(childEventListenerContractReferences);
            childEventListenerContractReferences = null;
        }

        if (childEventListenerPermissionRequests != null) {
            databaseReferencePermissionRequests.removeEventListener(childEventListenerPermissionRequests);
            childEventListenerPermissionRequests = null;
        }

        if (valueEventListenerCurrentUser != null) {
            databaseReferenceUsers.child(uid).removeEventListener(valueEventListenerCurrentUser);
            valueEventListenerCurrentUser = null;
        }

        if (valueEventListenerAllUsers != null) {
            databaseReferenceUsers.removeEventListener(valueEventListenerAllUsers);
            valueEventListenerAllUsers = null;
        }
    }

    public void write(String test) {
        databaseReferenceTest.push().setValue(test);
    }


    public void signOut() {
        AuthUI.getInstance().signOut(CONTEXT);
    }

    public void chooseUserType(String userType) {
        user = new User(uid, userEmail, userType, "FirstName", null, "LastName", null);
        if (userDisplayName != null) {
            String[] userNameSplit = userDisplayName.split("\\s");
            if (userNameSplit.length == 2)
                user = new User(uid, userEmail, userType, userNameSplit[0], null, userNameSplit[1], null);
            else if (userNameSplit.length == 3)
                user = new User(uid, userEmail, userType, userNameSplit[0], userNameSplit[1], userNameSplit[2], null);
        }
        databaseReferenceUsers.child(uid).setValue(user);

    }

}
