package Repository;

import Models.Message;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {
    protected static final String url = "jdbc:mysql://localhost:3306/InstaChat";
    protected static final String username = "root";
    protected static final String password = "";

    // Get messages by chat room
    public static List<Message> getAllMessages(int recepientUserId) {
        List<Message> messages = new ArrayList<>();
        String query = """
                SELECT m.recipient_type, m.content,  m.created_at, u.username AS sender_username
                \tFROM message m
                    JOIN user u ON m.sender_user_id = u.user_id
                    WHERE m.recipient_user_id = ? or m.recipient_type = ?
                    ORDER BY created_at"""; // Ensure to have a timestamp field

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, recepientUserId);
            pstmt.setString(2, "group");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Message message = new Message();
                message.setSenderUsername(rs.getString("sender_username"));
                message.setRecepientType(rs.getString("recipient_type"));
                message.setContent(rs.getString("content"));

                LocalDateTime dateTime = LocalDateTime.parse(rs.getString("created_at"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                message.setTime("%d:%d".formatted(dateTime.getHour(), dateTime.getMinute()));
                // Add other fields as necessary
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public static void addMessage(Message message) {
        String query = "INSERT INTO message (sender_user_id, recipient_chat_room_id, recipient_type, content) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, message.getSenderUserId());
            pstmt.setInt(2, message.getChatRoomId());
            pstmt.setString(3, "group");
            pstmt.setString(4, message.getContent());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPrivateMessage(Message message) {
        String query = "INSERT INTO message (sender_user_id, recipient_user_id, recipient_type, content) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, message.getSenderUserId());
            pstmt.setInt(2, message.getRecipientUserId());
            pstmt.setString(3, "private");
            pstmt.setString(4, message.getContent());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Message> getGroupMessages(int chatRoomId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM message WHERE recipient_chat_room_id = ? AND recipient_type = 'group' ORDER BY timestamp"; // Ensure to have a timestamp field

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, chatRoomId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Message message = new Message();
                message.setSenderUserId(rs.getInt("sender_user_id"));
                message.setChatRoomId(rs.getInt("recipient_chat_room_id"));
                message.setContent(rs.getString("content"));
                // Add other fields as necessary
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public static List<Message> getPrivateMessages(int userId) {
        List<Message> messages = new ArrayList<>();

        // Updated query to join the user table to get the sender's username
        String query = """
        SELECT m.*, u.username AS sender_username\s
        FROM message m
        JOIN user u ON m.sender_user_id = u.user_id
        WHERE m.recipient_user_id = ?\s
        AND m.recipient_type = 'private'
        ORDER BY m.timestamp
   \s""";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Message message = new Message();

                // Use the alias 'sender_username' from the query
                message.setSenderUsername(rs.getString("sender_username"));
                message.setRecipientUserId(rs.getInt("recipient_user_id"));
                message.setContent(rs.getString("content"));
                message.setTime(rs.getTimestamp("timestamp").toString().split(" ")[2]);
                // Add other fields as necessary

                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

}
