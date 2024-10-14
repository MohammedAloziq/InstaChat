package Repository;

import Controllers.ConnectionHandler;
import Controllers.ServerController;
import Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {


    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";

        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("user_id");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String username = rs.getString("username");
                users.add(new User(id, email, password, username));
            }

        } catch (SQLException e) {
            System.out.println("Database connection failed");
            e.printStackTrace();
        }
        return users;
    }

    // Method to find a user by ID
    public static User getUserById(int id) {
        String query = "SELECT * FROM user WHERE user_id = ?";
        User user = null;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                String password = rs.getString("password");
                String username = rs.getString("username");
                user = new User(id, email, password, username);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }


    public static User getUserByEmail(String enteredEmail) {
        String query = "SELECT * FROM user WHERE email = ?";
        User user = null;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, enteredEmail);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("user_id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String connectionStatus = rs.getString("status");

                user = new User(id, email, password, username);
                if ("online".equalsIgnoreCase(connectionStatus)) {
                    user.setConnectionStatus("online");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    // Method to add a new user
    public static void addUser(User user) {
        String query = "INSERT INTO user (email, password) VALUES (?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPassword()); // Replace with actual password hashing
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a user by ID
    public void deleteUser(int id) {
        String query = "DELETE FROM user WHERE user_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean updateUser(User user) {
        String query = "UPDATE user SET email = ?, password = ?, username = ?, status = ? WHERE user_id = ?";
        boolean updated = false;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set the new values
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getConnectionStatus());
            pstmt.setInt(5, user.getId());

            // Execute update and check if any rows were affected
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                updated = true;  // Update successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updated;
    }

}