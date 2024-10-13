import Controllers.ServerController;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) {
        ArrayList<Controllers.ConnectionHandler> connections = new ArrayList<>();
        ServerController serverController = new ServerController(connections);
        serverController.run();
    }
}


