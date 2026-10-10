package com.example.autocare.chatbox;

public class ChatMessage {

    private String text;
    private boolean fromUser;
    private long timestamp;

    public ChatMessage() {
        // Constructor rỗng để tương thích Firestore
    }

    public ChatMessage(String text, boolean fromUser, long timestamp) {
        this.text = text;
        this.fromUser = fromUser;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public boolean isFromUser() {
        return fromUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFromUser(boolean fromUser) {
        this.fromUser = fromUser;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}