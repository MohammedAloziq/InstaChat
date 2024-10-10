package Repository;

import java.sql.*;



public class MySQLConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/InstaChat";
    private static final String USER = "root"; // Change to your MySQL username
    private static final String PASSWORD = ""; // Change to your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

//        Repository.UserRepository userRepository = new Repository.UserRepository();

//    public static void main(String[] args) {
//
//            String query = "SELECT * FROM user WHERE user_id = ?";
//            User user = null;
//
//            try (Connection conn = Repository.MySQLConnection.getConnection();
//                 PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//                pstmt.setInt(1, 11);
//                ResultSet rs = pstmt.executeQuery();
//
//                if (rs.next()) {
//                    String username = rs.getString("username");
//                    String email = rs.getString("email");
//                    user = new User(11, username, email);
//                }
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        System.out.println(user.toString());
//
//
//    }

}

//import java.util.List;
//
//public class Repository.MySQLConnection {
//
//    public static void main(String[] args) {
//        // Existing user-related code...
//
//        Repository.ChatRoomRepository chatRoomRepository = new Repository.ChatRoomRepository();
//        Repository.MessageRepository messageRepository = new Repository.MessageRepository();
//
//        // Create a new chat room
//        Models.ChatRoom newChatRoom = new Models.ChatRoom(1, "General", 1);
//        chatRoomRepository.addChatRoom(newChatRoom);
//
//        // Get all chat rooms
//        List<Models.ChatRoom> chatRooms = chatRoomRepository.getAllChatRooms();
//        chatRooms.forEach(System.out::println);
//
//        // Send a new message to the chat room
//        Models.Message newMessage = new Models.Message(1, 1, 0, 1, "Hello, everyone!", new Timestamp(System.currentTimeMillis()));
//        messageRepository.addMessage(newMessage);
//
//        // Get all messages from a chat room
//        List<Models.Message> messages = messageRepository.getMessagesByChatRoom(1);
//        messages.forEach(System.out::println);
//    }
//}
