package com.thomosim.consentcoin.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.thomosim.consentcoin.Persistence.ModelClass.PermissionRequest;
import com.thomosim.consentcoin.R;

public class ProcessRequestActivity extends AppCompatActivity {
    private Intent returnIntent;
    private PermissionRequest permissionRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_request);

        TextView tvShowContract = findViewById(R.id.tv_show_contract);

        // The getIntent method returns the intent that started this activity. This intent was created in the constructor of the ViewHolderProcessRequest class found in the AdapterProcessRequest. The processRequest method of the MainActivity class creates an instance of the AdapterProcessRequest class
        Intent startIntent = getIntent();
        if (startIntent.hasExtra("PR")) {
            permissionRequest = (PermissionRequest) startIntent.getSerializableExtra("PR");

            SpannableStringBuilder contract = new SpannableContractBuilder(this).displayPermissionRequest(permissionRequest);
            tvShowContract.setText(contract);
        }
        returnIntent = new Intent();
        returnIntent.putExtra("PR", permissionRequest);
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
