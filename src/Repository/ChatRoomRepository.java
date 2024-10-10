package Repository;

import Models.ChatRoom;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomRepository {

    public List<ChatRoom> getAllChatRooms() {
        List<ChatRoom> chatRooms = new ArrayList<>();
        String query = "SELECT * FROM chat_room";

        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int chatRoomId = rs.getInt("chat_room_id");
                String name = rs.getString("name");
                int createdBy = rs.getInt("created_by");
                chatRooms.add(new ChatRoom(chatRoomId, name, createdBy));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatRooms;
    }

    // Add a new chat room
    public void addChatRoom(ChatRoom chatRoom) {
        String query = "INSERT INTO chat_room (name, created_by) VALUES (?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, chatRoom.getName());
            pstmt.setInt(2, chatRoom.getCreatedBy());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
