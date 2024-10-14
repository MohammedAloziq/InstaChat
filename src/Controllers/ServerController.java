package Controllers;

import Models.Message;
import Models.User;
import Repository.MessageRepository;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerController implements Runnable {

    private static final List<ConnectionHandler> connections = new CopyOnWriteArrayList<>();
    private boolean done;
    private ExecutorService pool;

    public ServerController() {
        this.done = false;
    }

    @Override
    public void run() {
        pool = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(9999)) {
            System.out.println("Server is running on port 9999...");
            while (!done) {
                Socket client = server.accept();  // Accepting incoming connections
                System.out.println("Accepted connection from " + client.getInetAddress().getHostAddress());
                ConnectionHandler handler = new ConnectionHandler(client, this);
                connections.add(handler);
                pool.execute(handler);  // Handle each client in a separate thread
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    // ServerController.java
    public void shutDown() {
        try {
            // Notify all clients about the server shutdown
            broadcast("Server has shut down. All clients will be disconnected.");

            // Close all connections and remove them from the list
            for (ConnectionHandler ch : connections) {
                ch.shutDown();  // Close each connection
                deleteConnection(ch);  // Remove connection from list
            }

            // Stop the server from accepting new connections
            done = true;

            // Shutdown the thread pool
            pool.shutdown();

            // Force shutdown if threads do not terminate within the timeout period
            if (!pool.awaitTermination(15, TimeUnit.SECONDS)) {
                pool.shutdownNow();  // Force shutdown if necessary
            }

            System.out.println("Server has successfully shut down.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public User getRecepientUser(String recepientUsername, ConnectionHandler sender) {
        User user = null;

        for (ConnectionHandler ch : connections) {
            if (ch.user.getUsername() != null && ch.user.getUsername().equals(recepientUsername)) {
                user = ch.user;
                break;
            }
        }

        if (user == null) {
            sender.sendMessage("User with username '%s' not found.".formatted(recepientUsername));
        }

        return user;
    }

    public void handlePrivateMessage(ConnectionHandler sender, String recepientUsername, String messageBody) {
        User receiver = getRecepientUser(recepientUsername, sender);

        if (receiver == null) {
            return;  // Exit if the recipient user is not found
        }

        for (ConnectionHandler ch : connections) {
            if (ch.user.getUsername() != null && ch.user.getEmail().equals(receiver.getEmail())) {
                Message message = new Message(sender.user.getId(), receiver.getId(), messageBody);
                ch.sendMessage("%s (private): %s".formatted(sender.user.getUsername(), messageBody));
                MessageRepository.addPrivateMessage(message);
                break;
            }
        }
    }

    public List<ConnectionHandler> getConnections() {
        return connections;
    }

    public void deleteConnection(ConnectionHandler ch) {
        connections.remove(ch);
    }
}
