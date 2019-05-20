package com.thomosim.consentcoin.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
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

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Hint: Swipe to delete", Toast.LENGTH_SHORT).show();
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

        switch (userActivity.getActivityCode()) {
            case "UC":
                holder.tvActivity.setText("User created");
                holder.tvActivityText.setText("Your user was created.");
                break;
            case "CPR":
                holder.tvActivity.setText("Permission request sent");
                holder.tvActivityText.setText("You sent a permission request to " + userActivity.getMemberName() + ".");
                break;
            case "RPR":
                holder.tvActivity.setText("Permission request recieved");
                holder.tvActivityText.setText("You recieved a permission request from " + userActivity.getOrganizationName() + ".");
                break;
            case "APR":
                holder.tvActivity.setText("Consentcoin recieved");
                holder.tvActivityText.setText("You recieved a Consentcoin by accepting the permission request from " + userActivity.getOrganizationName() + ".");
                break;
            case "RAPR":
                holder.tvActivity.setText("Consentcoin recieved");
                holder.tvActivityText.setText("You recieved a Consentcoin as " + userActivity.getMemberName() + " accepted your permission request.");
                break;
            case "DPR":
                holder.tvActivity.setText("Permission request denied");
                holder.tvActivityText.setText("You denied the permission request from " + userActivity.getOrganizationName() + ".");
                break;
            case "RDPR":
                holder.tvActivity.setText("Permission request denied");
                holder.tvActivityText.setText(userActivity.getMemberName() + " denied your permission request.");
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
