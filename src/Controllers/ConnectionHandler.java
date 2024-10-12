package Controllers;

import Models.User;
import Repository.UserRepository;

import java.io.*;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket client;
    private final ServerController serverController;
    private BufferedReader in;
    private PrintWriter out;
    private User user;

    public ConnectionHandler(Socket client, ServerController serverController) {
        this.client = client;
        this.serverController = serverController;
        this.user = new User();
    }

    @Override
    public void run() {
        try {
            setupStreams();
            processCommands();
        } catch (IOException e) {
            shutDown();
        }
    }

    private void setupStreams() throws IOException {
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    private void processCommands() throws IOException {
        out.println("To sign in '/in' or '/up' to sign up");
        String command = in.readLine();

        if (command.startsWith("/in ")) {
            handleSignIn(command);
        } else if (command.startsWith("/up")) {
            handleSignUp();
        } else {
            out.println("Invalid command. Please sign in or sign up to proceed.");
            processCommands();
        }

        out.println("Your nickname: ");
        user.setUsername(in.readLine());
        System.out.printf("%s connected!%n", user.getUsername());
        serverController.broadcast("%s joined the chat!".formatted(user.getUsername()));

        while ((command = in.readLine()) != null) {
            handleChatCommands(command);
        }
    }

    private void handleSignIn(String command) throws IOException {
        String[] messageSplit = command.split(" ", 3);
        if (messageSplit.length == 3) {
            String email = messageSplit[1];
            String password = messageSplit[2];

            // Sign-in logic
            this.user = UserRepository.getUserByEmail(email);

            System.out.println(user.getPassword());

            if (user.getPassword().equals(password)) {
                out.printf("Login successful. Welcome, %s%n", user.getEmail());
                System.out.println("Login successfully");
                user.setConnectionStatus("online");
            } else {
                out.println("Incorrect password.");
                processCommands();
            }
        } else {
            out.println("Invalid command format. Use '/in <email> <password>'.");
            processCommands();
        }
    }

    private void handleSignUp() throws IOException {
        out.println("Enter email: ");
        String email = in.readLine();

        if (UserRepository.getUserByEmail(email) != null) {
            out.println("Email already taken. Please try another email.");
            handleSignUp();  // Recursive call for another attempt
        } else {
            user.setEmail(email);

            out.println("Enter password: ");
            String password = in.readLine();
            user.setPassword(password);  // You should hash the password here

            // Save the user in the database
            UserRepository.addUser(user);
            out.println("Sign-up successful! Please sign in.");
        }
    }

    private void handleChatCommands(String command) {
        if (command.startsWith("/private ")) {
            preparePrivateMessage(command);
        } else if (command.startsWith("/nick ")) {
            handleChangeNickname(command);
        } else if (command.startsWith("/quit")) {
            serverController.broadcast("%s has left the chat!".formatted(user.getUsername()));
            user.setConnectionStatus("offline");
            shutDown();
        } else if (command.startsWith("/show")) {
            handleShowActiveUsers();
        } else if (command.startsWith("/help" ) || command.startsWith("/h") ) {
            // TODO: HANDLE HELP COMMAND
            out.println("help");
        } else {
            serverController.broadcast("%s: %s".formatted(user.getUsername(), command));
        }
    }

    private void preparePrivateMessage(String command) {
        String[] messageSplit = command.split(" ", 3);
        if (messageSplit.length == 3) {
            String targetNickname = messageSplit[1];
            String privateMessage = messageSplit[2];
            serverController.handlePrivateMessage(targetNickname, "%s (private): %s".formatted(user.getUsername(), privateMessage), this);
        } else {
            out.println("Invalid command format. Use /private <nickname> <message>.");
        }
    }

    private void handleChangeNickname(String command) {
        String[] messageSplit = command.split(" ", 2);
        if (messageSplit.length == 2) {
            String newNickname = messageSplit[1];
            serverController.broadcast("%s renamed themselves to %s".formatted(user.getUsername(), newNickname));
            System.out.printf("%s renamed themselves to %s%n", user.getUsername(), newNickname);
            user.setUsername(newNickname);
            out.printf("Successfully changed username to %s%n", user.getUsername());
        } else {
            out.println("No nickname provided.");
        }
    }

    private void handleShowActiveUsers() {
        StringBuilder activeUsers = new StringBuilder("Active users: ");
        for (ConnectionHandler ch : serverController.getConnections()) {
            if (ch.user.getUsername() != null) {
                activeUsers.append(ch.getNickName()).append(", ");
            }
        }
        out.println(!activeUsers.isEmpty() ? activeUsers.substring(0, activeUsers.length() - 2) : "No active users.");
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getNickName() {
        return user.getUsername();
    }

    public void shutDown() {
        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            // Handle exception
        }
    }
}
