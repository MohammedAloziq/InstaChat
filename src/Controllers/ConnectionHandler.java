package Controllers;

import Models.Message;
import Models.User;
import Repository.MessageRepository;
import Repository.UserRepository;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ConnectionHandler implements Runnable {
    private final Socket client;
    private final ServerController serverController;
    User user;
    private BufferedReader in;
    private PrintWriter out;

    public ConnectionHandler(Socket client, ServerController serverController) {
        this.client = client;
        this.serverController = serverController;
        this.user = new User(); // Initialize a user object for sign-in and sign-up processes.
    }

    @Override
    public void run() {
        try {
            setupStreams();  // Setup input and output streams for communication
            processCommands();  // Handle user commands
        } catch (IOException e) {
            shutDown();  // If an error occurs, gracefully shut down the connection
        }
    }

    private void setupStreams() throws IOException {
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    private void processCommands() throws IOException {
        out.println("To sign in use '/in' or use '/up' to sign up.");
        String command = in.readLine();
        String[] messageSplit = command.split(" ", 3);

        if (command.startsWith("/in ")) {
            if (messageSplit.length != 3) {
                out.println("Invalid command. Use /in <email> <password>");
            } else {
                handleSignIn(command);
            }
        } else if (command.startsWith("/up")) {
            handleSignUp();
        } else if (command.startsWith("/quit")) {
            shutDown();
        } else {
            out.println("Invalid command. Please sign in or sign up to proceed.");
            processCommands();
        }
    }

    private void handleSignIn(String command) throws IOException {
        String[] messageSplit = command.split(" ", 4);

        if (messageSplit.length == 4 || messageSplit.length == 3) {
            String email = messageSplit[1];
            String password = messageSplit[2];

            this.user = UserRepository.getUserByEmail(email);
            if (this.user == null) {
                out.println("No user found with this email.");
                processCommands();
            } else if (this.user.getConnectionStatus().equals("online")) {
                out.println("User currently connected");
                processCommands();
            } else if (user.getPassword().equals(password)) {
                out.printf("Login successful. Welcome, %s%n", user.getUsername());
                user.setConnectionStatus("online");
                user.setSignedIn(true);
                UserRepository.updateUser(user);

                serverController.broadcast("%s joined the chat!".formatted(user.getUsername()));
                if (messageSplit[3] != null) switch (messageSplit[3]) {
                    case "all" -> sendAllPastMessages();
                    case "private" -> sendPastPrivateMessages();
                    case "group" -> sendPastGroupMessages();
                }

                if (user.isSignedIn()) {
                    while ((command = in.readLine()) != null) {
                        handleChatCommands(command);
                    }
                }
            } else {
                out.println("Incorrect password.");
                processCommands();
            }
        } else {
            out.println("Invalid command format. Use '/in <email> <password>'");
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
            if (this.user == null) {
                this.user = new User();
            }
            this.user.setEmail(email);

            out.println("Enter password: ");
            String password = in.readLine();
            user.setPassword(password);  // Password should be hashed in production

            // Save the user in the database
            UserRepository.addUser(user);
            out.println("Sign-up successful! Please sign in.");
            processCommands();
        }
    }

    private void sendAllPastMessages() {
        List<Message> messages = MessageRepository.getAllMessages(user.getId());
        for (Message message : messages) {
                sendMessage("%s %s: %s ".formatted(message.getTime(),
                        message.getSenderUsername() + (message.getRecepientType()
                                .equals("private") ? " (private)" : ""), message.getContent()));

        }
    }

    private void sendPastGroupMessages() {
        // Fetch and send group messages
        List<Message> groupMessages = MessageRepository.getGroupMessages(1);
        for (Message message : groupMessages) {
            sendMessage("%s: %s ".formatted(message.getSenderUsername(), message.getContent()));
        }
    }

    private void sendPastPrivateMessages() {
        // Fetch and send private messages
        List<Message> privateMessages = MessageRepository.getPrivateMessages(user.getId());
        for (Message message : privateMessages) {
            sendMessage("%s (private): %s".formatted(message.getSenderUsername(), message.getContent()));
        }
    }

    private void handleChatCommands(String command) throws IOException {
        if (command.startsWith("/private ")) {
            preparePrivateMessage(command);
        } else if (command.startsWith("/user ")) {
            handleChangeNickname(command);
        } else if (command.startsWith("/quit")) {
            serverController.broadcast("%s has left the chat!".formatted(user.getUsername()));
            this.shutDown();
        } else if (command.startsWith("/show")) {
            handleShowActiveUsers();
        } else if (command.startsWith("/adminShutdown")) {
            // Broadcast the shutdown message to all users
            serverController.broadcast("Server is shutting down. All connections will be closed.");
            // Trigger server shutdown
            serverController.shutDown();
            shutDown();  // Shutdown the admin connection itself
            System.exit(0);
        } else {
            serverController.broadcast("%s: %s".formatted(user.getUsername(), command));
            Message message = new Message();
            message.setChatRoomId(1); // Replace with actual chat room ID.
            message.setContent(command);
            message.setSenderUserId(user.getId());
            MessageRepository.addMessage(message);
        }
    }


    private void preparePrivateMessage(String command) {
        String[] messageSplit = command.split(" ", 3);
        if (messageSplit.length == 3) {
            String recipientUsername = messageSplit[1];
            String messageBody = messageSplit[2];
            serverController.handlePrivateMessage(this, recipientUsername, messageBody);
        } else {
            out.println("Invalid command format. Use /private <username> <message>.");
        }
    }

    private void handleChangeNickname(String command) throws IOException {
        String[] messageSplit = command.split(" ");
        if (messageSplit.length == 2) {
            String newNickname = messageSplit[1];
            String renameMessage = "%s renamed themselves to %s".formatted(user.getUsername(), newNickname);
            serverController.broadcast(renameMessage);
            user.setUsername(newNickname);
            out.printf("Successfully changed username to %s%n", user.getUsername());
        } else if (messageSplit.length >= 3) {
            out.println("Usernames should be one word. Enter /user <username>");
            command = in.readLine();
            handleChangeNickname(command);
        } else {
            out.println("Invalid command format. Use /user <username>");
        }
    }

    private void handleShowActiveUsers() {
        StringBuilder activeUsers = new StringBuilder("Active users: ");
        for (ConnectionHandler ch : serverController.getConnections()) {
            if (ch.user.getUsername() != null) {
                activeUsers.append(ch.user.getUsername()).append(", ");
            }
        }
        out.println(activeUsers.length() > 0 ? activeUsers.substring(0, activeUsers.length() - 2) : "No active users.");
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void shutDown() {
        try {
            if (user != null) {
                user.setConnectionStatus("offline");
                user.setSignedIn(false);
                UserRepository.updateUser(user);  // Update the user’s status in the database
            }

            if (in != null) {
                in.close();  // Close input stream
            }

            if (out != null) {
                out.close();  // Close output stream
            }

            if (client != null && !client.isClosed()) {
                client.close();  // Close the client socket
            }

            serverController.deleteConnection(this);  // Remove the connection from the server
        } catch (IOException e) {
            // Handle any exceptions if necessary
            e.printStackTrace();
        }
    }

}
