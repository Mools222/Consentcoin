package com.thomosim.consentcoin.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.thomosim.consentcoin.Persistence.ModelClass.Consentcoin;
import com.thomosim.consentcoin.Persistence.ModelClass.ConsentcoinReference;
import com.thomosim.consentcoin.R;
import com.thomosim.consentcoin.ViewModel.MyViewModel;

import java.util.Date;

// TODO This whole activity
public class MyConsentcoinActivity extends AppCompatActivity {
    private Intent returnIntent;
    private Consentcoin consentcoin;
    private ConsentcoinReference consentcoinReference;
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_consentcoin);

        TextView textView = findViewById(R.id.tv_my_consentcoins);

        myViewModel = MyViewModel.getInstance();

        Intent startIntent = getIntent();
        consentcoin = (Consentcoin) startIntent.getSerializableExtra("CC");
        consentcoinReference = (ConsentcoinReference) startIntent.getSerializableExtra("CR");

        textView.setText("ID: " + consentcoin.getContractId() + "\nContractType: " + consentcoin.getPermissionType() + "\nMemID: " + consentcoin.getMemberUid() + "\nOrgID: " + consentcoin.getOrganizationUid());

        returnIntent = new Intent();
    }

    public void delete(View view) {
        setResult(Activity.RESULT_OK, returnIntent);
        myViewModel.getDao().removeConsentcoin(consentcoin);
        consentcoinReference.setRevokedDate(new Date());
        myViewModel.getDao().updateConsentcoinReference(consentcoinReference.getId(), consentcoinReference);
        finish();
    }

    public void cancel(View view) {
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
