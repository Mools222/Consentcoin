package com.thomosim.consentcoin.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thomosim.consentcoin.ObserverPattern.MyObserver;
import com.thomosim.consentcoin.Persistence.ModelClass.Consentcoin;
import com.thomosim.consentcoin.Persistence.ModelClass.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.ModelClass.InviteRequest;
import com.thomosim.consentcoin.Persistence.ModelClass.PermissionRequest;
import com.thomosim.consentcoin.Persistence.ModelClass.User;
import com.thomosim.consentcoin.Persistence.ModelClass.UserActivity;
import com.thomosim.consentcoin.R;
import com.thomosim.consentcoin.ViewModel.MyViewModel;

import java.util.ArrayList;
import java.util.Date;

// TODO Add a nice UI for "Active Request(s)"
// TODO Add a nice UI for "My Consentcoins"
// TODO Add a nice UI for "My Members"
// TODO Add "Settings"
// TODO Denying an invite request doesn't seem to produce a UserActivity
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView tvNavigationHeaderName, tvNavigationHeaderEmail, tvNavigationDrawerCounter, tvNavigationDrawerPendingPermissionsCounter, tvNavigationDrawerPendingInviteCounter;
    private MenuItem menuItemPendingRequests, menuItemCreateRequest, menuItemSentRequests, menuItemMyPermissions, menuItemPendingInvites, menuItemInvite, menuItemAddOrganization, menuItemAddMember, menuItemMyOrganizations, menuItemMyMembers;
    private AdapterMainActivity adapterMainActivity;
    private AdapterProcessRequest adapterProcessRequest;
    private AdapterProcessInvite adapterProcessInvite;
    private TextInputEditText tietInviteMember;
    private View navigationDrawerHeader;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private ArrayList<String> inviteMemberList;
    private ArrayAdapter<String> inviteMemberAdapter;
    private String userDisplayName, userEmail, uid;
    private User user;
    private int chosenUserType; // int default value = 0
    private static final int REQUEST_CODE_SIGN_IN = 1, REQUEST_CODE_PROCESS_REQUEST = 2, REQUEST_CODE_MY_CONSENTCOINS = 3, REQUEST_CODE_PROCESS_INVITE = 4, REQUEST_CODE_CREATE_REQUEST = 5;
    private ArrayList<User> users;
    private ArrayList<ConsentcoinReference> consentcoinReferences;
    private ArrayList<PermissionRequest> pendingPermissionRequests;
    private ArrayList<InviteRequest> pendingInviteRequests;
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigationView();
        setupRecyclerView();

        createSwipeFunction();

        assignTextViews();
        assignMenuItems();

        setupViewModel();
    }

    public void setupNavigationView() {
        // Setting up the Navigation View
        // https://material.io/develop/android/components/navigation-view/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        navigationDrawerHeader = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void setupRecyclerView() {
        // Initialize references to views
        recyclerView = findViewById(R.id.rv_main_activity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()); // Creates a divider between items
//        recyclerView.addItemDecoration(dividerItemDecoration);
        adapterMainActivity = new AdapterMainActivity(this);
        recyclerView.setAdapter(adapterMainActivity);
    }

    public void createSwipeFunction() {
        // This ItemTouchHelper allows the user to delete UserActivity objects by swiping left or right
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) { // This method is called when a user swipes left or right on a ViewHolder
                int position = viewHolder.getAdapterPosition();
                ArrayList<UserActivity> userActivities = user.getUserActivities();
                userActivities.remove(position);
                user.setUserActivities(userActivities);
                myViewModel.getDao().updateUser(uid, user);
            }
        }).attachToRecyclerView(recyclerView);
    }

    public void assignTextViews() {
        tvNavigationDrawerCounter = findViewById(R.id.tv_navigation_drawer_count); // This is the counter in the app bar on top of button that opens the Navigation Drawer
        tvNavigationDrawerPendingPermissionsCounter = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_requests).getActionView(); // This is the counter inside the Navigation Drawer menu next to the "Pending requests" button
        tvNavigationDrawerPendingPermissionsCounter.setGravity(Gravity.CENTER_VERTICAL);
        tvNavigationDrawerPendingPermissionsCounter.setTypeface(null, Typeface.BOLD);
        tvNavigationDrawerPendingPermissionsCounter.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
//        tvNavigationDrawerPendingPermissionsCounter.setBackground(getResources().getDrawable(R.drawable.navigation_drawer_counter_red_circle));
        tvNavigationDrawerPendingPermissionsCounter.setText("0");
        tvNavigationHeaderName = navigationDrawerHeader.findViewById(R.id.tv_navigation_header_name);
        tvNavigationHeaderEmail = navigationDrawerHeader.findViewById(R.id.tv_navigation_header_email);
        tvNavigationDrawerPendingInviteCounter = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_invites).getActionView();
        tvNavigationDrawerPendingInviteCounter.setGravity(Gravity.CENTER_VERTICAL);
        tvNavigationDrawerPendingInviteCounter.setTypeface(null, Typeface.BOLD);
        tvNavigationDrawerPendingInviteCounter.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
        tvNavigationDrawerPendingInviteCounter.setText("0");
    }

    public void assignMenuItems() {
        menuItemPendingRequests = navigationView.getMenu().findItem(R.id.nav_pending_requests);
        menuItemCreateRequest = navigationView.getMenu().findItem(R.id.nav_create_request);
        menuItemSentRequests = navigationView.getMenu().findItem(R.id.nav_active_requests);
        menuItemMyPermissions = navigationView.getMenu().findItem(R.id.nav_my_consentcoins);
        menuItemPendingInvites = navigationView.getMenu().findItem(R.id.nav_pending_invites);
        menuItemInvite = navigationView.getMenu().findItem(R.id.nav_invite);
        menuItemAddOrganization = navigationView.getMenu().findItem(R.id.nav_add_organization);
        menuItemAddMember = navigationView.getMenu().findItem(R.id.nav_add_member);
        menuItemMyOrganizations = navigationView.getMenu().findItem(R.id.nav_my_organizations);
        menuItemMyMembers = navigationView.getMenu().findItem(R.id.nav_my_members);
    }

    /**
     * This method sets up the view model.
     * 1) It creates a new MyViewModel object and assigns it to myViewModel instance variable.
     * 2) It gets an instance of every ObservableData[Name] class, all of which are subclasses of MyObservable, which contains the observe and setValue methods.
     * 3) It calls the observe method found in these classes and passes an anonymous inner class of the interface MyObserver as a parameter to each of them. This
     * combines defining an inner class and creating an instance of it into one step. The MyObserver object created is added as the MyObserver objects inside the
     * various instances of ObservableData[Name].
     * 4) When creating the anonymous inner classes, we implement the onChange method found in the interface. The onChanged method is used to initialize and
     * update various data fields and to create and manipulate certain views.
     * 5) The onChange method is called by the setValue method found in the subclasses of MyObservable. In most cases the setValue method is called by the
     * onDataChange method found in the various ValueEventListener objects contained in each subclasses of MyObservable. The onDataChange method is called by
     * Firebase when it detects changes to the relevant data sources. In ObservableDataConsentcoin the setValue method is called by the onPostExecute method
     * of the AsyncTask. The onPostExecute is called via the setConsentcoinUrl method, which is called when a user inspects a Consentcoin.
     */

    public void setupViewModel() {
        myViewModel = MyViewModel.getInstance();

        myViewModel.getAuthentication().observe(new MyObserver<FirebaseAuth>() {
            @Override
            public void onChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) { // User is signed in
                    Log.i("ZZZ", "logged in " + firebaseUser.getUid() + " + addDatabaseListenerUser");

                    userDisplayName = firebaseUser.getDisplayName();
                    userEmail = firebaseUser.getEmail();
                    uid = firebaseUser.getUid();
                    tvNavigationHeaderName.setText(userDisplayName);
                    tvNavigationHeaderEmail.setText(userEmail);

                    myViewModel.getDao().setDatabaseReferenceCurrentUser(); // Since the construction of this DatabaseReference depends on which user is logged in, it must be changed every time a new user logs in.
                    myViewModel.getDao().addDatabaseListenerUser(); // Starting off, only the database listener for the current user is added, since the user type (which is only known when the User object connected to the logged in user is retrieved) is needed to determine whether the user has any PermissionRequests or ConsentcoinReferences
                } else { // User is signed out
                    Log.i("ZZZ", "logged out + startActivityForResult");

                    userEmail = null;
                    uid = null;
                    userDisplayName = null;
                    adapterMainActivity.updateData(new ArrayList<UserActivity>()); // This prevents the next user from getting a glimpse of the previous user's activities

                    startActivityForResult(myViewModel.getDao().getSignInIntent(), REQUEST_CODE_SIGN_IN); // Create and start a sign in activity. This triggers the onPause method, which removes the listeners
                }
            }
        });

        myViewModel.getUser().observe(new MyObserver<User>() {
            @Override
            public void onChanged(User currentUser) {
                user = currentUser;

                if (user == null) {
                    addUser();
                } else {
                    myViewModel.getDao().addDatabaseListener(); // Added the remaining listeners here ensures that the User object named user is not null when the remaining listeners are added. This allows the program to sort through PermissionRequests and ConsentcoinReferences and determine which ones regard the current user
                    Log.i("ZZZ", "addDatabaseListener ");

                    ArrayList<UserActivity> userActivity = user.getUserActivities();
                    if (userActivity != null)
                        adapterMainActivity.updateData(userActivity); // Add the list of UserActivity objects to the RecyclerView adapter

                    if (user.getType().equals("Member")) {
                        menuItemPendingRequests.setVisible(true); // Members can receive, but not create requests
                        menuItemCreateRequest.setVisible(false);
                        menuItemSentRequests.setVisible(false);
                        tvNavigationDrawerCounter.setVisibility(View.VISIBLE);
                        menuItemAddOrganization.setVisible(true); // Members can only add and view their organizations
                        menuItemAddMember.setVisible(false);
                        menuItemMyOrganizations.setVisible(true);
                        menuItemMyMembers.setVisible(false);
                        menuItemInvite.setVisible(false);
                        menuItemPendingInvites.setVisible(true);
                        tvNavigationDrawerPendingInviteCounter.setVisibility(View.VISIBLE);
                    } else if (user.getType().equals("Organization")) {
                        menuItemPendingRequests.setVisible(false); // Organizations can create, but not receive requests
                        menuItemCreateRequest.setVisible(true);
                        menuItemSentRequests.setVisible(true);
                        tvNavigationDrawerCounter.setVisibility(View.GONE);
                        menuItemAddOrganization.setVisible(false); // Organizations can only add and view their members
                        menuItemAddMember.setVisible(true);
                        menuItemMyOrganizations.setVisible(false);
                        menuItemMyMembers.setVisible(true);
                        menuItemInvite.setVisible(true);
                        menuItemPendingInvites.setVisible(false);
                        tvNavigationDrawerPendingInviteCounter.setVisibility(View.INVISIBLE);

                        tvNavigationHeaderName.setText(user.getOrganizationName()); // Add the name of the organization to the NavigationDrawer header
                    }
                }
            }
        });

        myViewModel.getUsers().observe(new MyObserver<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> allUsers) {
                users = allUsers;
            }
        });

        myViewModel.getPermissionRequests().observe(new MyObserver<ArrayList<PermissionRequest>>() {
            @Override
            public void onChanged(ArrayList<PermissionRequest> permissionRequests) {
                pendingPermissionRequests = new ArrayList<>();
                for (PermissionRequest permissionRequest : permissionRequests) {
                    if (user.getType().equals("Member") && permissionRequest.getMemberUid().equals(uid))
                        pendingPermissionRequests.add(permissionRequest);
                    else if (user.getType().equals("Organization") && permissionRequest.getOrganizationUid().equals(uid))
                        pendingPermissionRequests.add(permissionRequest);
                }
                tvNavigationDrawerCounter.setText(String.valueOf(pendingPermissionRequests.size()));
                tvNavigationDrawerPendingPermissionsCounter.setText(String.valueOf(pendingPermissionRequests.size()));

                if (adapterProcessRequest != null)
                    adapterProcessRequest.updateData(pendingPermissionRequests);
            }
        });

        myViewModel.getConsentcoinReferences().observe(new MyObserver<ArrayList<ConsentcoinReference>>() {
            @Override
            public void onChanged(ArrayList<ConsentcoinReference> newConsentcoinReferences) {
                consentcoinReferences = new ArrayList<>();
                for (ConsentcoinReference consentcoinReference : newConsentcoinReferences) {
                    if (user.getType().equals("Member") && consentcoinReference.getMemberUid().equals(uid) && consentcoinReference.getRevokedDate() == null)
                        consentcoinReferences.add(consentcoinReference);
                    else if (user.getType().equals("Organization") && consentcoinReference.getOrganizationUid().equals(uid) && consentcoinReference.getRevokedDate() == null)
                        consentcoinReferences.add(consentcoinReference);
                }
            }
        });

        myViewModel.getInviteRequests().observe(new MyObserver<ArrayList<InviteRequest>>() {
            @Override
            public void onChanged(ArrayList<InviteRequest> inviteRequests) {
                pendingInviteRequests = new ArrayList<>();
                for (InviteRequest inviteRequest : inviteRequests) {
                    if (inviteRequest.getMember().equals(userEmail)) {
                        pendingInviteRequests.add(inviteRequest);
                    }
                }
                tvNavigationDrawerPendingInviteCounter.setText(String.valueOf(pendingInviteRequests.size()));

                if (adapterProcessInvite != null)
                    adapterProcessInvite.updateData(pendingInviteRequests);
            }
        });

        myViewModel.getConsentcoin().observe(new MyObserver<Consentcoin>() {
            @Override
            public void onChanged(Consentcoin consentcoin) {
                displayConsentcoin(consentcoin);
            }
        });
    }

    /**
     * This method adds the AuthStateListener
     */

    @Override
    protected void onResume() {
        super.onResume();
        myViewModel.getDao().addAuthStateListener();
    }

    /**
     * The onResume method adds the AuthStateListener, which (if the user is signed in) adds the different EventListeners. Therefore the onPause method should
     * remove both the AuthStateListener and EventListeners, to ensure they are not added multiple times when the onResume method is called
     */

    @Override
    protected void onPause() {
        super.onPause();
        myViewModel.getDao().removeAuthStateListener();
        myViewModel.getDao().removeDatabaseListener();
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
                myViewModel.getDao().signOut(this);
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

        switch (id) {
            case R.id.nav_pending_requests:
                processRequest();
                break;
            case R.id.nav_create_request:
                createRequest();
                break;
            case R.id.nav_active_requests:
                displayActiveRequests();
                break;
            case R.id.nav_my_consentcoins:
                displayConsentcoinReferences();
                break;
            case R.id.nav_pending_invites:
                processInvites();
                break;
            case R.id.nav_invite:
                createInvite();
                break;
            case R.id.nav_add_organization:
                addOrganizationOrMember("organization");
                break;
            case R.id.nav_add_member:
                addOrganizationOrMember("member");
                break;
            case R.id.nav_my_organizations:
                displayOrganizationsOrMembers("organization");
                break;
            case R.id.nav_my_members:
                displayOrganizationsOrMembers("member");
                break;
            case R.id.nav_settings:
                test();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START); // Always close the drawer again
        return true;
    }

    public void test() { // Method for testing new stuff
//        PermissionRequest2 permissionRequest2 = new PermissionRequest2("123", "org", "mem", Enums.P1, new Date());
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("test2");
//        databaseReference.push().setValue(permissionRequest2);
    }

    /**
     * This method handles results from other activities
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == RESULT_OK) { // Sign-in succeeded
                    Toast.makeText(this, getString(R.string.toast_signed_in), Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) { // Sign in was canceled by the user, finish the activity
                    Toast.makeText(this, getString(R.string.toast_sign_in_cancelled), Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_CODE_PROCESS_REQUEST:
                if (resultCode == RESULT_OK) {
                    createOrDenyConsentcoin(data);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, getString(R.string.toast_pr_not_processed), Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_MY_CONSENTCOINS:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, getString(R.string.toast_concentcoin_ok), Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, getString(R.string.toast_concentcoin_cancelled), Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_PROCESS_INVITE:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra("BOOLEAN") && data.hasExtra("POS")) {
                        InviteRequest inviteRequest = pendingInviteRequests.get(data.getIntExtra("POS", -1));

                        boolean inviteAccepted = data.getBooleanExtra("BOOLEAN", false);

                        User organization = null;
                        for (User user : users) {

                            if (user.getUid().equals(inviteRequest.getOrganizationUID())) {
                                organization = user;
                                break;
                            }

                        }

                        if (inviteAccepted) {
                            Toast.makeText(this, getString(R.string.toast_invite_accepted), Toast.LENGTH_SHORT).show();


                            ArrayList<String> associatedUsersUids = organization.getAssociatedUsersUids();
                            if (associatedUsersUids == null)
                                associatedUsersUids = new ArrayList<>();
                            if (!associatedUsersUids.contains(uid))
                                associatedUsersUids.add(uid);

                            organization.setAssociatedUsersUids(associatedUsersUids);

                            Toast.makeText(this, organization.getEmail(), Toast.LENGTH_SHORT).show();

                            myViewModel.getDao().updateUser(organization.getUid(), organization);

                            if (organization.getUserActivities() != null) {
                                organization.getUserActivities().add(0, new UserActivity("RAIR", inviteRequest.getMember(), inviteRequest.getOrganizationName(), new Date()));
                                myViewModel.getDao().updateUser(organization.getUid(), organization);
                            }


                            if (user.getUserActivities() != null) {
                                user.getUserActivities().add(0, new UserActivity("AIR", inviteRequest.getMember(), inviteRequest.getOrganizationName(), new Date()));
                                myViewModel.getDao().updateUser(user.getUid(), user);
                            }

                        } else {
                            if (user.getUserActivities() != null) {
                                user.getUserActivities().add(0, new UserActivity("DIR", inviteRequest.getMember(), inviteRequest.getOrganizationName(), new Date()));
                                myViewModel.getDao().updateUser(user.getUid(), user);

                            }

                            if (organization.getUserActivities() != null) {
                                organization.getUserActivities().add(0, new UserActivity("RDIR", inviteRequest.getMember(), inviteRequest.getOrganizationName(), new Date()));
                                myViewModel.getDao().updateUser(organization.getUid(), organization);
                                Toast.makeText(this, "user activity updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "there are no userActivities for this user", Toast.LENGTH_SHORT).show();
                            }


                            Toast.makeText(this, getString(R.string.toast_invite_declined), Toast.LENGTH_SHORT).show();
                        }

                        myViewModel.getDao().removeInviteRequest(inviteRequest.getId());
                    }
                }
                break;
            case REQUEST_CODE_CREATE_REQUEST:
                if (resultCode == RESULT_OK) {
//                Toast.makeText(this, "Permission request sent", Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "Permission request canceled", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void createOrDenyConsentcoin(Intent data) {
        if (data.hasExtra("BOOLEAN") && data.hasExtra("POS")) {
            boolean permissionGranted = data.getBooleanExtra("BOOLEAN", false);
            PermissionRequest permissionRequest = pendingPermissionRequests.get(data.getIntExtra("POS", -1)); // Get the position from the returned intent
            Date date = new Date();

            User organization = null;
            for (User user : users) {
                if (user.getUid().equals(permissionRequest.getOrganizationUid())) {
                    organization = user;
                    break;
                }
            }

            if (permissionGranted) {
                myViewModel.getDao().addConsentcoin(this, permissionRequest.getId(), permissionRequest.getPermissionType(), permissionRequest.getOrganizationUid(), permissionRequest.getMemberUid(),
                        date, permissionRequest.getPermissionStartDate(), permissionRequest.getPermissionEndDate(), permissionRequest.getPersonsIncluded().getScope()); // If the user chooses to give permission, create a Consentcoin

                ArrayList<UserActivity> userActivities = user.getUserActivities();
                if (userActivities == null)
                    userActivities = new ArrayList<>();
                userActivities.add(0, new UserActivity("APR", userDisplayName, organization.getOrganizationName(), date)); // "APR" = Accept Permission Request
                user.setUserActivities(userActivities);
                myViewModel.getDao().updateUser(uid, user);

                userActivities = organization.getUserActivities();
                if (userActivities == null)
                    userActivities = new ArrayList<>();
                userActivities.add(0, new UserActivity("RAPR", userDisplayName, organization.getOrganizationName(), date)); // "RAPR" = Receive Accepted Permission Request
                organization.setUserActivities(userActivities);
                myViewModel.getDao().updateUser(organization.getUid(), organization);

                Toast.makeText(this, getString(R.string.toast_permission_given), Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<UserActivity> userActivities = user.getUserActivities();
                if (userActivities == null)
                    userActivities = new ArrayList<>();
                userActivities.add(0, new UserActivity("DPR", userDisplayName, organization.getOrganizationName(), date)); // "DPR" = Deny Permission Request
                user.setUserActivities(userActivities);
                myViewModel.getDao().updateUser(uid, user);

                userActivities = organization.getUserActivities();
                if (userActivities == null)
                    userActivities = new ArrayList<>();
                userActivities.add(0, new UserActivity("RDPR", userDisplayName, organization.getOrganizationName(), date)); // "RDPR" = Receive Denied Permission Request
                organization.setUserActivities(userActivities);
                myViewModel.getDao().updateUser(organization.getUid(), organization);

                Toast.makeText(this, R.string.toast_permission_denied, Toast.LENGTH_SHORT).show();
            }

            myViewModel.getDao().removePermissionRequest(permissionRequest.getId()); // Remove the permission request from the database
        }
    }

    public void addUser() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_user, null);

        final TextInputEditText TEXT_INPUT_EDIT_TEXT = dialogView.findViewById(R.id.et_create_request);
        TEXT_INPUT_EDIT_TEXT.setVisibility(View.GONE);

        RadioGroup radioGroup = dialogView.findViewById(R.id.rg_create_user);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_create_user_member) {
                    TEXT_INPUT_EDIT_TEXT.setVisibility(View.GONE);
                    chosenUserType = 0;
                } else if (checkedId == R.id.rb_create_user_organization) {
                    TEXT_INPUT_EDIT_TEXT.setVisibility(View.VISIBLE);
                    chosenUserType = 1;
                }
            }
        });

        final Context CONTEXT = this;
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.title_specify_user_details))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.create), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userType = chosenUserType == 0 ? getString(R.string.member) : getString(R.string.organization);
                        String organizationName = TEXT_INPUT_EDIT_TEXT.getText().toString();
                        myViewModel.getDao().addUser(userType, uid, userEmail, userDisplayName, organizationName.toLowerCase().matches(".*asshole.*|.*shit.*") ? "BadOrg" : organizationName);
                        Toast.makeText(CONTEXT, getString(R.string.toast_user_details_saved), Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void createRequest() {
        ArrayList<String> associatedUsersUids = user.getAssociatedUsersUids();
        if (associatedUsersUids != null) {
            ArrayList<User> members = new ArrayList<>();
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                for (int j = 0; j < associatedUsersUids.size(); j++) {
                    if (user.getUid().equals(associatedUsersUids.get(j))) {
                        members.add(user);
                        break;
                    }
                }
            }

            Intent intent = new Intent(this, CreateRequestActivity.class);
            intent.putExtra("O", user);
            intent.putExtra("M", members);
            startActivityForResult(intent, REQUEST_CODE_CREATE_REQUEST);
        } else
            Toast.makeText(this, getString(R.string.toast_add_users_to_request), Toast.LENGTH_SHORT).show();
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
                .setTitle(getString(R.string.title_process_request))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void displayActiveRequests() {
        if (pendingPermissionRequests != null && pendingPermissionRequests.size() > 0) {
            String[] array = new String[pendingPermissionRequests.size()];
            for (int i = 0; i < pendingPermissionRequests.size(); i++) {
                PermissionRequest permissionRequest = pendingPermissionRequests.get(i);
                array[i] = getString(R.string.array_member) + permissionRequest.getMemberName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);

            final Context CONTEXT = this;
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.title_requests_sent))
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(CONTEXT, "Show the thing clicked", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();

            ListView listView = alertDialog.getListView();
            listView.setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.colorOuterSpace)));
            listView.setDividerHeight(5);
            alertDialog.show();
        } else
            Toast.makeText(this, getString(R.string.toast_no_active_requests), Toast.LENGTH_SHORT).show();
    }

    public void displayConsentcoinReferences() {
        if (consentcoinReferences != null && consentcoinReferences.size() > 0) {
            String[] array = new String[consentcoinReferences.size()];
            for (int i = 0; i < consentcoinReferences.size(); i++) {
                ConsentcoinReference consentcoinReference = consentcoinReferences.get(i);
                if (user.getType().equals("Member"))
                    for (User u : users) {
                        if (u.getUid().equals(consentcoinReference.getOrganizationUid())) {
                            array[i] = getString(R.string.array_org) + u.getOrganizationName();
                        }
                    }
                else
                    for (User u : users) {
                        if (u.getUid().equals(consentcoinReference.getMemberUid())) {
                            array[i] = getString(R.string.array_member) + u.getFirstName() + " " + u.getLastName();
                        }
                    }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);

            final Context CONTEXT = this;
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.title_my_consentcoins))
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myViewModel.getDao().setConsentcoinUrl(consentcoinReferences.get(which).getStorageUrl());
                            Toast.makeText(CONTEXT, getString(R.string.toast_getting_consentcoins), Toast.LENGTH_SHORT).show(); // TODO Add something that prevents the user from clicking on stuff while the Consentcoin is being downloaded
                        }
                    })
                    .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();

            ListView listView = alertDialog.getListView();
            listView.setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.colorOuterSpace)));
            listView.setDividerHeight(5);
            alertDialog.show();
        } else
            Toast.makeText(this, getString(R.string.toast_no_consentcoins), Toast.LENGTH_SHORT).show();
    }

    public void displayConsentcoin(Consentcoin consentcoin) {
        Intent intent = new Intent(this, MyConsentcoinActivity.class);
        intent.putExtra("CC", consentcoin);

        ConsentcoinReference consentcoinReference = null;
        for (ConsentcoinReference consentcoinRef : consentcoinReferences) {
            if (consentcoinRef.getContractId().equals(consentcoin.getContractId()))
                consentcoinReference = consentcoinRef;
        }
        intent.putExtra("CR", consentcoinReference);

        intent.putExtra("CU", user);
        intent.putExtra("OU", getUser(user.getType().equals("Member") ? consentcoinReference.getOrganizationUid() : consentcoinReference.getMemberUid()));

        startActivityForResult(intent, REQUEST_CODE_MY_CONSENTCOINS);
    }

    public User getUser(String uid) {
        for (User user : users) {
            if (user.getUid().equals(uid))
                return user;
        }
        return null;
    }

    public void addOrganizationOrMember(final String USER_TYPE) {
        final ArrayList<User> organizations = new ArrayList<>();
        final ArrayList<User> members = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getType().equals("Organization") && USER_TYPE.equals("organization"))
                organizations.add(user);
            else if (user.getType().equals("Member") && USER_TYPE.equals("member"))
                members.add(user);
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_organization, null);
        final TextInputEditText TEXT_INPUT_EDIT_TEXT = dialogView.findViewById(R.id.et_dialog_add);
        TextInputLayout textInputLayout = dialogView.findViewById(R.id.til_dialog_add);
        if (USER_TYPE.equals("organization"))
            textInputLayout.setHint(getString(R.string.tf_enter_organisation_mail));
        else
            textInputLayout.setHint(getString(R.string.tf_enter_member_mail));
        final Context CONTEXT = this;
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.title_add_with_space) + USER_TYPE)
                .setView(dialogView)
                .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean existingOrganizationOrMember = false;
//                        if (textInputEditText.getText().length() > 0)
                        String email = TEXT_INPUT_EDIT_TEXT.getText().toString();
                        User organizationOrMember = null;

                        if (USER_TYPE.equals("organization")) // If a member is trying to add an organization, check if the organization exists
                            for (int i = 0; i < organizations.size(); i++) {
                                organizationOrMember = organizations.get(i);
                                if (organizationOrMember.getEmail().equals(email)) {
                                    existingOrganizationOrMember = true;
                                    break;
                                }
                            }
                        else if (USER_TYPE.equals("member")) // If an organization is trying to add a member, check if the member exists
                            for (int i = 0; i < members.size(); i++) {
                                organizationOrMember = members.get(i);
                                if (organizationOrMember.getEmail().equals(email)) {
                                    existingOrganizationOrMember = true;
                                    break;
                                }
                            }

                        if (existingOrganizationOrMember) { // If the organization or member exists, add it to the logged in user's associated users list
                            ArrayList<String> associatedUsersUids = user.getAssociatedUsersUids();
                            if (associatedUsersUids == null)
                                associatedUsersUids = new ArrayList<>();
                            if (!associatedUsersUids.contains(organizationOrMember.getUid()))
                                associatedUsersUids.add(organizationOrMember.getUid());
                            user.setAssociatedUsersUids(associatedUsersUids);
                            myViewModel.getDao().updateUser(uid, user);
                            Toast.makeText(CONTEXT, USER_TYPE.substring(0, 1).toUpperCase() + USER_TYPE.substring(1) + " added", Toast.LENGTH_SHORT).show();
                        } else // If the organization or member does not exist, display a toast
                            Toast.makeText(CONTEXT, USER_TYPE.substring(0, 1).toUpperCase() + USER_TYPE.substring(1) + " does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void displayOrganizationsOrMembers(String userType) {
        ArrayList<String> associatedUsersUids = user.getAssociatedUsersUids();
        if (associatedUsersUids != null) {
            String[] array = new String[associatedUsersUids.size()];
            for (int i = 0; i < array.length; i++) {
                for (User u : users) {
                    if (u.getUid().equals(associatedUsersUids.get(i))) {
                        array[i] = userType.equals("member") ? (u.getMiddleName() == null ? u.getFirstName() + getString(R.string.space) + u.getLastName() : u.getFirstName() +
                                getString(R.string.space) + u.getMiddleName() + getString(R.string.space) + u.getLastName()) : u.getOrganizationName();
                    }
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.title_my_with_space) + userType)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();

            ListView listView = alertDialog.getListView();
            listView.setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.colorOuterSpace)));
            listView.setDividerHeight(5);
            alertDialog.show();
        } else
            Toast.makeText(this, getString(R.string.toast_you_have_no) + userType, Toast.LENGTH_SHORT).show();
    }

    // TODO Maybe add a list of active invites for the organization (like the "Active Request(s)" in the menu)
    // TODO Add 1) the sending of invites to the UserActivity of the sender and receiver and 2) the accepting / denying of the createInvite to the UserActivity of the sender and receiver
    public void createInvite() {
        View inviteDialogView = getLayoutInflater().inflate(R.layout.dialog_create_invite, null);

        tietInviteMember = inviteDialogView.findViewById(R.id.memberEditText);
        inviteMemberList = new ArrayList<>();
        ListView listMember = inviteDialogView.findViewById(R.id.list_member);

        inviteMemberAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, inviteMemberList);
        listMember.setAdapter(inviteMemberAdapter);
        listMember.setVisibility(View.VISIBLE);

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.title_invite_members))
                .setView(inviteDialogView)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!inviteMemberAdapter.isEmpty()) {
                            inviteMemberList = validateMembers(inviteMemberList);
                            String organization;
                            if (user.getOrganizationName() != null) {
                                organization = user.getOrganizationName();
                            } else {
                                organization = "testOrg";
                            }
                            myViewModel.getDao().addInviteRequest(inviteMemberList, organization, user.getUid());
                            if (user.getUserActivities() != null) {
                                for (String member : inviteMemberList) {
                                    user.getUserActivities().add(0, new UserActivity("CIR", member, user.getOrganizationName(), new Date()));
                                    myViewModel.getDao().updateUser(user.getUid(), user);
                                    for (User inviteMember : users) {
                                        if (inviteMemberList.contains(inviteMember.getEmail())) {
                                            if (inviteMember.getUserActivities() != null) {
                                                inviteMember.getUserActivities().add(0, new UserActivity("RIR", inviteMember.getFirstName() + " " + inviteMember.getLastName(), user.getOrganizationName(), new Date()));
                                                myViewModel.getDao().updateUser(inviteMember.getUid(), inviteMember);
                                            }
                                        }
                                    }

                                }
                            }

                        }

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void addInviteMember(View view) {

        if (!tietInviteMember.getText().toString().equals("") || tietInviteMember.getText() == null) {
            inviteMemberAdapter.add(tietInviteMember.getText().toString());
            tietInviteMember.setText("");
        }
    }

    private ArrayList<String> validateMembers(ArrayList<String> userEmails) {
        ArrayList<String> emails = new ArrayList<>();
        for (User user : users) {
            emails.add(user.getEmail());
        }
        for (String email : userEmails) {
            if (!emails.contains(email)) {
                userEmails.remove(email);
            }
        }
        return userEmails;
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
                .setTitle(getString(R.string.title_create_invites))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}
