package com.thomosim.consentcoin.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thomosim.consentcoin.Persistence.ModelClass.User;
import com.thomosim.consentcoin.R;

import java.util.ArrayList;

public class AdapterCreateRequest extends RecyclerView.Adapter<AdapterCreateRequest.ViewHolderCreateRequest> {

    private ArrayList<User> associatedUsers;
    private ArrayList<User> checkedUsers;

    public class ViewHolderCreateRequest extends RecyclerView.ViewHolder {
        public TextView textView;
        public CheckBox checkBox;

        public ViewHolderCreateRequest(@NonNull View v) {
            super(v);
            textView = v.findViewById(R.id.tv_create_request_id);
            checkBox = v.findViewById(R.id.cb_create_request);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                    if (checkBox.isChecked()) {
                        checkedUsers.add(associatedUsers.get(getAdapterPosition()));
                    } else {
                        checkedUsers.remove(associatedUsers.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public AdapterCreateRequest(ArrayList<User> associatedUsers) {
        this.associatedUsers = associatedUsers;
        checkedUsers = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolderCreateRequest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_create_request_item, parent, false);
        return new ViewHolderCreateRequest(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCreateRequest holder, int position) {
        if (checkedUsers.contains(associatedUsers.get(position)))
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);

        User user = associatedUsers.get(position);
        holder.textView.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return associatedUsers.size();
    }

    public void updateData(ArrayList<User> associatedUsers) {
        this.associatedUsers = associatedUsers;
        notifyDataSetChanged();
    }

    public ArrayList<User> getCheckedUsers() {
        return checkedUsers;
    }
}

