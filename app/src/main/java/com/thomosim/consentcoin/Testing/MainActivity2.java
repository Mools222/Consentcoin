package com.thomosim.consentcoin.Testing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thomosim.consentcoin.AdapterCreateRequest;
import com.thomosim.consentcoin.AdapterProcessInvite;
import com.thomosim.consentcoin.AdapterProcessRequest;
import com.thomosim.consentcoin.MyConsentcoinsActivity;
import com.thomosim.consentcoin.Persistence.Consentcoin;
import com.thomosim.consentcoin.Persistence.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.DAO;
import com.thomosim.consentcoin.Persistence.DAOFirebase;
import com.thomosim.consentcoin.Persistence.DAOInterface;
import com.thomosim.consentcoin.Persistence.InviteRequest;
import com.thomosim.consentcoin.Persistence.PermissionRequest;
import com.thomosim.consentcoin.Persistence.User;
import com.thomosim.consentcoin.R;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextInputEditText textInputEditText;
    private TextView textView1;
    private TextView tvNavigationHeaderName, tvNavigationHeaderEmail;
    private TextView tvNavigationDrawerCounter;
    private TextView tvNavigationDrawerPendingPermissionsCounter;
    private TextView tvNavigationDrawerPendingInviteCounter;
    private MenuItem menuItemPendingRequests, menuItemCreateRequest, menuItemMyPermissions, menuItemPendingInvites, menuItemInvite, menuItemAddOrganization, menuItemAddMember, menuItemMyOrganizations, menuItemMyMembers;
    private AdapterProcessRequest adapterProcessRequest;
    private AdapterCreateRequest adapterCreateRequest;
    private AdapterProcessInvite adapterProcessInvite;

    private TextInputEditText tietInviteMember;
    private ArrayList<String> inviteMemberList;
    private ArrayAdapter<String> inviteMemberAdapter;
    private ListView listMember;

    private String userDisplayName, userEmail, uid;
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

    private FirebaseUtilities firebaseUtilities;
    private MyViewModel myViewModel;
    private DAOInterface dao;

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
        menuItemPendingInvites = navigationView.getMenu().findItem(R.id.nav_pending_invites);
        menuItemInvite = navigationView.getMenu().findItem(R.id.nav_invite);
        menuItemAddOrganization = navigationView.getMenu().findItem(R.id.nav_add_organization);
        menuItemAddMember = navigationView.getMenu().findItem(R.id.nav_add_member);
        menuItemMyOrganizations = navigationView.getMenu().findItem(R.id.nav_my_organizations);
        menuItemMyMembers = navigationView.getMenu().findItem(R.id.nav_my_members);

        firebaseUtilities = FirebaseUtilities.getInstance();
        dao = new DAOFirebase(this);
        setupViewModel();

        Log.i("ZZZ", "onCreate");
    }

    public void setupViewModel() {
        myViewModel = new MyViewModel();

        myViewModel.getObservableDataFirebaseAuth().observe(new MyObserver<FirebaseAuth>() {
            @Override
            public void onChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) { // User is signed in
                    Log.i("ZZZ", "logged in ");

                    user = null; // Set the user to null to avoid using the user details of a different user, who was logged in on the same device

                    userDisplayName = firebaseUser.getDisplayName();
                    userEmail = firebaseUser.getEmail();
                    uid = firebaseUser.getUid();
                    tvNavigationHeaderName.setText(userDisplayName);
                    tvNavigationHeaderEmail.setText(userEmail);

                    myViewModel.getObservableDataUser().setDatabaseReference(firebaseUtilities.getDatabaseReferenceCurrentUser()); // Since the construction of this DatabaseReference depends on which user is logged in, it must be changed every time a new user logs in.
                    myViewModel.addDatabaseListener();
                } else { // User is signed out
                    Log.i("ZZZ", "logged out ");

                    myViewModel.removeDatabaseListener();

                    userEmail = null;
                    uid = null; // This value is used in removeDatabaseListener(), so it is set to null after this method is done
                    userDisplayName = null;

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(true) // Doesn't seem to do anything
                                    .setTheme(R.style.LightTheme)
                                    .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build())) // Additional sign-in providers can be added here. See: https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
                                    .build(),
                            REQUEST_CODE_SIGN_IN);
                }
            }
        });

        myViewModel.getObservableDataUser().observe(new MyObserver<User>() {
            @Override
            public void onChanged(User currentUser) {
                user = currentUser;

                if (currentUser == null) {
                    addUser();
                } else {
                    if (currentUser.getType().equals("Member")) {
                        menuItemPendingRequests.setVisible(true); // Members can receive, but not create requests
                        menuItemCreateRequest.setVisible(false);
                        tvNavigationDrawerCounter.setVisibility(View.VISIBLE);
                        menuItemAddOrganization.setVisible(true); // Members can only add and view their organizations
                        menuItemAddMember.setVisible(false);
                        menuItemMyOrganizations.setVisible(true);
                        menuItemMyMembers.setVisible(false);
                        menuItemInvite.setVisible(false);
                        menuItemPendingInvites.setVisible(true);
                        tvNavigationDrawerPendingInviteCounter.setVisibility(View.VISIBLE);
                    } else if (currentUser.getType().equals("Organization")) {
                        menuItemPendingRequests.setVisible(false); // Organizations can create, but not receive requests
                        menuItemCreateRequest.setVisible(true);
                        tvNavigationDrawerCounter.setVisibility(View.GONE);
                        menuItemAddOrganization.setVisible(false); // Organizations can only add and view their members
                        menuItemAddMember.setVisible(true);
                        menuItemMyOrganizations.setVisible(false);
                        menuItemMyMembers.setVisible(true);
                        menuItemInvite.setVisible(true);
                        menuItemPendingInvites.setVisible(false);
                        tvNavigationDrawerPendingInviteCounter.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        myViewModel.getObservableDataDataUsers().observe(new MyObserver<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> allUsers) {
                users = allUsers;
            }
        });

        myViewModel.getObservableDataPermissionRequests().observe(new MyObserver<ArrayList<PermissionRequest>>() {
            @Override
            public void onChanged(ArrayList<PermissionRequest> permissionRequests) {
                pendingPermissionRequests = new ArrayList<>();
                for (PermissionRequest permissionRequest : permissionRequests) {
                    if (permissionRequest.getMember().equals(userEmail)) {
                        pendingPermissionRequests.add(permissionRequest);
                    }
                }
                tvNavigationDrawerCounter.setText(String.valueOf(pendingPermissionRequests.size()));
                tvNavigationDrawerPendingPermissionsCounter.setText(String.valueOf(pendingPermissionRequests.size()));
            }
        });

        myViewModel.getObservableDataConsentcoinReferences().observe(new MyObserver<ArrayList<ConsentcoinReference>>() {
            @Override
            public void onChanged(ArrayList<ConsentcoinReference> newConsentcoinReferences) {
                consentcoinReferences = new ArrayList<>();
                for (ConsentcoinReference consentcoinReference : newConsentcoinReferences) {
                    if (user.getType().equals("Member") && consentcoinReference.getMember().equals(userEmail))
                        consentcoinReferences.add(consentcoinReference);
                    else if (user.getType().equals("Organization") && consentcoinReference.getOrganization().equals(userEmail))
                        consentcoinReferences.add(consentcoinReference);
                }
            }
        });

        myViewModel.getObservableDataInviteRequests().observe(new MyObserver<ArrayList<InviteRequest>>() {
            @Override
            public void onChanged(ArrayList<InviteRequest> inviteRequests) {
                pendingInviteRequests = new ArrayList<>();
                for (InviteRequest inviteRequest : inviteRequests) {
                    if (inviteRequest.getMember().equals(userEmail)) {
                        pendingInviteRequests.add(inviteRequest);
                    }
                }
                tvNavigationDrawerPendingInviteCounter.setText(String.valueOf(pendingInviteRequests.size()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        firebaseUtilities.addAuthStateListener();
        myViewModel.addAuthStateListener();
//        myViewModel2.addAuthStateListener();
    }

    // onResume adds the AuthStateListener, which (if the user is signed in) adds the different EventListeners. Therefore the onPause method should remove both the AuthStateListener and EventListeners, so they are not added multiple times when the onResume method is called
    @Override
    protected void onPause() {
        super.onPause();
//        firebaseUtilities.removeAuthStateListener();
        myViewModel.removeAuthStateListener();
//        myViewModel2.removeAuthStateListener();

//        firebaseUtilities.removeDatabaseListener();
        myViewModel.removeDatabaseListener();
    }

    /**
     * This method handles the back button
     */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) { // If the Navigation Drawer is open and the back button is pressed, close the Navigation Drawer instead of closing the app
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
        return true;
    }

    /**
     * This method handles the inputs to the menu on the right side of the action bar
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                firebaseUtilities.signOut(this);
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
        drawer.closeDrawer(GravityCompat.START); // Always close the drawer again
        return true;
    }


    /**
     * This method handles results from other activities
     */

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
//                        createConsentcoin(permissionRequest.getId(), permissionRequest.getPermissionType(), permissionRequest.getOrganization(), permissionRequest.getMember()); // If the user chooses to give permission, create a Consentcoin
                        dao.addConsentcoin(permissionRequest.getId(), permissionRequest.getPermissionType(), permissionRequest.getOrganization(), permissionRequest.getMember()); // If the user chooses to give permission, create a Consentcoin
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }

//                    databaseReferencePermissionRequests.child(permissionRequest.getId()).removeValue(); // Remove the permission request from the database
                    dao.removePermissionRequest(permissionRequest.getId()); // Remove the permission request from the database
                    pendingPermissionRequests.remove(permissionRequest); // Remove the permission request from the ArrayList
                    adapterProcessRequest.updateData(pendingPermissionRequests); // Update the adapter
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
                    InviteRequest inviteRequest = pendingInviteRequests.get(data.getIntExtra("POS", -1));

                    boolean inviteAccepted = data.getBooleanExtra("BOOLEAN", false);

                    if (inviteAccepted) {
                        Toast.makeText(this, "Invite Accepted", Toast.LENGTH_SHORT).show();

                        for (User user : users) {
                            if (user.getUid().equals(inviteRequest.getOrganization())) {
                                ArrayList<String> associatedUsersUids = user.getAssociatedUsersUid();
                                if (associatedUsersUids == null)
                                    associatedUsersUids = new ArrayList<>();
                                if (!associatedUsersUids.contains(uid))
                                    associatedUsersUids.add(uid);

                                user.setAssociatedUsersUid(associatedUsersUids);

                                Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();

                                dao.updateUser(inviteRequest.getOrganization(), user);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Invite declined", Toast.LENGTH_SHORT).show();
                    }

//                    databaseReferenceInviteRequests.child(inviteRequest.getId()).removeValue();
                    dao.removeInviteRequest(inviteRequest.getId());
                    pendingInviteRequests.remove(inviteRequest);
                    adapterProcessInvite.updateData(pendingInviteRequests);
                }
            }
        }
    }

    public void addUser() {
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
                        dao.addUser(userType, uid, userEmail, userDisplayName);
                    }
                })
                .setCancelable(false)
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
                                    dao.addPermissionRequest(userEmail, members.get(i).getEmail(), "P1");
                                }
                            } else { // If the organization wishes to send requests out to a select number of its associated members
                                ArrayList<User> checkedUsers = adapterCreateRequest.getCheckedUsers();
                                for (int i = 0; i < checkedUsers.size(); i++) {
                                    dao.addPermissionRequest(userEmail, checkedUsers.get(i).getEmail(), "P1");
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

    public void displayConsentcoins() {
        if (consentcoinReferences != null && consentcoinReferences.size() > 0) {
            String[] array = new String[consentcoinReferences.size()];
            for (int i = 0; i < consentcoinReferences.size(); i++) {
                ConsentcoinReference consentcoinReference = consentcoinReferences.get(i);
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
                            intent.putExtra("CR", consentcoinReferences.get(which));
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

                        if (userType.equals("organization")) // If a member is trying to add an organization, check if the organization exists
                            for (int i = 0; i < organizations.size(); i++) {
                                organizationOrMember = organizations.get(i);
                                if (organizationOrMember.getEmail().equals(email)) {
                                    existingOrganizationOrMember = true;
                                    break;
                                }
                            }
                        else if (userType.equals("member")) // If an organization is trying to add a member, check if the member exists
                            for (int i = 0; i < members.size(); i++) {
                                organizationOrMember = members.get(i);
                                if (organizationOrMember.getEmail().equals(email)) {
                                    existingOrganizationOrMember = true;
                                    break;
                                }
                            }

                        if (existingOrganizationOrMember) { // If the organization or member exists, add it to the logged in user's associated users list
                            ArrayList<String> associatedUsersUids = user.getAssociatedUsersUid();
                            if (associatedUsersUids == null)
                                associatedUsersUids = new ArrayList<>();
                            if (!associatedUsersUids.contains(organizationOrMember.getUid()))
                                associatedUsersUids.add(organizationOrMember.getUid());
                            user.setAssociatedUsersUid(associatedUsersUids);
                            dao.updateUser(uid, user);
                            Toast.makeText(CONTEXT, userType.substring(0, 1).toUpperCase() + userType.substring(1) + " added", Toast.LENGTH_SHORT).show();
                        } else // If the organization or member does not exist, display a toast
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

    public void invite() {
        View inviteDialogView = getLayoutInflater().inflate(R.layout.dialog_create_invite, null);

        tietInviteMember = inviteDialogView.findViewById(R.id.memberEditText);
        inviteMemberList = new ArrayList<>();
        listMember = inviteDialogView.findViewById(R.id.list_member);

        inviteMemberAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, inviteMemberList);
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
                                String organization;
                                if (uid != null) {
                                    organization = uid;
                                } else {
                                    organization = "testOrg";
                                }
                                dao.addInviteRequest(inviteMemberList, organization);

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

    public void write(View view) {
//        String test = textInputEditText.getText().toString();
//        firebaseUtilities.write(test);
//        textInputEditText.setText("");

        Toast.makeText(this, user.getType(), Toast.LENGTH_SHORT).show();

    }
}
