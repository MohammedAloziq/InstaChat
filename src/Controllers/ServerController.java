package Controllers;

import Models.User;
import Repository.UserRepository;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController implements Runnable {

    private final List<ConnectionHandler> connections;
    private boolean done;
    private ExecutorService pool;

    public ServerController(List<ConnectionHandler> connections) {
        this.connections = connections;
        this.done = false;
    }

    @Override
    public void run() {
        pool = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(9999)) {
            while (!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client, this);
                connections.add(handler);
                pool.execute(handler);
            }
        } catch (IOException e) {
            shutDown();
        }
    }

    public void broadcast(String message) {
        for (ConnectionHandler ch : connections) {
            if (ch != null && ch.user.isSignedIn()) {
                ch.sendMessage(message);
            }
        }
    }

    public void shutDown() {
        try {
            for (ConnectionHandler ch : connections) {
                broadcast("Server has shut down.");
//                ch.user.setConnectionStatus("offline");
//                UserRepository.updateUser(ch.user);
                ch.shutDown();
            }
            done = true;
            pool.shutdown();
        } catch (Exception e) {
            // Handle exception
        }
    }

    public User getRecepientUser(String recepientUsername, ConnectionHandler sender) {
        boolean found = false;
        User user = null;

        for (ConnectionHandler ch : connections) {
            if (ch.user.getUsername() != null && ch.user.getUsername().equals(recepientUsername)) {
                found = true;
                user = ch.user;
                break;
            }
        }
        if (!found) {
            sender.sendMessage("User with username '%s' not found.".formatted(recepientUsername));
        }
        return user;
    }


    public void handlePrivateMessage(String recepientUsername, String message, ConnectionHandler sender) {
        User receiver = getRecepientUser(recepientUsername, sender);
        for (ConnectionHandler ch : connections) {
            if (ch.user.getUsername() != null && ch.user.getEmail().equals(receiver.getEmail())) {

                ch.sendMessage(message);
                break;
            }
        }
    }

    public List<ConnectionHandler> getConnections() {
        return connections;
    }
}
