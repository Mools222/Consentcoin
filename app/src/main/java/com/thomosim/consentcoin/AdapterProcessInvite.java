package com.thomosim.consentcoin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thomosim.consentcoin.Persistence.InviteRequest;

import java.util.ArrayList;

public class AdapterProcessInvite extends RecyclerView.Adapter<AdapterProcessInvite.ViewHolderProcessInvite> {

    private ArrayList<InviteRequest> pendingInviteRequests;
    private Context context;
    private static final int REQUEST_CODE_PROCESS_INVITE = 4;

    public class ViewHolderProcessInvite extends RecyclerView.ViewHolder {
        public TextView textView1;
        public TextView textView2;

        public ViewHolderProcessInvite(@NonNull View v) {
            super(v);
            textView1 = v.findViewById(R.id.tv_process_invite_id);
            textView2 = v.findViewById(R.id.tv_process_invite_sender);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InviteRequest inviteRequest = pendingInviteRequests.get(getAdapterPosition());

                    Intent intent = new Intent(context, ProcessInviteActivity.class);
                    intent.putExtra("PR", inviteRequest);
                    intent.putExtra("POS", getAdapterPosition());
                    ((MainActivity) context).startActivityForResult(intent, REQUEST_CODE_PROCESS_INVITE);
                }
            });
        }
    }

    public AdapterProcessInvite(ArrayList<InviteRequest> pendingInviteRequests, Context context) {
        this.pendingInviteRequests = pendingInviteRequests;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterProcessInvite.ViewHolderProcessInvite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_process_invite_item, parent, false);
        return new AdapterProcessInvite.ViewHolderProcessInvite(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProcessInvite.ViewHolderProcessInvite holder, int position) {
        InviteRequest inviteRequest = pendingInviteRequests.get(position);

        holder.textView2.setText(inviteRequest.getOrganization());
    }

    @Override
    public int getItemCount() {
        return pendingInviteRequests.size();
    }

    public void updateData(ArrayList<InviteRequest> pendingInviteRequests) {
        this.pendingInviteRequests = pendingInviteRequests;
        notifyDataSetChanged();
    }
}
