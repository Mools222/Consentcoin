package com.thomosim.consentcoin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.thomosim.consentcoin.Persistens.PermissionRequest;

public class ProcessRequestActivity extends AppCompatActivity {

    private Intent returnIntent;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_request);

        TextView textView = findViewById(R.id.tv_process_request);

        Intent startIntent = getIntent();

        if (startIntent.hasExtra("PR") && startIntent.hasExtra("POS")) {
            PermissionRequest permissionRequest = (PermissionRequest) startIntent.getSerializableExtra("PR");
            position = startIntent.getIntExtra("POS", -1);
            textView.setText("ID: " + permissionRequest.getId());
        }

        returnIntent = new Intent();
        returnIntent.putExtra("POS", position);
        setResult(Activity.RESULT_OK, returnIntent);
    }

    public void accept(View view) {
        returnIntent.putExtra("BOOLEAN", true);
        finish();
    }

    public void deny(View view) {
        returnIntent.putExtra("BOOLEAN", false);
        finish();

    }

    public void later(View view) {
        returnIntent.putExtra("LATER", "later");
        finish();
    }
}
