package com.thomosim.consentcoin.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
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

        TextView tvMember = findViewById(R.id.tv_show_contract);


        // The getIntent method returns the intent that started this activity. This intent was created in the constructor of the ViewHolderProcessRequest class found in the AdapterProcessRequest. The processRequest method of the MainActivity class creates an instance of the AdapterProcessRequest class
        Intent startIntent = getIntent();
        if (startIntent.hasExtra("PR") && startIntent.hasExtra("POS")) {
            PermissionRequest permissionRequest = (PermissionRequest) startIntent.getSerializableExtra("PR");
            position = startIntent.getIntExtra("POS", -1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

            String[] lines = new String[6];
            lines[0] = getString(R.string.contract_text_part_one) + permissionRequest.getMemberName();
            lines[1] = getString(R.string.contract_text_part_two) + permissionRequest.getOrganizationName();
            lines[2] = getString(R.string.contract_text_part_three);
            lines[3] = getString(R.string.contract_text_part_four) +
                    permissionRequest.getPermissionType().getType() + getString(R.string.contract_text_part_five);
            lines[4] = getString(R.string.contract_text_part_six);
            lines[5] = simpleDateFormat.format(permissionRequest.getPermissionStartDate()) + getString(R.string.contract_text_part_seven)
                    + simpleDateFormat.format(permissionRequest.getPermissionEndDate());

            String completeContract = "";

            for (int i = 0; i < lines.length; i++) {
                completeContract += lines[i];
            }

            tvMember.setText(completeContract);
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
