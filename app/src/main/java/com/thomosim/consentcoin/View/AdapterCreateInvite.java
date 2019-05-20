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

public class AdapterCreateInvite extends RecyclerView.Adapter<AdapterCreateInvite.ViewHolderCreateInvite>{

    private ArrayList<User> associatedUsers;
    private ArrayList<User> checkedUsers;

    public class ViewHolderCreateInvite extends RecyclerView.ViewHolder {
        public TextView textView;
        public CheckBox checkBox;

        public ViewHolderCreateInvite(@NonNull View v) {
            super(v);
            textView = v.findViewById(R.id.tv_create_invite_id);
            checkBox = v.findViewById(R.id.cb_create_invite);

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

    public AdapterCreateInvite(ArrayList<User> associatedUsers) {
        this.associatedUsers = associatedUsers;
        checkedUsers = new ArrayList<>();
    }

    @NonNull
    @Override
    public AdapterCreateInvite.ViewHolderCreateInvite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_create_invite_item, parent, false);
        return new ViewHolderCreateInvite(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCreateInvite.ViewHolderCreateInvite holder, int position) {
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
