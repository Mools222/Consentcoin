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
import com.thomosim.consentcoin.R;

import java.util.ArrayList;

public class AdapterMainActivity extends RecyclerView.Adapter<AdapterMainActivity.ViewHolderMainActivity> {

    private ArrayList<String> stuff;
    private Context context;

    public class ViewHolderMainActivity extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvActivity;
        public TextView tvActivityText;
        public MaterialButton mbReadMore;
        public MaterialButton mbEdit;

        public ViewHolderMainActivity(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_cardView_name);
            tvActivity = v.findViewById(R.id.tv_cardView_activity);
            tvActivityText = v.findViewById(R.id.tv_cardView_activity_text);
            mbReadMore = v.findViewById(R.id.mb_cardView_read_more);
            mbEdit = v.findViewById(R.id.mb_cardView_edit);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Howdy ho", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public AdapterMainActivity(ArrayList<String> stuff, Context context) {
        this.stuff = stuff;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterMainActivity.ViewHolderMainActivity onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_main_activity_item, parent, false);
        return new ViewHolderMainActivity(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMainActivity.ViewHolderMainActivity holder, int position) {
//        String stu = stuff.get(position);
//        holder.tvName.setText(stu);
    }

    @Override
    public int getItemCount() {
        return stuff.size();
    }
}
