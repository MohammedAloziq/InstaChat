package Models;

public class User {
    private int user_id;
    private String username;
    private String email;
    private String password;
    private String connectionStatus = "offline";
    private boolean signedIn = false;

    public User(int id, String email, String password, String username) {
        this.user_id = id;
        this.username = username;
        this.email = email;
        this.password = password;

    }public User(int id, String email, String password, String username, String connectionStatus) {
        this.user_id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.connectionStatus = connectionStatus;
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

    public String getConnectionStatus() {
        return this.connectionStatus;
    }

    public boolean isSignedIn() {
        return signedIn;
    }

    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
    }

    @Override
    public String toString() {
        return "Models.User{user_id=%d, username='%s', email='%s', password='%s', connectionStatus=%s}".formatted(user_id, username, email, password, connectionStatus);
    }
}
