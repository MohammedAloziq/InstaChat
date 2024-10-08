import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MySQLConnection {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/InstaChat"; // Replace with your database URL
        String username = "root"; // Replace with your MySQL username
        String password = ""; // Replace with your MySQL password

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database!");

            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM user";
            ResultSet rs = stmt.executeQuery(query);


            while (rs.next()) {
                int id = rs.getInt("user_id");
                String name = rs.getString("username");
                String email = rs.getString("email");

                System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("Database connection failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        UserRepository userRepository = new UserRepository();

        // Add a new user
//        User newUser = new User(1, "heis", "testa@example.com");
//        userRepository.addUser(newUser);

        // Get all users
        List<User> users = userRepository.getAllUsers();
        users.forEach(System.out::println);

        // Get a user by ID
        User user = userRepository.getUserById(1);
        if (user != null) {
            System.out.println("User found: " + user);
        }

        // Delete a user by ID
        userRepository.deleteUser(1);
    }


}
