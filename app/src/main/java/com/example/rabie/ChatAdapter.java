package com.example.rabie;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<Message> messages;
    private String teacherName;
    private String selectedUser;

    public ChatAdapter(List<Message> messages, String teacherName, String selectedUser) {
        this.messages = messages;
        this.teacherName = teacherName;
        this.selectedUser = selectedUser;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message.getType().equals("sentMessage")) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.messageContainer.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.removeRule(RelativeLayout.ALIGN_PARENT_START);
            holder.messageContainer.setLayoutParams(params);
            holder.messageContainer.setBackgroundResource(R.drawable.bg_message_sent);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.messageContainer.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.removeRule(RelativeLayout.ALIGN_PARENT_END);
            holder.messageContainer.setLayoutParams(params);
            holder.messageContainer.setBackgroundResource(R.drawable.bg_message_received);
        }

        holder.messageTextView.setText(message.getMessageText());
        holder.timestampTextView.setText(formatTimestamp(message.getTimestamp()));
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        LinearLayout messageContainer;
        TextView timestampTextView;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}


