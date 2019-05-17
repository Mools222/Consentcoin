package com.thomosim.consentcoin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterMainActivity extends RecyclerView.Adapter<AdapterMainActivity.ViewHolderMainActivity> {

    public class ViewHolderMainActivity extends RecyclerView.ViewHolder {

        public ViewHolderMainActivity(@NonNull View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    public AdapterMainActivity() {
    }

    @NonNull
    @Override
    public AdapterMainActivity.ViewHolderMainActivity onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_main_activity_item, parent, false);
        return new ViewHolderMainActivity(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMainActivity.ViewHolderMainActivity holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
