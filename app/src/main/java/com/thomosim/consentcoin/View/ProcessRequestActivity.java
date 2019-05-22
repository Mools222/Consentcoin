package com.thomosim.consentcoin.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.thomosim.consentcoin.Persistence.ModelClass.PermissionRequest;
import com.thomosim.consentcoin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ProcessRequestActivity extends AppCompatActivity {

    private Intent returnIntent;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_request);

        TextView tvShowContract = findViewById(R.id.tv_show_contract);

        // The getIntent method returns the intent that started this activity. This intent was created in the constructor of the ViewHolderProcessRequest class found in the AdapterProcessRequest. The processRequest method of the MainActivity class creates an instance of the AdapterProcessRequest class
        Intent startIntent = getIntent();
        if (startIntent.hasExtra("PR") && startIntent.hasExtra("POS")) {
            PermissionRequest permissionRequest = (PermissionRequest) startIntent.getSerializableExtra("PR");
            position = startIntent.getIntExtra("POS", -1);

            ArrayList<SpannableStringBuilder> contract = createContractText(permissionRequest);

            for (SpannableStringBuilder s: contract) {
                tvShowContract.append(s);
            }
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

    //This method creates SpannableStringBuilders which have different colors, these will be added and appear as text
    public ArrayList<SpannableStringBuilder> createContractText(PermissionRequest pr){
        ArrayList<SpannableStringBuilder> completeContract = new ArrayList<>();
        Typeface typeface = Typeface.createFromAsset(getAssets(), "NotoSans-Black.ttf");

        ArrayList<Object> contractElements = getContractElements(pr);

        for (Object o: contractElements) {
            SpannableStringBuilder element = null;
            if(Build.VERSION.SDK_INT > 22) {
                if (o instanceof SpannableStringBuilder) {
                    element = (SpannableStringBuilder) o;
                    element = setColorOfElement(element, R.color.colorBitterLemon);


                } else if (o instanceof String) {
                    element = new SpannableStringBuilder((String) o);
                    element = setColorOfElement(element, R.color.colorRichBlack);
                }
            }
            element.setSpan(new CustomTypefaceSpan("", typeface), 0 , element.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            element.setSpan(new RelativeSizeSpan(1.2f), 0, element.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
            completeContract.add(element);
        }

        return completeContract;
    }

    //Class for putting all the text elements of the contract in to a single ArrayList
    //If the text is userspecific it will be added as a SpannableStringBuilder, if it is generic text from the strings.xml file it wil be added as a String
    private ArrayList<Object> getContractElements(PermissionRequest pr) {
        ArrayList<Object> contract = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        contract.add(getString(R.string.contract_text_part_one));
        contract.add(new SpannableStringBuilder(pr.getMemberName()));
        contract.add(getString(R.string.contract_text_part_two));
        contract.add(new SpannableStringBuilder(pr.getOrganizationName()));
        contract.add(getString(R.string.contract_text_part_three));
        contract.add(new SpannableStringBuilder(pr.getPermissionType().getType()));
        contract.add(getString(R.string.contract_text_part_four));
        contract.add(new SpannableStringBuilder(simpleDateFormat.format(pr.getPermissionStartDate())));
        contract.add(getString(R.string.contract_text_part_five));
        contract.add(new SpannableStringBuilder(simpleDateFormat.format(pr.getPermissionEndDate())));

        return contract;
    }

    //Simple method to change color of elements of the contract
    //The method was created to avoid phones with API less than 23 to see their invites
    @RequiresApi(api = Build.VERSION_CODES.M)
    public SpannableStringBuilder setColorOfElement(SpannableStringBuilder e, int color){
        e.setSpan(new ForegroundColorSpan(getColor(color)), 0, e.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return e;
    }
}
