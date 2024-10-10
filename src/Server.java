import Controllers.ServerController;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) {
        ArrayList<Controllers.ConnectionHandler> connections = new ArrayList<>();
        ServerController serverController = new ServerController(connections);
        serverController.run();
    }
}

// TODO: HANDLE USER CONNECTIVITY
// TODO: CONNECT TO DATABASE
// TODO: SAVE USERS TO DATABASE
// TODO: SAVE CHATS TO DATABASE
// TODO: CREATE A FRONTEND SERVER // SALMAN
// TODO: SHOW CHATS IN FRONTEND // SALMAN
// TODO: SEND MESSAGES TO BACKEND // SALMAN

// TODO: PREMIUM FEATURES
// TODO: PREMIUM USER PAYMENT
// TODO: IMPLEMENT BLOCKING 🚫 // EXTRA
// TODO: IMPLEMENT IMAGE TRANSFER // EXTRA
// TODO: VIDEO TRANSFER // EXTRA
// TODO: VOICE MESSAGING

