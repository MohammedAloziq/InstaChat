package Repository;

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
                String username = rs.getString("username");
                String email = rs.getString("email");
                users.add(new User(id, username, email));
            }

        } catch (SQLException e) {
            System.out.println("Database connection failed");
            e.printStackTrace();
        }
        return users;
    }

    // Method to find a user by ID
    public User getUserById(int id) {
        String query = "SELECT * FROM user WHERE user_id = ?";
        User user = null;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                user = new User(id, username, email);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    // Method to add a new user
    public void addUser(User user) {
        String query = "INSERT INTO user (username, email, password) VALUES (?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, "hashed_password"); // Replace with actual password hashing
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
}