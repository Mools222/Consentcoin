package com.thomosim.consentcoin.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.thomosim.consentcoin.Persistence.ModelClass.Consentcoin;
import com.thomosim.consentcoin.R;

// TODO This whole activity
public class MyConsentcoinActivity extends AppCompatActivity {
    private Intent returnIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_consentcoin);

        TextView textView = findViewById(R.id.tv_my_consentcoins);

        Intent startIntent = getIntent();
        if (startIntent.hasExtra("CC")) {
            Consentcoin consentcoin = (Consentcoin) startIntent.getSerializableExtra("CC");
            textView.setText("ID: " + consentcoin.getContractId() + "\nContractType: " + consentcoin.getPermissionType().toString() + "\nMemID: " + consentcoin.getMemberUid() + "\nOrgID: " + consentcoin.getOrganizationUid());
        }

        returnIntent = new Intent();
    }

    public void delete(View view) {
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void cancel(View view) {
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
