package Controllers;

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
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }

    public void shutDown() {
        try {
            done = true;
            pool.shutdown();
            for (ConnectionHandler ch : connections) {
                broadcast("Server has shut down.");
                ch.shutDown();
            }
        } catch (Exception e) {
            // Handle exception
        }
    }

    public void handlePrivateMessage(String targetNickname, String message, ConnectionHandler sender) {
        boolean found = false;
        for (ConnectionHandler ch : connections) {
            if (ch.getNickName() != null && ch.getNickName().equals(targetNickname)) {
                ch.sendMessage(message);
                found = true;
                break;
            }
        }
        if (!found) {
            sender.sendMessage("User with nickname '%s' not found.".formatted(targetNickname));
        }
    }

    public List<ConnectionHandler> getConnections() {
        return connections;
    }
}
