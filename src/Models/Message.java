package Models;

import java.sql.Timestamp;

public class Message {
    private int messageId;
    private int senderUserId;
    private int recipientUserId;  // Optional for private messages
    private int chatRoomId;       // Optional for group messages
    private String content;

    public Message(int messageId, int senderUserId, int recipientUserId, int chatRoomId, String content, Timestamp createdAt) {
        this.messageId = messageId;
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.chatRoomId = chatRoomId;
        this.content = content;
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

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", senderUserId=" + senderUserId +
                ", recipientUserId=" + recipientUserId +
                ", chatRoomId=" + chatRoomId +
                ", content='" + content + '\'' +
                '}';
    }
}
