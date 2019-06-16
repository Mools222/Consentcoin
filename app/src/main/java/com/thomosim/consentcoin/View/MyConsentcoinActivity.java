package com.thomosim.consentcoin.View;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.thomosim.consentcoin.Persistence.ModelClass.Consentcoin;
import com.thomosim.consentcoin.Persistence.ModelClass.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.ModelClass.User;
import com.thomosim.consentcoin.Persistence.ModelClass.UserActivity;
import com.thomosim.consentcoin.R;
import com.thomosim.consentcoin.ViewModel.MyViewModel;

import java.util.ArrayList;
import java.util.Date;

public class MyConsentcoinActivity extends AppCompatActivity {
    private Intent returnIntent;
    private Consentcoin consentcoin;
    private ConsentcoinReference consentcoinReference;
    private MyViewModel myViewModel;
    private User member;
    private User organization;
    private String memberName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_consentcoin);

        TextView textView = findViewById(R.id.tv_my_consentcoins);

        myViewModel = MyViewModel.getInstance();

        Intent startIntent = getIntent();
        consentcoin = (Consentcoin) startIntent.getSerializableExtra("CC");
        consentcoinReference = (ConsentcoinReference) startIntent.getSerializableExtra("CR");
        User currentUser = (User) startIntent.getSerializableExtra("CU");
        User otherUser = (User) startIntent.getSerializableExtra("OU");

        if (currentUser.getType().equals("Member")) {
            member = currentUser;
            organization = otherUser;
        } else {
            member = otherUser;
            organization = currentUser;
        }

        memberName = member.getMiddleName() == null ? member.getFirstName() + " " + member.getLastName() : member.getFirstName() + " " + member.getMiddleName() + " " + member.getLastName();

        SpannableStringBuilder text = new SpannableContractBuilder(this).displayConsentcoin(consentcoin.getContractId(),
                memberName, organization.getOrganizationName(), consentcoin.getPermissionType().getType());

        textView.setText(text);

        returnIntent = new Intent();
    }

    public void confirm(View view) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.consentcoin_value_confirm))
                .setPositiveButton(getString(R.string.consentcoin_value_y), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                    }
                })
                .setNegativeButton(getString(R.string.consentcoin_value_n), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void delete() {
        setResult(Activity.RESULT_OK, returnIntent);

        myViewModel.getDao().removeConsentcoin(consentcoin); // Delete Consentcoin

        Date date = new Date();
        consentcoinReference.setRevokedDate(date); // Set revokedDate for ConsentcoinReference
        myViewModel.getDao().updateConsentcoinReference(consentcoinReference.getId(), consentcoinReference);

        addUserActivity(member, member.getUid(), member.getUserActivities(), "DC", memberName, organization.getOrganizationName(), date); // "DC" = Delete Consentcoin
        addUserActivity(organization, organization.getUid(), organization.getUserActivities(), "RDC", memberName, organization.getOrganizationName(), date); // "RDC" = Receive Delete Consentcoin

        finish();
    }

    public void addUserActivity(User user, String uid, ArrayList<UserActivity> userActivities, String activityCode, String memberName, String organizationName, Date date) {
        if (userActivities == null)
            userActivities = new ArrayList<>();
        userActivities.add(0, new UserActivity(activityCode, memberName, organizationName, date));
        user.setUserActivities(userActivities);
        myViewModel.getDao().updateUser(uid, user);
    }

    public void cancel(View view) {
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
