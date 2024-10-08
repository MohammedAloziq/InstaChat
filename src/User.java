public class User {
    private int user_id;
    private String username;
    private String email;

    public User(int id, String username, String email) {
        this.user_id = id;
        this.username = username;
        this.email = email;
    }

    // Getters and setters
    public int getId() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "ID: " + user_id + ", Username: " + username + ", Email: " + email;
    }
}
