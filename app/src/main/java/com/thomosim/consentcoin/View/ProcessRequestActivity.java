package com.thomosim.consentcoin.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.thomosim.consentcoin.Persistence.ModelClass.PermissionRequest;
import com.thomosim.consentcoin.R;

import java.text.SimpleDateFormat;

public class ProcessRequestActivity extends AppCompatActivity {

    private Intent returnIntent;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_request);

        TextView tvMember = findViewById(R.id.tv_process_request_member);
        TextView tvOrganization = findViewById(R.id.tv_process_request_organization);
        TextView tvPurposes = findViewById(R.id.tv_process_request_purposes);
        TextView tvStartDate = findViewById(R.id.tv_process_request_startDate);
        TextView tvEndDate = findViewById(R.id.tv_process_request_endDate);

        // The getIntent method returns the intent that started this activity. This intent was created in the constructor of the ViewHolderProcessRequest class found in the AdapterProcessRequest. The processRequest method of the MainActivity class creates an instance of the AdapterProcessRequest class
        Intent startIntent = getIntent();
        if (startIntent.hasExtra("PR") && startIntent.hasExtra("POS")) {
            PermissionRequest permissionRequest = (PermissionRequest) startIntent.getSerializableExtra("PR");
            position = startIntent.getIntExtra("POS", -1);
            tvMember.setText(permissionRequest.getMemberName());
            tvOrganization.setText(permissionRequest.getOrganizationName());

            switch (permissionRequest.getPermissionType()) {
                case NON_COMMERCIAL_USE:
                    tvPurposes.setText("non-commercial purposes");
                    break;
                case COMMERCIAL_USE:
                    tvPurposes.setText("commercial purposes");
                    break;
                case NON_COMMERCIAL_AND_COMMERCIAL_USE:
                    tvPurposes.setText("commercial and non-commercial purposes");
                    break;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            tvStartDate.setText(simpleDateFormat.format(permissionRequest.getPermissionStartDate()));
            tvEndDate.setText(simpleDateFormat.format(permissionRequest.getPermissionEndDate()));

        }

        returnIntent = new Intent();
        returnIntent.putExtra("POS", position);
    }

    /**
     * Add a boolean set to true to the intent, which will be returned to the main method
     */
    public void accept(View view) {
        returnIntent.putExtra("BOOLEAN", true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void deny(View view) {
        returnIntent.putExtra("BOOLEAN", false);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void back(View view) {
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    public void readMore(View view) {
    }
}
