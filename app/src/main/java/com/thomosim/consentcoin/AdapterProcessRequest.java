package com.thomosim.consentcoin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thomosim.consentcoin.Persistence.PermissionRequest;
import com.thomosim.consentcoin.Testing.MainActivity2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterProcessRequest extends RecyclerView.Adapter<AdapterProcessRequest.ViewHolderProcessRequest> {

    private ArrayList<PermissionRequest> pendingPermissionRequests;
    private Context context;
    private static final int REQUEST_CODE_PROCESS_REQUEST = 2;

    public class ViewHolderProcessRequest extends RecyclerView.ViewHolder {
        public TextView textView1;
        public TextView textView2;

        public ViewHolderProcessRequest(@NonNull View v) {
            super(v);
            textView1 = v.findViewById(R.id.tv_process_request_id);
            textView2 = v.findViewById(R.id.tv_process_request_sender);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermissionRequest permissionRequest = pendingPermissionRequests.get(getAdapterPosition());

                    Intent intent = new Intent(context, ProcessRequestActivity.class);
                    intent.putExtra("PR", permissionRequest);
                    intent.putExtra("POS", getAdapterPosition());
//                    ((MainActivity) context).startActivityForResult(intent, REQUEST_CODE_PROCESS_REQUEST);
                    ((MainActivity2) context).startActivityForResult(intent, REQUEST_CODE_PROCESS_REQUEST);
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
        Date date = permissionRequest.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        holder.textView1.setText(simpleDateFormat.format(date));
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
