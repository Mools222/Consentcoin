package com.thomosim.consentcoin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thomosim.consentcoin.Persistens.PermissionRequest;

import java.util.ArrayList;

public class AdapterProcessRequest extends RecyclerView.Adapter<AdapterProcessRequest.ViewHolderProcessRequest> {

    private ArrayList<PermissionRequest> pendingPermissionRequests;
    private Context context;
    private static final int REQUEST_CODE_PROCESS_REQUEST = 2;

    public class ViewHolderProcessRequest extends RecyclerView.ViewHolder {
        public TextView textView1;
        public TextView textView2;

        public ViewHolderProcessRequest(@NonNull View v) {
            super(v);
            textView1 = v.findViewById(R.id.tv_request_id);
            textView2 = v.findViewById(R.id.tv_request_sender);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, ProcessRequestActivity.class);
//                    intent.putExtra("tv1", textView1.getText().toString());
//                    intent.putExtra("tv2", textView2.getText().toString());
//                    context.startActivity(intent);

                    PermissionRequest permissionRequest = pendingPermissionRequests.get(getAdapterPosition());

                    Intent intent = new Intent(context, ProcessRequestActivity.class);
                    intent.putExtra("PR", permissionRequest);
                    intent.putExtra("POS", getAdapterPosition());
                    ((MainActivity) context).startActivityForResult(intent, REQUEST_CODE_PROCESS_REQUEST);
                }
            });
        }
    }

    public AdapterProcessRequest(ArrayList<PermissionRequest> pendingPermissionRequests, Context context) {
        this.pendingPermissionRequests = pendingPermissionRequests;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderProcessRequest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_process_request_item, parent, false);

        return new ViewHolderProcessRequest(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProcessRequest holder, int position) {
        PermissionRequest permissionRequest = pendingPermissionRequests.get(position);
        holder.textView1.setText(permissionRequest.getId());
        holder.textView2.setText(permissionRequest.getOrganization());
    }

    @Override
    public int getItemCount() {
        return pendingPermissionRequests.size();
    }

    public void updateData(ArrayList<PermissionRequest> pendingPermissionRequests) {
        this.pendingPermissionRequests = pendingPermissionRequests;
        notifyDataSetChanged();
    }
}
