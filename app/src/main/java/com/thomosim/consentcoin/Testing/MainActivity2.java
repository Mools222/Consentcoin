package com.thomosim.consentcoin.Testing;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.thomosim.consentcoin.Persistence.User;
import com.thomosim.consentcoin.R;

public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextInputEditText textInputEditText;
    private TextView textView1;
    private TextView tvNavigationHeaderName, tvNavigationHeaderEmail;
    private TextView tvNavigationDrawerCounter;
    private TextView tvNavigationDrawerPendingPermissionsCounter;
    private MenuItem menuItemPendingRequests, menuItemCreateRequest, menuItemMyPermissions, menuItemInvite, menuItemAddOrganization, menuItemAddMember, menuItemMyOrganizations, menuItemMyMembers;

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_PROCESS_REQUEST = 2;
    private int chosenUserType;

    private FirebaseUtilities firebaseUtilities;

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

        menuItemPendingRequests = navigationView.getMenu().findItem(R.id.nav_pending_requests);
        menuItemCreateRequest = navigationView.getMenu().findItem(R.id.nav_create_request);
        menuItemMyPermissions = navigationView.getMenu().findItem(R.id.nav_my_consentcoins);
        menuItemInvite = navigationView.getMenu().findItem(R.id.nav_invite);
        menuItemAddOrganization = navigationView.getMenu().findItem(R.id.nav_add_organization);
        menuItemAddMember = navigationView.getMenu().findItem(R.id.nav_add_member);
        menuItemMyOrganizations = navigationView.getMenu().findItem(R.id.nav_my_organizations);
        menuItemMyMembers = navigationView.getMenu().findItem(R.id.nav_my_members);

//        firebaseUtilities = new FirebaseUtilities(this);
        firebaseUtilities = FirebaseUtilities.getInstance(this);

        setupViewModels();
    }

    private void setupViewModels() {
        MyViewModel myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        myViewModel.getLiveDataUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.i("ZZZ", "onChanged");

                if (user == null) {
                    chooseUserType();
                } else {
                    tvNavigationHeaderName.setText(user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName());
                    tvNavigationHeaderEmail.setText(user.getEmail());

                    if (user.getType().equals("Member")) {
                        menuItemPendingRequests.setVisible(true); // Members can receive, but not create requests
                        menuItemCreateRequest.setVisible(false);
                        tvNavigationDrawerCounter.setVisibility(View.VISIBLE);
                        menuItemAddOrganization.setVisible(true); // Members can only add and view their organizations
                        menuItemAddMember.setVisible(false);
                        menuItemMyOrganizations.setVisible(true);
                        menuItemMyMembers.setVisible(false);
                    } else if (user.getType().equals("Organization")) {
                        menuItemPendingRequests.setVisible(false); // Organizations can create, but not receive requests
                        menuItemCreateRequest.setVisible(true);
                        tvNavigationDrawerCounter.setVisibility(View.GONE);
                        menuItemAddOrganization.setVisible(false); // Organizations can only add and view their members
                        menuItemAddMember.setVisible(true);
                        menuItemMyOrganizations.setVisible(false);
                        menuItemMyMembers.setVisible(true);
                    }
                }

            }
        });

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
                        firebaseUtilities.chooseUserType(userType);
                    }
                })
                .setCancelable(false)
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        firebaseUtilities.addAuthStateListener();
    }

    // onResume adds the AuthStateListener, which calls the runOnSignIn method (if the user is signed in), which adds the different EventListeners. Therefore the onPause method should remove both the AuthStateListener and EventListeners, so they are not added multiple times when the onResume method is called
    @Override
    protected void onPause() {
        super.onPause();
        firebaseUtilities.removeAuthStateListener();
        firebaseUtilities.removeDatabaseListener();
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
                firebaseUtilities.signOut();
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

//        if (id == R.id.nav_pending_requests) {
//            processRequest();
//        } else if (id == R.id.nav_create_request) {
//            createRequest();
//        } else if (id == R.id.nav_my_consentcoins) {
////            myConsentcoins();
//            displayConsentcoins();
//        } else if (id == R.id.nav_invite) {
//
//        } else if (id == R.id.nav_add_organization) {
//            addOrganizationOrMember("organization");
//        } else if (id == R.id.nav_add_member) {
//            addOrganizationOrMember("member");
//        } else if (id == R.id.nav_my_organizations) {
//            myOrganizationsOrMembers("organizations");
//        } else if (id == R.id.nav_my_members) {
//            myOrganizationsOrMembers("members");
//        } else if (id == R.id.nav_send) {
//
//        }

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

        if (requestCode == REQUEST_CODE_SIGN_IN)
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
    }


    public void write(View view) {
//        String test = textInputEditText.getText().toString();
//        firebaseUtilities.write(test);
//        textInputEditText.setText("");

        firebaseUtilities.changeUser();
    }
}
