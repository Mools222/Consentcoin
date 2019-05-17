package com.thomosim.consentcoin.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.thomosim.consentcoin.Persistence.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.PermissionRequest;
import com.thomosim.consentcoin.R;

public class MyConsentcoinsActivity extends AppCompatActivity {

    private Intent returnIntent;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_consentcoins);

        TextView textView = findViewById(R.id.tv_my_consentcoins);

        Intent startIntent = getIntent();
        if (startIntent.hasExtra("CR") && startIntent.hasExtra("POS")) {
            ConsentcoinReference consentcoinReference = (ConsentcoinReference) startIntent.getSerializableExtra("CR");
            position = startIntent.getIntExtra("POS", -1);
            textView.setText("ID: " + consentcoinReference.getContractId());
        }

        returnIntent = new Intent();
        returnIntent.putExtra("POS", position);
    }

    public void back(View view) {
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void cancel(View view) {
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
