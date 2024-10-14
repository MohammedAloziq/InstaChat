package Models;

import java.sql.Timestamp;

public class Message {
    private int messageId;
    private String senderUsername;
    private int senderUserId;
    private int recipientUserId;  // Optional for private messages
    private int chatRoomId;       // Optional for group messages
    private String content;
    private String recepientType;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public Message(int messageId, int senderUserId, int recipientUserId, int chatRoomId, String content, Timestamp createdAt) {
        this.messageId = messageId;
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.chatRoomId = chatRoomId;
        this.content = content;
    }

    public Message(int senderUserId, int recipientUserId, String content) {
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.content = content;
    }

    public Message() {
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(int senderUserId) {
        this.senderUserId = senderUserId;
    }

    public int getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(int recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecepientType() {
        return recepientType;
    }

    public void setRecepientType(String recepientType) {
        this.recepientType = recepientType;
    }

    @Override
    public String toString() {
        return "Message{messageId=%d, senderUserId=%d, recipientUserId=%d, chatRoomId=%d, content='%s'}".formatted(messageId, senderUserId, recipientUserId, chatRoomId, content);
    }
}
