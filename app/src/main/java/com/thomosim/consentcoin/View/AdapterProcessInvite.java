package com.thomosim.consentcoin.View;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thomosim.consentcoin.Persistence.ModelClass.InviteRequest;
import com.thomosim.consentcoin.R;

import java.util.ArrayList;

public class AdapterProcessInvite extends RecyclerView.Adapter<AdapterProcessInvite.ViewHolderProcessInvite> {
    private ArrayList<InviteRequest> pendingInviteRequests;
    private Context context;
    private static final int REQUEST_CODE_PROCESS_INVITE = 4;

    public class ViewHolderProcessInvite extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolderProcessInvite(@NonNull View v) {
            super(v);
            textView = v.findViewById(R.id.tv_process_invite_sender);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InviteRequest inviteRequest = pendingInviteRequests.get(getAdapterPosition());

                    Intent intent = new Intent(context, ProcessInviteActivity.class);
                    intent.putExtra("PI", inviteRequest);
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
        return new ViewHolderProcessInvite(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProcessInvite.ViewHolderProcessInvite holder, int position) {
        InviteRequest inviteRequest = pendingInviteRequests.get(position);
        String text = context.getString(R.string.text_sender) + inviteRequest.getOrganizationName();

        holder.textView.setText(text);
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
