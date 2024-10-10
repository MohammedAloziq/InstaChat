package Models;

public class ChatRoom {
    private int chatRoomId;
    private String name;
    private int createdBy;

    public ChatRoom(int chatRoomId, String name, int createdBy) {
        this.chatRoomId = chatRoomId;
        this.name = name;
        this.createdBy = createdBy;
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "chatRoomId=" + chatRoomId +
                ", name='" + name + '\'' +
                ", createdBy=" + createdBy +
                '}';
    }
}
