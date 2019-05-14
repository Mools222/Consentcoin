package com.thomosim.consentcoin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
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
import com.google.firebase.storage.UploadTask;
import com.thomosim.consentcoin.Persistens.Consentcoin;
import com.thomosim.consentcoin.Persistens.ContractReference;
import com.thomosim.consentcoin.Persistens.DAO;
import com.thomosim.consentcoin.Persistens.PermissionRequest;
import com.thomosim.consentcoin.Persistens.User;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference databaseReferenceTest;
    private DatabaseReference databaseReferenceContractReferences;
    private DatabaseReference databaseReferencePermissionRequests;
    private DatabaseReference databaseReferenceUserTypes;
    private ChildEventListener childEventListenerTest;
    private ChildEventListener childEventListenerContractReferences;
    private ChildEventListener childEventListenerPermissionRequests;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private TextInputEditText textInputEditText;
    private TextView textView1;
    private TextView textView2;
    private TextView tvNavigationDrawerCounter;
    private TextView tvNavigationDrawerPendingPermissionsCounter;
    private MenuItem menuItemPendingRequests;
    private MenuItem menuItemCreateRequest;

    private String userEmail;
    private String uid;
    private String userType;
    private int chosenUserType;
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_PROCESS_REQUEST = 2;
    private boolean sendRequestToAllMembers;
    private ArrayList<ContractReference> contractReferences;
    private ArrayList<Consentcoin> consentcoins;
    private ArrayList<PermissionRequest> pendingPermissionRequests;
    private ArrayList<User> users;
    private ArrayList<String> members;
    private AdapterProcessRequest adapterProcessRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up the Navigation View
        // https://material.io/develop/android/components/navigation-view/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize references to views
        textView1 = findViewById(R.id.textView1);
        textInputEditText = findViewById(R.id.textInputEditText);
        textView2 = findViewById(R.id.textView2);
        tvNavigationDrawerCounter = findViewById(R.id.tv_navigation_drawer_count); // This is the counter in the app bar on top of button that opens the Navigation Drawer
        tvNavigationDrawerPendingPermissionsCounter = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_requests).getActionView(); // This is the counter inside the Navigation Drawer menu next to the "Pending requests" button
        tvNavigationDrawerPendingPermissionsCounter.setGravity(Gravity.CENTER_VERTICAL);
        tvNavigationDrawerPendingPermissionsCounter.setTypeface(null, Typeface.BOLD);
        tvNavigationDrawerPendingPermissionsCounter.setTextColor(getResources().getColor(R.color.colorRed));
//        tvNavigationDrawerPendingPermissionsCounter.setBackground(getResources().getDrawable(R.drawable.navigation_drawer_counter_red_circle));
        tvNavigationDrawerPendingPermissionsCounter.setText("0");

        menuItemPendingRequests = navigationView.getMenu().findItem(R.id.nav_pending_requests);
        menuItemCreateRequest = navigationView.getMenu().findItem(R.id.nav_create_request);

        // Initialize ArrayLists
        contractReferences = new ArrayList<>();
        consentcoins = new ArrayList<>();
        pendingPermissionRequests = new ArrayList<>();
        users = new ArrayList<>();
        members = new ArrayList<>();

        // Initialize Firebase components
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("consentcoins");
        databaseReferenceTest = FirebaseDatabase.getInstance().getReference().child("test");
        databaseReferenceContractReferences = FirebaseDatabase.getInstance().getReference().child("ContractReferences");
        databaseReferencePermissionRequests = FirebaseDatabase.getInstance().getReference().child("PermissionRequests");
        databaseReferenceUserTypes = FirebaseDatabase.getInstance().getReference().child("UserTypes");
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    userType = null;

                    // User is signed in
                    runOnSignIn(firebaseUser.getEmail(), firebaseUser.getUid());
//                    Toast.makeText(MainActivity.this, "You're now signed in. Welcome to FriendlyChat. " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this, firebaseUser.getMetadata().getCreationTimestamp() + " " + firebaseUser.getMetadata().getLastSignInTimestamp(), Toast.LENGTH_SHORT).show();

                    // Check to see if it is the first time the user is signing in
                    // https://stackoverflow.com/questions/51577039/check-if-the-user-has-signed-in-for-the-first-time-or-not-using-firebase-authui
//                    if (firebaseUser.getMetadata().getCreationTimestamp() == firebaseUser.getMetadata().getLastSignInTimestamp()) {
//                        chooseUserType();
//                    }

//                    Toast.makeText(getApplicationContext(), firebaseUser.getUid() + " uid", Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    runOnSignOut();

                    startActivityForResult(
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

    /**
     * This method handles the back button
     */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) { // If the Navigation Drawer is open and the back button is pressed, close the Navigation Drawer instead of the app
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * This method handles the inputs to the Navigation Drawer
     */

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pending_requests) {
            processRequest();
        } else if (id == R.id.nav_create_request) {
            createRequest();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This method inflates the menu on the right side of the action bar
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // Notification Badge - https://www.youtube.com/watch?v=1AxVtMo7FfY

        return true;
    }

    /**
     * This method handles the inputs to the menu on the right side of the action bar
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                            }
                        });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void write(View view) {
        String test = textInputEditText.getText().toString();
        databaseReferenceTest.push().setValue(test);
        textInputEditText.setText("");
    }

    public void processRequest() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_process_request, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_process_request);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapterProcessRequest = new AdapterProcessRequest(pendingPermissionRequests, this);
        recyclerView.setAdapter(adapterProcessRequest);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Process request(s)")
                .setView(dialogView)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    // https://www.dev2qa.com/android-custom-listview-with-checkbox-example/
    // http://android-coding.blogspot.com/2011/09/listview-with-multiple-choice.html
    public void createRequest() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_request, null);

        final String[] emails = {"a@a.aa", "z@z.zz", "f@f.ff", "c@c.cc", "b@b.bb", "tt@t.tt"};

        sendRequestToAllMembers = true;

        final ListView listView = dialogView.findViewById(R.id.list_create_request);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, emails);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
        listView.setVisibility(View.GONE);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_first) {
                    listView.setVisibility(View.GONE);
                    sendRequestToAllMembers = true;
                } else if (checkedId == R.id.rb_second) {
                    listView.setVisibility(View.VISIBLE);
                    sendRequestToAllMembers = false;
                }
            }
        });

        new MaterialAlertDialogBuilder(this)
                .setTitle("Create request(s)")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (sendRequestToAllMembers) {
                            for (int i = 0; i < emails.length; i++) {
                                DatabaseReference databaseReference = databaseReferencePermissionRequests.push(); // Creates blank record in the database
                                String firebaseId = databaseReference.getKey(); // Get the auto generated key
                                PermissionRequest permissionRequest = new PermissionRequest(firebaseId, userEmail, emails[i], "P1");
                                databaseReference.setValue(permissionRequest);
                            }
                        } else {
                            int cntChoice = listView.getCount();
                            System.out.println("cnt " + cntChoice);
                            SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
                            for (int i = 0; i < cntChoice; i++) {
                                if (sparseBooleanArray.get(i)) {
                                    DatabaseReference databaseReference = databaseReferencePermissionRequests.push(); // Creates blank record in the database
                                    String firebaseId = databaseReference.getKey(); // Get the auto generated key
                                    PermissionRequest permissionRequest = new PermissionRequest(firebaseId, userEmail, listView.getItemAtPosition(i).toString(), "P1");
                                    databaseReference.setValue(permissionRequest);
                                }
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    // onResume adds the AuthStateListener, which calls the runOnSignIn method (if the user is signed in), which adds the ChildEventListener. Therefore the onPause method should remove both the AuthStateListener and ChildEventListener, so they are not added multiple times when the onResume method is called
    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
//        mMessageAdapter.clear();
        removeDatabaseListener();
    }

    // For some reason it causes an UserCancellationException when the back button is pressed at the sign in screen, but it has no effect. The "Sign in canceled" Toast still works
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == REQUEST_CODE_PROCESS_REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Permission decided!", Toast.LENGTH_SHORT).show();

                if (data.hasExtra("BOOLEAN") && data.hasExtra("POS")) {
                    System.out.println(data.getBooleanExtra("BOOLEAN", false));

                    if (data.getBooleanExtra("BOOLEAN", false)) { // If the user chooses to give permission, do the following:

                        PermissionRequest permissionRequest = pendingPermissionRequests.get(data.getIntExtra("POS", -1)); // Get the position from the returned intent

                        createConsentcoin(permissionRequest.getId(), permissionRequest.getPermissionType(), permissionRequest.getOrganization(), permissionRequest.getMember()); // Create a Consentcoin

                        databaseReferencePermissionRequests.child(permissionRequest.getId()).removeValue(); // Remove the permission request from the database
                        pendingPermissionRequests.remove(permissionRequest); // Remove the permission request from the ArrayList
                        adapterProcessRequest.updateData(pendingPermissionRequests);
                    }
                } else if (data.hasExtra("LATER"))
                    System.out.println("Decide later");

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Permission not yet decided!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void runOnSignIn(String userEmail, String uid) {
        this.userEmail = userEmail;
        this.uid = uid;
        addDatabaseListener();
    }

    private void runOnSignOut() {
        userEmail = null;
//        mMessageAdapter.clear();
        removeDatabaseListener();
    }

    // Bug: Every listener, except childEventListenerContractReferences, is added every time the onResume method is called
    public void addDatabaseListener() {
        childEventListenerTest = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                textView1.append("Key: " + dataSnapshot.getKey() + "   Value: " + dataSnapshot.getValue() + "\n");
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

        databaseReferenceTest.addChildEventListener(childEventListenerTest);

        if (childEventListenerContractReferences == null) {
            childEventListenerContractReferences = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ContractReference contractReference = dataSnapshot.getValue(ContractReference.class);
                    contractReferences.add(contractReference);
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

        childEventListenerPermissionRequests = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PermissionRequest permissionRequest = dataSnapshot.getValue(PermissionRequest.class);

                if (permissionRequest.getMember().equals(userEmail)) {
                    pendingPermissionRequests.add(permissionRequest);
                    tvNavigationDrawerCounter.setText(String.valueOf(pendingPermissionRequests.size()));
                    tvNavigationDrawerPendingPermissionsCounter.setText(String.valueOf(pendingPermissionRequests.size()));
                    Toast.makeText(getApplicationContext(), "Pending request detected", Toast.LENGTH_SHORT).show();
                }
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

        databaseReferencePermissionRequests.addChildEventListener(childEventListenerPermissionRequests);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userType = (String) dataSnapshot.getValue();
//                Toast.makeText(getApplicationContext(), userType + " type", Toast.LENGTH_SHORT).show();

                if (userType == null)
                    chooseUserType();
                else if (userType.equals("Member")) { // Members can receive, but not create requests
                    menuItemPendingRequests.setVisible(true);
                    menuItemCreateRequest.setVisible(false);
                } else if (userType.equals("Organization")) { // Organizations can create, but not receive requests
                    menuItemPendingRequests.setVisible(false);
                    menuItemCreateRequest.setVisible(true);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReferenceUserTypes.child(uid).addValueEventListener(valueEventListener);

    }

    private void removeDatabaseListener() {
        if (childEventListenerContractReferences != null) {
            databaseReferenceContractReferences.removeEventListener(childEventListenerContractReferences);
            childEventListenerContractReferences = null;
        }
    }

    /**
     * This method:
     * 1) Creates a new instance of the Consentcoin class
     * 2) Creates a new file in the phone's internal storage called "contract.dat". The getFilesDir method returns the appropriate internal directory for the app
     * 3) Opens a new ObjectOutputStream object and writes the Consentcoin object to the file. The ObjectOutputStream is then closed.
     * The openFileOutput method opens a private file associated with this Context's application package for writing. By passing the argument Context.MODE_PRIVATE, the created file can only be accessed by the calling application.
     * 4) A new StorageReference object is created. The ensures that the file is saved in the "consentcoins" folder in Firebase Storage under the file name "[contractId].dat"
     * 5) The file is persisted via the putFile method, which requires a URI object. The static fromFile method from the Uri class creates a URI from the File object.
     * 6) The putFile method returns a UploadTask object. Using the addOnSuccessListener method, an OnSuccessListener is added to the UploadTask object.
     * This is accomplished by creating an anonymous inner class, which implements the onSuccess method. Since OnSuccessListener is a generic class, the formal generic type
     * "TResult" is replaced by the inner class TaskSnapshot, which is found in the UploadTask class.
     * 7) A new Task object is created. The formal generic type "TResult" is replaced by the Uri class. Since TaskSnapshot extends the StorageTask class, it inherits the getStorage method.
     * The method returns a StorageReference object upon which the getDownloadUrl method is called. This method returns a Task<Uri> object. The getDownloadUrl method is called on the
     * Task<Uri> object. This method returns a Task<Uri> containing the the download URL for the persisted file.
     * 8) A while loop with an empty body is created to keep the thread waiting until the getDownloadUrl method is successful.
     * 9) A new Uri object is created by calling the getResult method on the Task<Uri> object.
     * 10) A new ContractReference object is created using the contract ID, member ID and organization ID of the Consentcoin object and the download URL.
     * 11) The ContractReference object is persisted to the Firebase Realtime Database using the push and setValue methods.
     * 12) Finally the contract.dat file is deleted from the storage of the phone.
     */

    public void createConsentcoin(String id, String contractType, String organization, String member) {
        // TODO (2) Encrypt the Consentcoin object
//        final Consentcoin consentcoin = new Consentcoin("5", "Type 1", "TestOrg", "TestMember");
        final Consentcoin consentcoin = new Consentcoin(id, contractType, organization, member);

        String fileName = "consentcoin.dat";
        final File file = new File(getFilesDir(), fileName);
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(openFileOutput(fileName, Context.MODE_PRIVATE));
            objectOutputStream.writeObject(consentcoin);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StorageReference storageReference = this.storageReference.child(id);

        storageReference.putFile(Uri.fromFile(file)).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();

                ContractReference contractReference = new ContractReference(consentcoin.getContractId(), consentcoin.getMemberId(), consentcoin.getOrganizationId(), downloadUrl.toString());
                databaseReferenceContractReferences.push().setValue(contractReference);

                file.delete();
            }
        });
    }

    /**
     * This method:
     * 1)
     */

    public void readObject(View view) {
        textView2.setText("Loading...");

        if (contractReferences.size() > 0) {
            try {
                // If we start one AsyncTask for sendRequestToAllMembers downloads
                URL[] urls = new URL[contractReferences.size()];

                for (int i = 0; i < contractReferences.size(); i++) {
                    urls[i] = new URL(contractReferences.get(i).getStorageUrl());
                }

                consentcoins.clear();
                new DownloadObjects().execute(urls);

                // If we start one AsyncTask per download
//                for (int i = 0; i < contractReferences.size(); i++) {
//                    new DownloadObjects().execute(new URL(contractReferences.get(i).getStorageUrl()));
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public void chooseUserType() {
        final String[] array = {"Member", "Organization"};

        chosenUserType = 0; // Must be set to 0, since the OnClickListener in setSingleChoiceItems only fires if you actually click something

        new MaterialAlertDialogBuilder(this)
                .setTitle("Choose user type")
//                .setMessage("Message")
                .setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        String currentItem = array[which];
//                        Toast.makeText(getApplicationContext(), currentItem + " checked", Toast.LENGTH_SHORT).show();

                        chosenUserType = which;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        String currentItem = array[chosenUserType];
//                        Toast.makeText(getApplicationContext(), currentItem + " chosen", Toast.LENGTH_SHORT).show();

                        userType = array[chosenUserType];
                        databaseReferenceUserTypes.child(uid).setValue(userType);
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void invite(View view) {
        DAO dao = new DAO();
        ArrayList<String> members = new ArrayList<>();
        members.add("bob");
        members.add("lis");
        String organization;
        if(firebaseAuth.getUid() != null) {
            organization = firebaseAuth.getUid();
        } else{
            organization = "testOrg";
        }
        dao.invite(members,organization);
    }

    // To solve the "leaks might occur" warning: https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur/46166223#46166223
    private class DownloadObjects extends AsyncTask<URL, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(URL... urls) {
            try {
                ObjectInputStream objectInputStream = null;
                HttpURLConnection.setFollowRedirects(false);
                for (int i = 0; i < urls.length; i++) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) urls[i].openConnection();
                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        objectInputStream = new ObjectInputStream(new BufferedInputStream(urls[i].openStream()));
                        // TODO (3) Decrypt the Consentcoin object
                        Consentcoin consentcoin = (Consentcoin) objectInputStream.readObject();
                        consentcoins.add(consentcoin);
                    }
                }
                objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

//        @Override
//        protected Void doInBackground(URL... urls) {
//            try {
//                ObjectInputStream objectInputStream = null;
//                objectInputStream = new ObjectInputStream(new BufferedInputStream(urls[0].openStream()));
//                // TODO (3) Decrypt the Consentcoin object
//                Consentcoin contract = (Consentcoin) objectInputStream.readObject();
//                consentcoins.add(contract);
//                objectInputStream.close();
//            } catch (FileNotFoundException e) {
//                System.out.println("FileNotFoundException");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            textView2.setText("");
            for (Consentcoin consentcoin : consentcoins) {
                textView2.append("ID: " + consentcoin.getContractId() + " Type: " + consentcoin.getContractType() + " MemID: " + consentcoin.getMemberId() + " OrgID: " + consentcoin.getOrganizationId() + "\n");
            }
        }
    }

}
