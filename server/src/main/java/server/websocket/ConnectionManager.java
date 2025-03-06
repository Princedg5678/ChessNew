package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final ConcurrentHashMap<Integer, ArrayList<Connection>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, String username, Session session) {
        Connection connection = new Connection(username, session);
        if (!connections.containsKey(gameID)){
            ArrayList<Connection> connectionList = new ArrayList<>();
            connectionList.add(connection);
            connections.put(gameID, connectionList);
        }
        else {
            connections.get(gameID).add(connection);
        }
    }

    public void remove(Integer gameID){
        connections.remove(gameID);
    }

    public void broadcast(String excludedUser,
                          ServerMessage serverMessage, Integer gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (Connection c: connections.get(gameID)){
            if (c.session.isOpen()){
                if (!c.username.equals(excludedUser)) {
                    c.send(serverMessage.toString());
                }
            }
            else {
                removeList.add(c);
            }
        }

        for (Connection c: removeList){
            connections.get(gameID).remove(c);
        }
    }

    public void broadcastToRoot(LoadGameMessage gameMessage, Integer gameID, String username) throws IOException {
        for (Connection c: connections.get(gameID)) {
            if (c.session.isOpen()) {
                if (c.username.equals(username)) {
                    c.send(new Gson().toJson(gameMessage));
                }
            }
        }
    }

}
