import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private final ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    public Server() {
        connections = new ArrayList<>();
        done = false;
    }

    @Override
    public void run() {

        try {
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler); // execute the run function
            }
        } catch (Exception e) {
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
            if (!server.isClosed()) {
                server.close();
            }
            for (ConnectionHandler ch : connections) {
                ch.shutDown();
            }
        } catch (IOException e) {
            /* ignore */
        }
    }

    class ConnectionHandler implements Runnable {

        private final Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickName;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }



        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Please enter a nickname: ");
                nickName = in.readLine();
                System.out.println(nickName + " connected!");
                broadcast(nickName + " joined the chat!");
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/private ")) {
                        String[] messageSplit = message.split(" ", 3);
                        if (messageSplit.length == 3) {
                            String targetNickname = messageSplit[1];
                            String privateMessage = messageSplit[2];
                            sendPrivateMessage(targetNickname, nickName + " (private): " + privateMessage);
                        } else {
                            out.println("Invalid private message format. Use /msg <nickname> <message>.");
                        }
                    } else if (message.startsWith("/nick ")) {
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            broadcast(nickName + " renamed themselves to " + messageSplit[1]);
                            System.out.println(nickName + " renamed themselves to " + messageSplit[1]);
                            nickName = messageSplit[1];
                            out.println("Successfully changed nickname to " + nickName);
                        } else {
                            out.println("No nickname provided.");
                        }
                    } else if (message.startsWith("/quit")) {
                        broadcast(nickName + " has left the chat!");
                        shutDown();
                    } else if (message.startsWith("/show")) {
                        whoIsLive();
                    } else {
                        broadcast(nickName + ": " + message);
                    }

                }
            } catch (IOException e) {
                shutDown();
            }
        }

        public void whoIsLive() {
            for (ConnectionHandler ch : connections) {
                if (ch != null) {
                    out.println(ch.nickName);
                }
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void sendPrivateMessage(String targetNickname, String message) {
            boolean found = false;
            for (ConnectionHandler ch : connections) {
                if (ch.nickName != null && ch.nickName.equals(targetNickname)) {
                    ch.sendMessage(message);
                    found = true;
                    break;
                }
            }
            if (!found) {
                out.println("User with nickname '" + targetNickname + "' not found.");
            }
        }


        public void shutDown() {
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}