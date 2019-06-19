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
    private MaterialCheckBox materialCheckBoxCommercial, materialCheckBoxNoncommercial;
    private TextInputEditText textInputEditTextStartDate, textInputEditTextEndDate;
    private AdapterCreateRequest adapterCreateRequest;
    private GregorianCalendar startingDate;
    private SimpleDateFormat simpleDateFormat;
    private User organization;
    private ArrayList<User> members;
    private boolean sendRequestToAllMembers;
    private Date startDate, endDate;
    private ContractTypeEnum permissionType;
    private ContractScopeEnum personsIncluded;
    private Intent returnIntent;
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        myViewModel = MyViewModel.getInstance();

        Intent startIntent = getIntent();
        organization = (User) startIntent.getSerializableExtra("O");
        members = (ArrayList<User>) startIntent.getSerializableExtra("M");

        TextView textViewOrganization = findViewById(R.id.tv_create_request_organization_name);
        materialCheckBoxCommercial = findViewById(R.id.cb_create_request_commercial);
        materialCheckBoxNoncommercial = findViewById(R.id.cb_create_request_noncommercial);
        textInputEditTextStartDate = findViewById(R.id.et_create_request_start_date);
        textInputEditTextEndDate = findViewById(R.id.et_create_request_end_date);
        textViewOrganization.setText(organization.getOrganizationName());

        setPersonsIncluded();
        setDates();
        setReceivers();

        returnIntent = new Intent();
    }

    public void setPersonsIncluded() {
        RadioGroup radioGroupPersons = findViewById(R.id.rg_create_request_persons);
        radioGroupPersons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_create_request_members) {
                    personsIncluded = ContractScopeEnum.MYSELF;
                } else if (checkedId == R.id.rb_create_request_members_and_wards) {
                    personsIncluded = ContractScopeEnum.MYSELF_AND_WARDS;
                }
            }
        });
    }

    public void setDates() {
        startingDate = new GregorianCalendar();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        textInputEditTextStartDate.setText(simpleDateFormat.format(startingDate.getTime()));
        textInputEditTextEndDate.setText(simpleDateFormat.format(startingDate.getTime()));
        startDate = startingDate.getTime();
        endDate = startingDate.getTime();
    }

    public void setReceivers() {
        final RecyclerView RECYCLER_VIEW = findViewById(R.id.rv_create_request);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this); // The layout manager calls the adapter's onCreateViewHolder() method.
        RECYCLER_VIEW.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(RECYCLER_VIEW.getContext(), layoutManager.getOrientation()); // Creates a divider between items
        RECYCLER_VIEW.addItemDecoration(dividerItemDecoration);
        adapterCreateRequest = new AdapterCreateRequest(members);
        RECYCLER_VIEW.setAdapter(adapterCreateRequest);
        RECYCLER_VIEW.setVisibility(View.GONE);

        final ArrayList<User> MEMBERS_SEARCH_LIST = new ArrayList<>();
        final TextInputEditText SEARCH_FIELD = findViewById(R.id.et_create_request);
        SEARCH_FIELD.setVisibility(View.GONE);
        SEARCH_FIELD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                MEMBERS_SEARCH_LIST.clear();
                for (User member : members) {
                    if (member.getEmail().contains(s))
                        MEMBERS_SEARCH_LIST.add(member);
                }
                adapterCreateRequest.updateData(MEMBERS_SEARCH_LIST);
            }
        });

        sendRequestToAllMembers = true;
        RadioGroup radioGroupReceiver = findViewById(R.id.rg_create_request_receiver);
        radioGroupReceiver.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_create_request_all) {
                    RECYCLER_VIEW.setVisibility(View.GONE);
                    SEARCH_FIELD.setVisibility(View.GONE);
                    sendRequestToAllMembers = true;
                } else if (checkedId == R.id.rb_create_request_select) {
                    RECYCLER_VIEW.setVisibility(View.VISIBLE);
                    SEARCH_FIELD.setVisibility(View.VISIBLE);
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
        }, startingDate.get(Calendar.YEAR), startingDate.get(Calendar.MONTH), startingDate.get(Calendar.DAY_OF_MONTH));
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
        }, startingDate.get(Calendar.YEAR), startingDate.get(Calendar.MONTH), startingDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void findPermissionType() {
        if (materialCheckBoxCommercial.isChecked() && materialCheckBoxNoncommercial.isChecked())
            permissionType = ContractTypeEnum.NON_COMMERCIAL_AND_COMMERCIAL_USE; // Commercial + non-commercial
        else if (materialCheckBoxCommercial.isChecked())
            permissionType = ContractTypeEnum.COMMERCIAL_USE; // Commercial
        else if (materialCheckBoxNoncommercial.isChecked())
            permissionType = ContractTypeEnum.NON_COMMERCIAL_USE; // Non-commercial
    }

    public void send(View view) {
        findPermissionType();

        if (permissionType == null) // Check if permission type has been chosen
            Toast.makeText(this, getString(R.string.toast_select_purpose), Toast.LENGTH_SHORT).show();
        else if (!sendRequestToAllMembers && adapterCreateRequest.getCheckedUsers().size() == 0) // Check if at least one receiver has been chosen
            Toast.makeText(this, getString(R.string.toast_select_member), Toast.LENGTH_SHORT).show();
        else if (startDate.compareTo(endDate) != -1) // Check if the end date comes after the start date
            Toast.makeText(this, getString(R.string.toast_select_date), Toast.LENGTH_SHORT).show();
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
        Date creationDate = new Date();
        for (User member : memberList) {
            String memberName = member.getMiddleName() == null ? member.getFirstName() + " " + member.getLastName() : member.getFirstName() + " " + member.getMiddleName() + " " + member.getLastName();
            myViewModel.getDao().addPermissionRequest(organization.getOrganizationName(), organization.getUid(), memberName, member.getUid(), permissionType, creationDate, startDate, endDate, personsIncluded); // Add the PermissionRequest to Firebase

            addUserActivity(member, member.getUid(), member.getUserActivities(), "RPR", memberName, organization.getOrganizationName(), creationDate); // "RPR" = Receive Permission Request
            addUserActivity(organization, organization.getUid(), organization.getUserActivities(), "CPR", memberName, organization.getOrganizationName(), creationDate); // "CPR" = Create Permission Request
        }
    }

    public void addUserActivity(User user, String uid, ArrayList<UserActivity> userActivities, String activityCode, String memberName, String organizationName, Date date) {
        if (userActivities == null)
            userActivities = new ArrayList<>();
        userActivities.add(0, new UserActivity(activityCode, memberName, organizationName, date));
        user.setUserActivities(userActivities);
        myViewModel.getDao().updateUser(uid, user);
    }

    public void cancel(View view) {
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
