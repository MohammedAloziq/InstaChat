package Models;

public class User {
    private int user_id;
    private String username;
    private String email;
    private String password = "password";
    private Boolean connectionStatus = false;

    public User(int id, String username, String email) {
        this.user_id = id;
        this.username = username;
        this.email = email;
    }

    public void setId(int id) {
        this.user_id = id;
    }

    public int getId() {
        return user_id;
    }

    public void SetUsername(String username) {
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

    public void toggleConnectionStatus(boolean status) {
        this.connectionStatus = !status;
    }

    public boolean getConncetionStatus() {
        return this.connectionStatus;
    }

    @Override
    public String toString() {
        return "Models.User{" + "user_id=" + user_id + ", username='" + username + '\'' + ", email='" + email + '\'' + ", password='" + password + '\'' + ", connectionStatus=" + connectionStatus + '}';
    }
}
