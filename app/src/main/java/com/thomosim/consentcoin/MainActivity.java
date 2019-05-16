package com.thomosim.consentcoin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thomosim.consentcoin.Persistence.Consentcoin;
import com.thomosim.consentcoin.Persistence.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.DAO;
import com.thomosim.consentcoin.Persistence.InviteRequest;
import com.thomosim.consentcoin.Persistence.PermissionRequest;
import com.thomosim.consentcoin.Persistence.User;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;

// TODO (99) Find ud af hvorfor der står "uses or overrides a deprecated API" når den bygger MainActivity.java. Hvilken API taler den om?
// TODO (98) Make all named constants (keyword final) uppercase
// TODO (50) Use tasks to make sure the listeners are done reading the data before moving on (https://stackoverflow.com/questions/38966056/android-wait-for-firebase-valueeventlistener/40594607)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference databaseReferenceTest;
    private DatabaseReference databaseReferenceContractReferences;
    private DatabaseReference databaseReferencePermissionRequests;
    private DatabaseReference databaseReferenceInviteRequests;
    private DatabaseReference databaseReferenceUsers;
    private ChildEventListener childEventListenerTest;
    private ChildEventListener childEventListenerContractReferences;
    private ChildEventListener childEventListenerPermissionRequests;
    private ChildEventListener childEventListenerInviteRequests;
    private ValueEventListener valueEventListenerCurrentUser;
    private ValueEventListener valueEventListenerAllUsers;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private TextInputEditText textInputEditText;
    private TextView textView1;
    private TextView tvNavigationHeaderName, tvNavigationHeaderEmail;
    private TextView tvNavigationDrawerCounter;
    private TextView tvNavigationDrawerPendingPermissionsCounter;
    private TextView tvNavigationDrawerPendingInviteCounter;
    private MenuItem menuItemPendingRequests, menuItemCreateRequest, menuItemMyPermissions, menuItemInvite, menuItemAddOrganization, menuItemAddMember, menuItemMyOrganizations, menuItemMyMembers;

    private TextInputEditText tietInviteMember;
    private ArrayList<String> inviteMemberList;
    private ListView listMember;
    ArrayAdapter<String> inviteMemberAdapter;

    private String userDisplayName;
    private String userEmail;
    private String uid;
    private User user;
    private int chosenUserType;
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_PROCESS_REQUEST = 2;
    private static final int REQUEST_CODE_MY_CONSENTCOINS = 3;
    private static final int REQUEST_CODE_PROCESS_INVITE = 4;
    private boolean sendRequestToAllMembers;
    private ArrayList<ConsentcoinReference> consentcoinReferences;
    private ArrayList<Consentcoin> consentcoins;
    private ArrayList<PermissionRequest> pendingPermissionRequests;
    private ArrayList<InviteRequest> pendingInviteRequests;
    private ArrayList<User> organizations;
    private ArrayList<User> members;
    private ArrayList<User> users;
    private AdapterProcessRequest adapterProcessRequest;
    private AdapterCreateRequest adapterCreateRequest;
    private AdapterProcessInvite adapterProcessInvite;


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
        View navigationDrawerHeader = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize references to views
        textView1 = findViewById(R.id.textView1);
        textInputEditText = findViewById(R.id.textInputEditText);
        tvNavigationDrawerCounter = findViewById(R.id.tv_navigation_drawer_count); // This is the counter in the app bar on top of button that opens the Navigation Drawer
        tvNavigationDrawerPendingPermissionsCounter = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_requests).getActionView(); // This is the counter inside the Navigation Drawer menu next to the "Pending requests" button
        tvNavigationDrawerPendingPermissionsCounter.setGravity(Gravity.CENTER_VERTICAL);
        tvNavigationDrawerPendingPermissionsCounter.setTypeface(null, Typeface.BOLD);
        tvNavigationDrawerPendingPermissionsCounter.setTextColor(getResources().getColor(R.color.colorRed));
//        tvNavigationDrawerPendingPermissionsCounter.setBackground(getResources().getDrawable(R.drawable.navigation_drawer_counter_red_circle));
        tvNavigationDrawerPendingPermissionsCounter.setText("0");
        tvNavigationHeaderName = navigationDrawerHeader.findViewById(R.id.tv_navigation_header_name);
        tvNavigationHeaderEmail = navigationDrawerHeader.findViewById(R.id.tv_navigation_header_email);
        tvNavigationDrawerPendingInviteCounter = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_invites).getActionView();
        tvNavigationDrawerPendingInviteCounter.setGravity(Gravity.CENTER_VERTICAL);
        tvNavigationDrawerPendingInviteCounter.setTypeface(null, Typeface.BOLD);
        tvNavigationDrawerPendingInviteCounter.setTextColor(getResources().getColor(R.color.colorRed));
        tvNavigationDrawerPendingInviteCounter.setText("0");

        menuItemPendingRequests = navigationView.getMenu().findItem(R.id.nav_pending_requests);
        menuItemCreateRequest = navigationView.getMenu().findItem(R.id.nav_create_request);
        menuItemMyPermissions = navigationView.getMenu().findItem(R.id.nav_my_consentcoins);
        menuItemInvite = navigationView.getMenu().findItem(R.id.nav_invite);
        menuItemAddOrganization = navigationView.getMenu().findItem(R.id.nav_add_organization);
        menuItemAddMember = navigationView.getMenu().findItem(R.id.nav_add_member);
        menuItemMyOrganizations = navigationView.getMenu().findItem(R.id.nav_my_organizations);
        menuItemMyMembers = navigationView.getMenu().findItem(R.id.nav_my_members);

        // Initialize ArrayLists
        consentcoinReferences = new ArrayList<>();
        consentcoins = new ArrayList<>();
        pendingPermissionRequests = new ArrayList<>();

        // Initialize Firebase components
        storageReference = FirebaseStorage.getInstance().getReference().child("consentcoins");
        databaseReferenceTest = FirebaseDatabase.getInstance().getReference().child("test");
        databaseReferenceContractReferences = FirebaseDatabase.getInstance().getReference().child("ConsentcoinReferences");
        databaseReferencePermissionRequests = FirebaseDatabase.getInstance().getReference().child("PermissionRequests");
        databaseReferenceInviteRequests = FirebaseDatabase.getInstance().getReference().child("InviteRequests");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    user = null; // Set the user to null to avoid using the user details of a different user, who was logged in on the same device

                    userDisplayName = firebaseUser.getDisplayName();
                    userEmail = firebaseUser.getEmail();
                    uid = firebaseUser.getUid();

                    // User is signed in
                    runOnSignIn();
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

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    // onResume adds the AuthStateListener, which calls the runOnSignIn method (if the user is signed in), which adds the different EventListeners. Therefore the onPause method should remove both the AuthStateListener and EventListeners, so they are not added multiple times when the onResume method is called
    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
//        mMessageAdapter.clear();
        removeDatabaseListener();
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
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        } else if (id == R.id.nav_my_consentcoins) {
//            myConsentcoins();
            displayConsentcoins();
        } else if (id == R.id.nav_pending_invites) {
            processInvites();
        } else if (id == R.id.nav_invite) {
            invite();
        } else if (id == R.id.nav_add_organization) {
            addOrganizationOrMember("organization");
        } else if (id == R.id.nav_add_member) {
            addOrganizationOrMember("member");
        } else if (id == R.id.nav_my_organizations) {
            myOrganizationsOrMembers("organizations");
        } else if (id == R.id.nav_my_members) {
            myOrganizationsOrMembers("members");
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This method handles results from other activities
     */

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
                if (data.hasExtra("BOOLEAN") && data.hasExtra("POS")) {

                    boolean permissionGranted = data.getBooleanExtra("BOOLEAN", false);
                    PermissionRequest permissionRequest = pendingPermissionRequests.get(data.getIntExtra("POS", -1)); // Get the position from the returned intent

                    if (permissionGranted) {
                        Toast.makeText(this, "Permission given", Toast.LENGTH_SHORT).show();
                        createConsentcoin(permissionRequest.getId(), permissionRequest.getPermissionType(), permissionRequest.getOrganization(), permissionRequest.getMember()); // If the user chooses to give permission, create a Consentcoin
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }

                    databaseReferencePermissionRequests.child(permissionRequest.getId()).removeValue(); // Remove the permission request from the database
                    pendingPermissionRequests.remove(permissionRequest); // Remove the permission request from the ArrayList
                    adapterProcessRequest.updateData(pendingPermissionRequests); // Update the adapter
                    tvNavigationDrawerCounter.setText(String.valueOf(pendingPermissionRequests.size()));
                    tvNavigationDrawerPendingPermissionsCounter.setText(String.valueOf(pendingPermissionRequests.size()));
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Permission request not yet processed", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_MY_CONSENTCOINS) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Consentcoin RESULT_OK", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Consentcoin RESULT_CANCELED", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_PROCESS_INVITE) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("BOOLEAN") && data.hasExtra("POS")) {
                    InviteRequest inviteRequest = pendingInviteRequests.get(data.getIntExtra("POS",-1));

                    boolean inviteAccepted = data.getBooleanExtra("BOOLEAN", false);

                    if(inviteAccepted){

                        Toast.makeText(this, "Invite Accepted", Toast.LENGTH_SHORT).show();
                        DAO dao = new DAO();
                        dao.acceptInvite(inviteRequest,uid);


                    } else {
                        Toast.makeText(this,"Invite declined", Toast.LENGTH_SHORT).show();
                    }

                    databaseReferenceInviteRequests.child(inviteRequest.getId()).removeValue();
                    pendingInviteRequests.remove(inviteRequest);
                    adapterProcessInvite.updateData(pendingInviteRequests);
                    tvNavigationDrawerPendingInviteCounter.setText(String.valueOf(pendingInviteRequests.size()));

                }
            }
        }
    }

    public void addOrganizationOrMember(final String userType) {
        organizations = new ArrayList<>();
        members = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getType().equals("Organization") && userType.equals("organization"))
                organizations.add(user);
            else if (user.getType().equals("Member") && userType.equals("member"))
                members.add(user);
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_organization, null);
        final TextInputEditText textInputEditText = dialogView.findViewById(R.id.et_dialog_add_organization);
        final Context CONTEXT = this;
        new MaterialAlertDialogBuilder(this)
                .setTitle("Add " + userType)
                .setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean existingOrganizationOrMember = false;
//                        if (textInputEditText.getText().length() > 0)
                        String email = textInputEditText.getText().toString();
                        User organizationOrMember = null;

                        if (userType.equals("organization"))
                            for (int i = 0; i < organizations.size(); i++) {
                                organizationOrMember = organizations.get(i);
                                if (organizationOrMember.getEmail().equals(email)) {
                                    existingOrganizationOrMember = true;
                                    break;
                                }
                            }
                        else if (userType.equals("member"))
                            for (int i = 0; i < members.size(); i++) {
                                organizationOrMember = members.get(i);
                                if (organizationOrMember.getEmail().equals(email)) {
                                    existingOrganizationOrMember = true;
                                    break;
                                }
                            }

                        if (existingOrganizationOrMember) {
                            ArrayList<String> associatedUsersUids = user.getAssociatedUsersUid();
                            if (associatedUsersUids == null)
                                associatedUsersUids = new ArrayList<>();
                            if (!associatedUsersUids.contains(organizationOrMember.getUid()))
                                associatedUsersUids.add(organizationOrMember.getUid());
                            user.setAssociatedUsersUid(associatedUsersUids);
                            databaseReferenceUsers.child(uid).setValue(user);
                            Toast.makeText(CONTEXT, userType.substring(0, 1).toUpperCase() + userType.substring(1) + " added", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(CONTEXT, userType.substring(0, 1).toUpperCase() + userType.substring(1) + " does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void myOrganizationsOrMembers(String userType) {
        ArrayList<String> associatedUsersUids = user.getAssociatedUsersUid();

        if (associatedUsersUids != null) {
            String[] array = new String[associatedUsersUids.size()];
            associatedUsersUids.toArray(array);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("My " + userType)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();

            ListView listView = alertDialog.getListView();
            listView.setDivider(new ColorDrawable(getResources().getColor(R.color.colorOuterSpace)));
            listView.setDividerHeight(5);
            alertDialog.show();
        } else
            Toast.makeText(this, "You have no " + userType, Toast.LENGTH_SHORT).show();
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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()); // Creates a divider between items
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
        ArrayList<String> associatedUsersUids = user.getAssociatedUsersUid();
        if (associatedUsersUids != null) {
            final ArrayList<User> members = new ArrayList<>();
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                for (int j = 0; j < associatedUsersUids.size(); j++) {
                    if (user.getUid().equals(associatedUsersUids.get(j)))
                        members.add(user);
                }
            }

            View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_request, null);
            final RecyclerView recyclerView = dialogView.findViewById(R.id.rv_create_request);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()); // Creates a divider between items
            recyclerView.addItemDecoration(dividerItemDecoration);
            adapterCreateRequest = new AdapterCreateRequest(members);
            recyclerView.setAdapter(adapterCreateRequest);
            recyclerView.setVisibility(View.GONE);

            final ArrayList<User> membersSearch = new ArrayList<>();
            final TextInputEditText textInputEditText = dialogView.findViewById(R.id.et_create_request);
            textInputEditText.setVisibility(View.GONE);
            textInputEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    membersSearch.clear();
                    for (int i = 0; i < members.size(); i++) {
                        User member = members.get(i);
                        if (member.getEmail().contains(s))
                            membersSearch.add(member);
                        adapterCreateRequest.updateData(membersSearch);
                    }
                }
            });

            sendRequestToAllMembers = true;
            RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.rb_first) {
                        recyclerView.setVisibility(View.GONE);
                        textInputEditText.setVisibility(View.GONE);
                        sendRequestToAllMembers = true;
                    } else if (checkedId == R.id.rb_second) {
                        recyclerView.setVisibility(View.VISIBLE);
                        textInputEditText.setVisibility(View.VISIBLE);
                        sendRequestToAllMembers = false;
                    }
                }
            });

            final Context CONTEXT = this;
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Create request(s)")
                    .setView(dialogView)
                    .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sendRequestToAllMembers) { // If the organization wishes to send requests out to all of its associated members
                                for (int i = 0; i < members.size(); i++) {
                                    DatabaseReference databaseReference = databaseReferencePermissionRequests.push(); // Creates blank record in the database
                                    String firebaseId = databaseReference.getKey(); // Get the auto generated key
                                    PermissionRequest permissionRequest = new PermissionRequest(firebaseId, userEmail, members.get(i).getEmail(), "P1");
                                    databaseReference.setValue(permissionRequest);
                                }
                            } else { // If the organization wishes to send requests out to a select number of its associated members
                                ArrayList<User> checkedUsers = adapterCreateRequest.getCheckedUsers();

                                for (int i = 0; i < checkedUsers.size(); i++) {
                                    DatabaseReference databaseReference = databaseReferencePermissionRequests.push(); // Creates blank record in the database
                                    String firebaseId = databaseReference.getKey(); // Get the auto generated key
                                    PermissionRequest permissionRequest = new PermissionRequest(firebaseId, userEmail, checkedUsers.get(i).getEmail(), "P1");
                                    databaseReference.setValue(permissionRequest);
                                }
                            }
                            Toast.makeText(CONTEXT, "Request(s) sent!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        } else
            Toast.makeText(this, "Add members before creating a request", Toast.LENGTH_SHORT).show();
    }

    private void runOnSignIn() {
        addDatabaseListener();
    }

    private void runOnSignOut() {
//        mMessageAdapter.clear();
        removeDatabaseListener();
        userEmail = null;
        uid = null; // This value is used in removeDatabaseListener(), so it is set to null after this method is done
        userDisplayName = null;
    }

    public void addDatabaseListener() {
        if (valueEventListenerCurrentUser == null) {
            valueEventListenerCurrentUser = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);

                    if (user == null) {
                        chooseUserType();
                    } else {
                        tvNavigationHeaderName.setText(userDisplayName);
                        tvNavigationHeaderEmail.setText(userEmail);

                        if (user.getType().equals("Member")) {
                            menuItemPendingRequests.setVisible(true); // Members can receive, but not create requests
                            menuItemCreateRequest.setVisible(false);
                            tvNavigationDrawerCounter.setVisibility(View.VISIBLE);
                            menuItemAddOrganization.setVisible(true); // Members can only add and view their organizations
                            menuItemAddMember.setVisible(false);
                            menuItemMyOrganizations.setVisible(true);
                            menuItemMyMembers.setVisible(false);
                            menuItemInvite.setVisible(false);
                            tvNavigationDrawerPendingInviteCounter.setVisibility(View.VISIBLE);
                        } else if (user.getType().equals("Organization")) {
                            menuItemPendingRequests.setVisible(false); // Organizations can create, but not receive requests
                            menuItemCreateRequest.setVisible(true);
                            tvNavigationDrawerCounter.setVisibility(View.GONE);
                            menuItemAddOrganization.setVisible(false); // Organizations can only add and view their members
                            menuItemAddMember.setVisible(true);
                            menuItemMyOrganizations.setVisible(false);
                            menuItemMyMembers.setVisible(true);
                            menuItemInvite.setVisible(true);
                            tvNavigationDrawerPendingInviteCounter.setVisibility(View.INVISIBLE);
                        }
                    }
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

        if (childEventListenerTest == null) {
            textView1.setText("");

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
        }

        if (childEventListenerContractReferences == null) {
            consentcoinReferences = new ArrayList<>();

            childEventListenerContractReferences = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ConsentcoinReference consentcoinReference = dataSnapshot.getValue(ConsentcoinReference.class);

                    if (user.getType().equals("Member") && consentcoinReference.getMember().equals(userEmail))
                        consentcoinReferences.add(consentcoinReference);
                    else if (user.getType().equals("Organization") && consentcoinReference.getOrganization().equals(userEmail))
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

        if (childEventListenerPermissionRequests == null) {
            pendingPermissionRequests = new ArrayList<>();

            childEventListenerPermissionRequests = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    PermissionRequest permissionRequest = dataSnapshot.getValue(PermissionRequest.class);

                    if (permissionRequest.getMember().equals(userEmail)) {
                        pendingPermissionRequests.add(permissionRequest);
                        tvNavigationDrawerCounter.setText(String.valueOf(pendingPermissionRequests.size()));
                        tvNavigationDrawerPendingPermissionsCounter.setText(String.valueOf(pendingPermissionRequests.size()));
//                        Toast.makeText(getApplicationContext(), "Pending request detected", Toast.LENGTH_SHORT).show();
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
        }

        if (childEventListenerInviteRequests == null) {
            pendingInviteRequests = new ArrayList<>();

            childEventListenerInviteRequests = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    InviteRequest inviteRequest = dataSnapshot.getValue(InviteRequest.class);

                    if (inviteRequest.getMember().equals(userEmail)) {
                        pendingInviteRequests.add(inviteRequest);
                        tvNavigationDrawerPendingInviteCounter.setText(String.valueOf(pendingInviteRequests.size()));
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };

            databaseReferenceInviteRequests.addChildEventListener(childEventListenerInviteRequests);
        }

    }

    private void removeDatabaseListener() {
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

        if (childEventListenerInviteRequests != null) {
            databaseReferenceInviteRequests.removeEventListener(childEventListenerInviteRequests);
            childEventListenerInviteRequests = null;
        }
    }

    public void chooseUserType() {
        final String[] array = {"Member", "Organization"};

        chosenUserType = 0; // Must be set to 0, since the OnClickListener in setSingleChoiceItems only fires if you actually click something

        new MaterialAlertDialogBuilder(this)
                .setTitle("Choose user type")
                .setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chosenUserType = which;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userType = array[chosenUserType];
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
                })
                .setCancelable(false)
                .show();
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
     * 10) A new ConsentcoinReference object is created using the contract ID, member ID and organization ID of the Consentcoin object and the download URL.
     * 11) The ConsentcoinReference object is persisted to the Firebase Realtime Database using the push and setValue methods.
     * 12) Finally the contract.dat file is deleted from the storage of the phone.
     */

    public void createConsentcoin(String id, String contractType, String organization, String member) {
        // TODO (2) Encrypt the Consentcoin object
        final Consentcoin consentcoin = new Consentcoin(id, contractType, member, organization);

        String fileName = "consentcoin";
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

                ConsentcoinReference consentcoinReference = new ConsentcoinReference(consentcoin.getContractId(), consentcoin.getMemberId(), consentcoin.getOrganizationId(), downloadUrl.toString());
                databaseReferenceContractReferences.push().setValue(consentcoinReference);

                file.delete();
            }
        });
    }

    /**
     * This method:
     * 1)
     */

    public void myConsentcoins() {
        if (consentcoinReferences.size() > 0) {
            try {
                // If we start one AsyncTask for sendRequestToAllMembers downloads
                URL[] urls = new URL[consentcoinReferences.size()];

                for (int i = 0; i < consentcoinReferences.size(); i++) {
                    ConsentcoinReference consentcoinReference = consentcoinReferences.get(i);
                    if (consentcoinReference.getMember().equals(userEmail))
                        urls[i] = new URL(consentcoinReference.getStorageUrl());
                }

                consentcoins.clear();
                new DownloadObjects().execute(urls);

                Toast.makeText(this, "Loading Consenscoins. Please wait", Toast.LENGTH_SHORT).show();

                // If we start one AsyncTask per download
//                for (int i = 0; i < consentcoinReferences.size(); i++) {
//                    new DownloadObjects().execute(new URL(consentcoinReferences.get(i).getStorageUrl()));
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void displayConsentcoins() {
        if (consentcoinReferences != null) {
            final ArrayList<ConsentcoinReference> myConsentcoinReferences = new ArrayList<>();

            if (user.getType().equals("Member"))
                for (int i = 0; i < consentcoinReferences.size(); i++) {
                    ConsentcoinReference consentcoinReference = consentcoinReferences.get(i);
                    if (consentcoinReference.getMember().equals(userEmail))
                        myConsentcoinReferences.add(consentcoinReference);
                }
            else if (user.getType().equals("Organization"))
                for (int i = 0; i < consentcoinReferences.size(); i++) {
                    ConsentcoinReference consentcoinReference = consentcoinReferences.get(i);
                    if (consentcoinReference.getOrganization().equals(userEmail))
                        myConsentcoinReferences.add(consentcoinReference);
                }

            String[] array = new String[myConsentcoinReferences.size()];

            for (int i = 0; i < myConsentcoinReferences.size(); i++) {
                ConsentcoinReference consentcoinReference = myConsentcoinReferences.get(i);
                array[i] = "ID: " + consentcoinReference.getContractId() + " Member: " + consentcoinReference.getMember() + " Org: " + consentcoinReference.getOrganization();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);

            final Context CONTEXT = this;

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("My Consenscoins")
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(CONTEXT, MyConsentcoinsActivity.class);
                            intent.putExtra("CR", myConsentcoinReferences.get(which));
                            intent.putExtra("POS", which);
                            startActivityForResult(intent, REQUEST_CODE_MY_CONSENTCOINS);
                        }
                    })
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();

            ListView listView = alertDialog.getListView();
            listView.setDivider(new ColorDrawable(getResources().getColor(R.color.colorOuterSpace)));
            listView.setDividerHeight(5);
            alertDialog.show();
        } else
            Toast.makeText(this, "You have no Consenscoins", Toast.LENGTH_SHORT).show();
    }

    public void displayConsentcoin() {
        if (consentcoins != null) {
            String[] array = new String[consentcoins.size()];

            for (int i = 0; i < consentcoins.size(); i++) {
                Consentcoin consentcoin = consentcoins.get(i);
                array[i] = "ID: " + consentcoin.getContractId() + " Type: " + consentcoin.getContractType() + " MemID: " + consentcoin.getMemberId() + " OrgID: " + consentcoin.getOrganizationId();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("My Consenscoins")
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();

            ListView listView = alertDialog.getListView();
            listView.setDivider(new ColorDrawable(getResources().getColor(R.color.colorOuterSpace)));
            listView.setDividerHeight(5);
            alertDialog.show();
        } else
            Toast.makeText(this, "You have no Consenscoins", Toast.LENGTH_SHORT).show();

    }

    public void invite() {

        View inviteDialogView = getLayoutInflater().inflate(R.layout.dialog_create_invite, null);

        tietInviteMember = inviteDialogView.findViewById(R.id.memberEditText);
        inviteMemberList = new ArrayList<>();
        listMember = inviteDialogView.findViewById(R.id.list_member);


        inviteMemberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, inviteMemberList);
        listMember.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listMember.setAdapter(inviteMemberAdapter);
        listMember.setVisibility(View.VISIBLE);


        new MaterialAlertDialogBuilder(this)
                .setTitle("Invite member(s)")
                .setView(inviteDialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!inviteMemberAdapter.isEmpty()) {

                            if (validateMembers(inviteMemberList)) {

                                DAO dao = new DAO();
                                String organization;
                                if (firebaseAuth.getUid() != null) {
                                    organization = firebaseAuth.getUid();
                                } else {
                                    organization = "testOrg";
                                }
                                dao.invite(inviteMemberList, organization);
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


    public void addInviteMember(View view) {

        if (!tietInviteMember.getText().toString().equals("") || tietInviteMember.getText() == null) {
            //inviteMemberList.add(tietInviteMember.getText().toString());
            inviteMemberAdapter.add(tietInviteMember.getText().toString());

            tietInviteMember.setText("");
        }
    }

    private boolean validateMembers(ArrayList<String> users) {
        //TODO: add a check to see if users exists
        return true;
    }

    private void processInvites() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_process_invite, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_process_invite);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()); // Creates a divider between items
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapterProcessInvite = new AdapterProcessInvite(pendingInviteRequests, this);
        recyclerView.setAdapter(adapterProcessInvite);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Process invite(s)")
                .setView(dialogView)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
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
//                ObjectInputStream objectInputStream = null;
//                HttpURLConnection.setFollowRedirects(false);
//                for (int i = 0; i < urls.length; i++) {
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) urls[i].openConnection();
//                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                        objectInputStream = new ObjectInputStream(new BufferedInputStream(urls[i].openStream()));
//                        // TODO (3) Decrypt the Consentcoin object
//                        Consentcoin consentcoin = (Consentcoin) objectInputStream.readObject();
//                        consentcoins.add(consentcoin);
//                    }
//                }
//                objectInputStream.close();

                for (int i = 0; i < urls.length; i++) {
                    ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(urls[i].openStream()));
                    // TODO (3) Decrypt the Consentcoin object
                    Consentcoin consentcoin = (Consentcoin) objectInputStream.readObject();
                    consentcoins.add(consentcoin);
                    objectInputStream.close();
                }

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
//                Consentcoin contract = (Consentcoin) objectInputStream.myConsentcoins();
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
            displayConsentcoin();
        }
    }

}
