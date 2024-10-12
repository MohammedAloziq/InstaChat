package Models;

public class User {
    private int user_id;
    private String username;
    private String email;
    private String password;
    private String connectionStatus = "offline";


    public User(int id, String email, String password) {
        this.user_id = id;
        this.email = email;
        this.password = password;
    }

    public User() {}

    public void setId(int id) {
        this.user_id = id;
    }

    public int getId() {
        return user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setConnectionStatus(String status) {
        this.connectionStatus = status;
    }

    public String getConncetionStatus() {
        return this.connectionStatus;
    }


    @Override
    public String toString() {
        return "Models.User{" + "user_id=" + user_id + ", username='" + username + '\'' + ", email='" + email + '\'' + ", password='" + password + '\'' + ", connectionStatus=" + connectionStatus + '}';
    }
}
