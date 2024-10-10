package Controllers;

import Models.User;
import java.io.*;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket client;
    private final ServerController serverController;
    private BufferedReader in;
    private PrintWriter out;
    private String nickName;
    private User user;

    public ConnectionHandler(Socket client, ServerController serverController) {
        this.client = client;
        this.serverController = serverController;
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
        out.println("To sign in '/in'");
        String command = in.readLine();

        if (command.startsWith("/in ")) {
            handleSignIn(command);
        } else {
            handleSignUp();
        }

        out.println("Your nickname: ");
        this.nickName = in.readLine();
        System.out.println(nickName + " connected!");
        serverController.broadcast(nickName + " joined the chat!");

        while ((command = in.readLine()) != null) {
            handleChatCommands(command);
        }
    }

    private void handleSignIn(String command) {
        String[] messageSplit = command.split(" ", 3);
        if (messageSplit.length == 3) {
            String username = messageSplit[1];
            String password = messageSplit[2];
            // TODO: Implement sign-in
        } else {
            out.println("Invalid command format. Use '/in <username> <password>'.");
        }
    }

    private void handleSignUp() {
        // TODO: Implement sign-up
    }

    private void handleChatCommands(String command) {
        if (command.startsWith("/private ")) {
            preparePrivateMessage(command);
        } else if (command.startsWith("/nick ")) {
            handleChangeNickname(command);
        } else if (command.startsWith("/quit")) {
            serverController.broadcast(nickName + " has left the chat!");
            shutDown();
        } else if (command.startsWith("/show")) {
            handleShowActiveUsers();
        } else {
            serverController.broadcast(nickName + ": " + command);
        }
    }

    private void preparePrivateMessage(String command) {
        String[] messageSplit = command.split(" ", 3);
        if (messageSplit.length == 3) {
            String targetNickname = messageSplit[1];
            String privateMessage = messageSplit[2];
            serverController.handlePrivateMessage(targetNickname, nickName + " (private): " + privateMessage, this);
        } else {
            out.println("Invalid command format. Use /private <nickname> <message>.");
        }
    }

    private void handleChangeNickname(String command) {
        String[] messageSplit = command.split(" ", 2);
        if (messageSplit.length == 2) {
            String newNickname = messageSplit[1];
            serverController.broadcast(nickName + " renamed themselves to " + newNickname);
            System.out.println(nickName + " renamed themselves to " + newNickname);
            nickName = newNickname;
            out.println("Successfully changed nickname to " + nickName);
        } else {
            out.println("No nickname provided.");
        }
    }

    private void handleShowActiveUsers() {
        StringBuilder activeUsers = new StringBuilder("Active users: ");
        for (ConnectionHandler ch : serverController.getConnections()) {
            if (ch.getNickName() != null) {
                activeUsers.append(ch.getNickName()).append(", ");
            }
        }
        out.println(activeUsers.length() > 0 ? activeUsers.substring(0, activeUsers.length() - 2) : "No active users.");
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getNickName() {
        return nickName;
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
