package com.thomosim.consentcoin.View;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.thomosim.consentcoin.Persistence.DAOFirebase;
import com.thomosim.consentcoin.Persistence.User;
import com.thomosim.consentcoin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CreateRequestActivity extends AppCompatActivity {

    private TextInputEditText textInputEditTextStartDate;
    private TextInputEditText textInputEditTextEndDate;
    private MaterialCheckBox materialCheckBoxCommercial;
    private MaterialCheckBox materialCheckBoxNoncommercial;
    private AdapterCreateRequest adapterCreateRequest;

    private GregorianCalendar gregorianCalendar;
    private SimpleDateFormat simpleDateFormat;
    private ArrayList<User> members;
    private boolean sendRequestToAllMembers;
    private Date startDate;
    private Date endDate;
    private String organizationName;
    private String organizationUid;
    private String permissionType;

    private Intent returnIntent;

    private DAOFirebase dao = DAOFirebase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        textInputEditTextStartDate = findViewById(R.id.et_create_request_start_date);
        textInputEditTextEndDate = findViewById(R.id.et_create_request_end_date);
        final RecyclerView recyclerView = findViewById(R.id.rv_create_request);
        materialCheckBoxCommercial = findViewById(R.id.cb_create_request_commercial);
        materialCheckBoxNoncommercial = findViewById(R.id.cb_create_request_noncommercial);

        Intent startIntent = getIntent();
        if (startIntent.hasExtra("OrgName") && startIntent.hasExtra("OrgName") && startIntent.hasExtra("M")) {
            organizationName = startIntent.getStringExtra("OrgName");
            organizationUid = startIntent.getStringExtra("OrgUid");
            members = (ArrayList<User>) startIntent.getSerializableExtra("M");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()); // Creates a divider between items
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapterCreateRequest = new AdapterCreateRequest(members);
        recyclerView.setAdapter(adapterCreateRequest);
        recyclerView.setVisibility(View.GONE);

        final ArrayList<User> membersSearch = new ArrayList<>();
        final TextInputEditText textInputEditText = findViewById(R.id.et_create_request);
        textInputEditText.setVisibility(View.GONE);
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                membersSearch.clear();
                for (int i = 0; i < members.size(); i++) {
                    User member = members.get(i);
                    if (member.getEmail().contains(s))
                        membersSearch.add(member);
                    adapterCreateRequest.updateData(membersSearch);
                }
            }
        });

        sendRequestToAllMembers = true;
        RadioGroup radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_first) {
                    recyclerView.setVisibility(View.GONE);
                    textInputEditText.setVisibility(View.GONE);
                    sendRequestToAllMembers = true;
                } else if (checkedId == R.id.rb_second) {
                    recyclerView.setVisibility(View.VISIBLE);
                    textInputEditText.setVisibility(View.VISIBLE);
                    sendRequestToAllMembers = false;
                }
            }
        });

        gregorianCalendar = new GregorianCalendar();
        Date date = gregorianCalendar.getTime();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        textInputEditTextStartDate.setText(simpleDateFormat.format(date));
        textInputEditTextEndDate.setText(simpleDateFormat.format(date));

        startDate = new Date();
        endDate = new Date();

        returnIntent = new Intent();
    }

    public void startDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, dayOfMonth);
                startDate = gregorianCalendar.getTime();
                textInputEditTextStartDate.setText(simpleDateFormat.format(startDate));
            }
        }, gregorianCalendar.get(Calendar.YEAR), gregorianCalendar.get(Calendar.MONTH), gregorianCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void endDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, dayOfMonth);
                endDate = gregorianCalendar.getTime();
                textInputEditTextEndDate.setText(simpleDateFormat.format(endDate));
            }
        }, gregorianCalendar.get(Calendar.YEAR), gregorianCalendar.get(Calendar.MONTH), gregorianCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // TODO Create checks to make sure the user picked the purpose(s), correct dates, etc before sending.
    public void send(View view) {
        if (materialCheckBoxCommercial.isChecked() && materialCheckBoxNoncommercial.isChecked())
            permissionType = "3";
        else if (materialCheckBoxCommercial.isChecked())
            permissionType = "2";
        else if (materialCheckBoxNoncommercial.isChecked())
            permissionType = "1";

        if (permissionType == null)
            Toast.makeText(this, "Please select purpose(s)", Toast.LENGTH_SHORT).show();
        else {
            if (sendRequestToAllMembers) { // If the organization wishes to send requests out to all of its associated members
                for (int i = 0; i < members.size(); i++) {
                    dao.addPermissionRequest(organizationName, organizationUid, members.get(i).getUid(), permissionType, new Date(), startDate, endDate);
                }
            } else { // If the organization wishes to send requests out to a select number of its associated members
                ArrayList<User> checkedUsers = adapterCreateRequest.getCheckedUsers();
                for (int i = 0; i < checkedUsers.size(); i++) {
                    dao.addPermissionRequest(organizationName, organizationUid, checkedUsers.get(i).getUid(), permissionType, new Date(), startDate, endDate);
                }
            }
            Toast.makeText(this, "Request(s) sent!", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    public void cancel(View view) {
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
