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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.thomosim.consentcoin.Persistence.ModelClass.ContractScopeEnum;
import com.thomosim.consentcoin.Persistence.ModelClass.ContractTypeEnum;
import com.thomosim.consentcoin.Persistence.ModelClass.User;
import com.thomosim.consentcoin.Persistence.ModelClass.UserActivity;
import com.thomosim.consentcoin.R;
import com.thomosim.consentcoin.ViewModel.MyViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CreateRequestActivity extends AppCompatActivity {
    private MaterialCheckBox materialCheckBoxCommercial;
    private MaterialCheckBox materialCheckBoxNoncommercial;
    private TextInputEditText textInputEditTextStartDate;
    private TextInputEditText textInputEditTextEndDate;
    private AdapterCreateRequest adapterCreateRequest;

    private GregorianCalendar gregorianCalendar;
    private SimpleDateFormat simpleDateFormat;
    private User organization;
    private ArrayList<User> members;
    private boolean sendRequestToAllMembers;
    private boolean permissionRegardsMembersOnly;
    private Date startDate;
    private Date endDate;
    private ContractTypeEnum permissionType;
    private ContractScopeEnum personsIncluded;

    private Intent returnIntent;

    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        myViewModel = MyViewModel.getInstance();

        TextView textViewOrganization = findViewById(R.id.tv_create_request_organization_name);
        materialCheckBoxCommercial = findViewById(R.id.cb_create_request_commercial);
        materialCheckBoxNoncommercial = findViewById(R.id.cb_create_request_noncommercial);
        textInputEditTextStartDate = findViewById(R.id.et_create_request_start_date);
        textInputEditTextEndDate = findViewById(R.id.et_create_request_end_date);

        Intent startIntent = getIntent();
        organization = (User) startIntent.getSerializableExtra("O");
        members = (ArrayList<User>) startIntent.getSerializableExtra("M");

        textViewOrganization.setText(organization.getOrganizationName());

        setPersons();
        setDates();
        setReceivers();

        returnIntent = new Intent();
    }

    public void setPersons() {
        permissionRegardsMembersOnly = true;
        RadioGroup radioGroupPersons = findViewById(R.id.rg_create_request_persons);
        radioGroupPersons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_create_request_members) {
                    permissionRegardsMembersOnly = true;
                } else if (checkedId == R.id.rb_create_request_members_and_wards) {
                    permissionRegardsMembersOnly = false;
                }
            }
        });
    }

    public void setDates() {
        gregorianCalendar = new GregorianCalendar();
        Date date = gregorianCalendar.getTime();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        textInputEditTextStartDate.setText(simpleDateFormat.format(date));
        textInputEditTextEndDate.setText(simpleDateFormat.format(date));

        startDate = new Date();
        endDate = new Date();
    }

    public void setReceivers() {
        final RecyclerView recyclerView = findViewById(R.id.rv_create_request);
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
        RadioGroup radioGroupReceiver = findViewById(R.id.rg_create_request_receiver);
        radioGroupReceiver.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_create_request_all) {
                    recyclerView.setVisibility(View.GONE);
                    textInputEditText.setVisibility(View.GONE);
                    sendRequestToAllMembers = true;
                } else if (checkedId == R.id.rb_create_request_select) {
                    recyclerView.setVisibility(View.VISIBLE);
                    textInputEditText.setVisibility(View.VISIBLE);
                    sendRequestToAllMembers = false;
                }
            }
        });
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
            permissionType = ContractTypeEnum.NON_COMMERCIAL_AND_COMMERCIAL_USE; // Commercial + non-commercial
        else if (materialCheckBoxCommercial.isChecked())
            permissionType = ContractTypeEnum.COMMERCIAL_USE; // Commercial
        else if (materialCheckBoxNoncommercial.isChecked())
            permissionType = ContractTypeEnum.NON_COMMERCIAL_USE; // Non-commercial

        personsIncluded = permissionRegardsMembersOnly ? ContractScopeEnum.MYSELF : ContractScopeEnum.MYSELF_AND_WARDS; // 1 = members only. 2 = members + wards

        if (permissionType == null)
            Toast.makeText(this, getString(R.string.toast_select_purpose), Toast.LENGTH_SHORT).show();
        else {
            if (sendRequestToAllMembers) { // If the organization wishes to send requests out to all of its associated members, we iterate through the members list and perform the necessary tasks
                sendRequests(members);
            } else { // If the organization wishes to send requests out to a select number of its associated members, we iterate through the checkedUsers list and perform the necessary tasks
                ArrayList<User> checkedUsers = adapterCreateRequest.getCheckedUsers();
                sendRequests(checkedUsers);
            }
            Toast.makeText(this, getString(R.string.toast_request_sent), Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    public void sendRequests(ArrayList<User> memberList) {
        Date date = new Date();
        for (int i = 0; i < memberList.size(); i++) {
            User member = memberList.get(i);
            String memberName = member.getFirstName() + " " + (member.getMiddleName() == null ? "" : member.getMiddleName()) + member.getLastName();
            myViewModel.getDao().addPermissionRequest(organization.getOrganizationName(), organization.getUid(), memberName, member.getUid(), permissionType, date, startDate, endDate, personsIncluded); // Add the PermissionRequest to Firebase

            ArrayList<UserActivity> userActivities = organization.getUserActivities();
            if (userActivities == null)
                userActivities = new ArrayList<>();
            userActivities.add(0, new UserActivity("CPR", memberName, organization.getOrganizationName(), date)); // "CPR" = Create Permission Request
            organization.setUserActivities(userActivities);
            myViewModel.getDao().updateUser(organization.getUid(), organization); // Add the UserActivity for the organization to Firebase

            userActivities = member.getUserActivities();
            if (userActivities == null)
                userActivities = new ArrayList<>();
            userActivities.add(0, new UserActivity("RPR", memberName, organization.getOrganizationName(), date)); // "RPR" = Receive Permission Request
            member.setUserActivities(userActivities);
            myViewModel.getDao().updateUser(member.getUid(), member); // Add the UserActivity for the member to Firebase
        }
    }

    public void cancel(View view) {
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
