package com.example.rabie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MessageListAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> userList;
    private Map<String, Message> lastMessageMap;

    public MessageListAdapter(Context context, List<String> userList, Map<String, Message> lastMessageMap) {
        super(context, 0, userList);
        mContext = context;
        this.userList = userList;
        this.lastMessageMap = lastMessageMap;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_msg, parent,false);

        String currentUser = userList.get(position);
        TextView userName = listItem.findViewById(R.id.userName);
        TextView lastMessage = listItem.findViewById(R.id.lastMessage);
        TextView lastMessageTime = listItem.findViewById(R.id.lastMessageTime);

        userName.setText(currentUser);

        Message lastMessageObj = lastMessageMap.get(currentUser);
        if (lastMessageObj != null) {
            String messageText = lastMessageObj.getMessageText();
            long timestamp = lastMessageObj.getTimestamp();
            lastMessage.setText(messageText);
            String formattedTime = formatTimestamp(timestamp);
            lastMessageTime.setText(formattedTime);
        } else {
            lastMessage.setText("No messages");
            lastMessageTime.setText("");
        }

        return listItem;
    }

    private String formatTimestamp(long timestamp) {
        Date date = new Date(timestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        return sdf.format(date);
    }
}
