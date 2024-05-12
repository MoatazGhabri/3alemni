package com.example.rabie;

public class Message {
    private String senderName;
    private String messageText;
    private long timestamp;
    private  String teacherName;
    private  String type;


    public Message() {
    }

    public Message(String senderName, String messageText,String teacherName,String type,long timestamp) {
        this.senderName = senderName;
        this.messageText = messageText;
        this.teacherName = teacherName;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getSenderName() {
        return senderName;
    }
    public String getTeacherName() {
        return teacherName;
    }
    public String getType() {
        return type;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

