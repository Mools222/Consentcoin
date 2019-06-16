package com.thomosim.consentcoin.View;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.thomosim.consentcoin.Persistence.ModelClass.UserActivity;
import com.thomosim.consentcoin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterMainActivity extends RecyclerView.Adapter<AdapterMainActivity.ViewHolderMainActivity> {
    private Context context;
    private ArrayList<UserActivity> userActivities;
    public class ViewHolderMainActivity extends RecyclerView.ViewHolder {
        public TextView tvDate;
        public TextView tvActivity;
        public TextView tvActivityText;
        public MaterialButton mbReadMore;

        public ViewHolderMainActivity(@NonNull View v) {
            super(v);
            tvDate = v.findViewById(R.id.tv_cardView_date);
            tvActivity = v.findViewById(R.id.tv_cardView_activity);
            tvActivityText = v.findViewById(R.id.tv_cardView_activity_text);
            mbReadMore = v.findViewById(R.id.mb_cardView_read_more);
            mbReadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, context.getString(R.string.informationText),Toast.LENGTH_LONG).show();
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, context.getString(R.string.toast_swipe_to_delete), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public AdapterMainActivity(Context context) {
        this.context = context;
        userActivities = new ArrayList<>();
    }

    @NonNull
    @Override
    public AdapterMainActivity.ViewHolderMainActivity onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_main_activity_item, parent, false);
        return new ViewHolderMainActivity(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMainActivity.ViewHolderMainActivity holder, int position) {
        UserActivity userActivity = userActivities.get(position);
        Date date = userActivity.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        holder.tvDate.setText(simpleDateFormat.format(date));
        String text;

        switch (userActivity.getActivityCode()) {
            case "UC":
                holder.tvActivity.setText(context.getString(R.string.text_user_created));
                holder.tvActivityText.setText(context.getString(R.string.text_your_user_created));
                break;
            case "CPR":
                holder.tvActivity.setText(context.getString(R.string.text_request_sent));
                text = context.getString(R.string.text_your_request_sent) + userActivity.getMemberName() + context.getString(R.string.dot);
                holder.tvActivityText.setText(text);
                break;
            case "RPR":
                holder.tvActivity.setText(context.getString(R.string.text_request_received));
                text = context.getString(R.string.text_you_received_request) + userActivity.getOrganizationName() + context.getString(R.string.dot);
                holder.tvActivityText.setText(text);
                break;
            case "APR":
                holder.tvActivity.setText(context.getString(R.string.text_consentcoin_received));
                text = context.getString(R.string.text_user_received_consentcoin) + userActivity.getOrganizationName() + context.getString(R.string.dot);
                holder.tvActivityText.setText(text);
                break;
            case "RAPR":
                holder.tvActivity.setText(context.getString(R.string.text_consentcoin_received));
                text = context.getString(R.string.text_org_received_consentcoin) + userActivity.getMemberName() + context.getString(R.string.text_user_accepted_request);
                holder.tvActivityText.setText(text);
                break;
            case "DPR":
                holder.tvActivity.setText(context.getString(R.string.text_permission_denied));
                text = context.getString(R.string.text_request_denied_user) + userActivity.getOrganizationName() + context.getString(R.string.dot);
                holder.tvActivityText.setText(text);
                break;
            case "RDPR":
                holder.tvActivity.setText(R.string.text_permission_denied);
                text = userActivity.getMemberName() + context.getString(R.string.text_request_denied_org);
                holder.tvActivityText.setText(text);
                break;
            case "DC":
                holder.tvActivity.setText(context.getString(R.string.text_consentcoin_revoked));
                text = context.getString(R.string.text_revoked_part_one) + userActivity.getOrganizationName() + context.getString(R.string.text_revoked_part_two);
                holder.tvActivityText.setText(text);
                break;
            case "RDC":
                holder.tvActivity.setText(context.getString(R.string.text_consentcoin_revoked));
                text = context.getString(R.string.text_revoked_part_one) + userActivity.getMemberName() + context.getString(R.string.text_revoked_part_two);
                holder.tvActivityText.setText(text);
                break;
            case "CIR":
                holder.tvActivity.setText("Invite request sent");
                holder.tvActivityText.setText("You sent an invite request to " + userActivity.getMemberName() + ".");
                break;
            case "RIR":
                holder.tvActivity.setText("Invite request received");
                holder.tvActivityText.setText("You received an invite request from " + userActivity.getOrganizationName() + ".");
                break;
            case "AIR":
                holder.tvActivity.setText("Invite request accepted");
                holder.tvActivityText.setText("You have accepted an invite request from " + userActivity.getOrganizationName() + ".");
                break;
            case "RAIR":
                holder.tvActivity.setText("Invite request accepted");
                holder.tvActivityText.setText("Your invite request to " + userActivity.getMemberName() + " has been accepted.");
                break;
            case "DIR":
                holder.tvActivity.setText("Invite request denied");
                holder.tvActivityText.setText("You have denied an invite request from " + userActivity.getOrganizationName() + ".");
                break;
            case "RDIR":
                holder.tvActivity.setText("Invite request denied");
                holder.tvActivityText.setText("Your invite request to " + userActivity.getMemberName() + " has been denied.");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return userActivities.size();
    }

    public void updateData(ArrayList<UserActivity> userActivities) {
        this.userActivities = userActivities;
        notifyDataSetChanged();
    }
}
