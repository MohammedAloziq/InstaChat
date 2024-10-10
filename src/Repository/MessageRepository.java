package Repository;

import Models.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {
    private String url = "jdbc:mysql://localhost:3306/InstaChat";
    private String username = "root";
    private String password = "";

    // Get messages by chat room
    public List<Message> getMessagesByChatRoom(int chatRoomId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM message WHERE chat_room_id = ? ORDER BY created_at ASC";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, chatRoomId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int messageId = rs.getInt("message_id");
                int senderUserId = rs.getInt("sender_user_id");
                String content = rs.getString("content");
                Timestamp createdAt = rs.getTimestamp("created_at");
                messages.add(new Message(messageId, senderUserId, 0, chatRoomId, content, createdAt));  // recipientUserId is 0 for group messages
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    // Add a new message to a chat room
    public void addMessage(Message message) {
        String query = "INSERT INTO message (sender_user_id, chat_room_id, content) VALUES (?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, message.getSenderUserId());
            pstmt.setInt(2, message.getChatRoomId());
            pstmt.setString(3, message.getContent());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
