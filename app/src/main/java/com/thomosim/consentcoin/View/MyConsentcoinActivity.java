package com.thomosim.consentcoin.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.thomosim.consentcoin.Persistence.ModelClass.Consentcoin;
import com.thomosim.consentcoin.Persistence.ModelClass.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.ModelClass.User;
import com.thomosim.consentcoin.Persistence.ModelClass.UserActivity;
import com.thomosim.consentcoin.R;
import com.thomosim.consentcoin.ViewModel.MyViewModel;

import java.util.ArrayList;
import java.util.Date;

// TODO This whole activity
public class MyConsentcoinActivity extends AppCompatActivity {
    private Intent returnIntent;
    private Consentcoin consentcoin;
    private ConsentcoinReference consentcoinReference;
    private MyViewModel myViewModel;
    private User currentUser;
    private User otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_consentcoin);

        TextView textView = findViewById(R.id.tv_my_consentcoins);

        myViewModel = MyViewModel.getInstance();

        Intent startIntent = getIntent();
        consentcoin = (Consentcoin) startIntent.getSerializableExtra("CC");
        consentcoinReference = (ConsentcoinReference) startIntent.getSerializableExtra("CR");
        currentUser = (User) startIntent.getSerializableExtra("CU");
        otherUser = (User) startIntent.getSerializableExtra("OU");

        textView.setText("ID: " + consentcoin.getContractId() + "\nContractType: " + consentcoin.getPermissionType() + "\nMemID: " + consentcoin.getMemberUid() + "\nOrgID: " + consentcoin.getOrganizationUid());

        returnIntent = new Intent();
    }

    public void delete(View view) {
        setResult(Activity.RESULT_OK, returnIntent);
        myViewModel.getDao().removeConsentcoin(consentcoin); // Delete Consentcoin
        consentcoinReference.setRevokedDate(new Date()); // Set rekovedDate for ConsentcoinReference
        myViewModel.getDao().updateConsentcoinReference(consentcoinReference.getId(), consentcoinReference);
        addUserActivity();
        finish();
    }

    public void addUserActivity() {
        User member;
        User organization;
        if (currentUser.getType().equals("Member")) {
            member = currentUser;
            organization = otherUser;
        } else {
            member = otherUser;
            organization = currentUser;
        }

        Date date = new Date();
        String memberName = member.getFirstName() + " " + member.getMiddleName() + (member.getMiddleName().length() > 0 ? " " : "") + member.getLastName();
        ArrayList<UserActivity> userActivities = organization.getUserActivities();
        if (userActivities == null)
            userActivities = new ArrayList<>();
        userActivities.add(0, new UserActivity("RDC", memberName, organization.getOrganizationName(), date)); // "RDC" = Receive Delete Consentcoin
        organization.setUserActivities(userActivities);
        myViewModel.getDao().updateUser(organization.getUid(), organization); // Add the UserActivity for the organization to Firebase

        userActivities = member.getUserActivities();
        if (userActivities == null)
            userActivities = new ArrayList<>();
        userActivities.add(0, new UserActivity("DC", memberName, organization.getOrganizationName(), date)); // "DC" = Delete Consentcoin
        member.setUserActivities(userActivities);
        myViewModel.getDao().updateUser(member.getUid(), member); // Add the UserActivity for the member to Firebase
    }

    public void cancel(View view) {
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
