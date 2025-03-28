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

    public void remove(Integer gameID, String username){
        ArrayList<Connection> connectionList = connections.get(gameID);
        for (Connection c: connectionList){
            if (c.username.equals(username)){
                connections.get(gameID).remove(c);
                break;
            }
        }
    }

    public void broadcast(String excludedUser,
                          ServerMessage serverMessage, Integer gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (Connection c: connections.get(gameID)){
            if (c.session.isOpen()){
                if (!c.username.equals(excludedUser)) {
                    c.send(new Gson().toJson(serverMessage));
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

    public void broadcastGame(String excludedUser,
                              LoadGameMessage gameMessage, Integer gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (Connection c: connections.get(gameID)){
            if (c.session.isOpen()){
                if (!c.username.equals(excludedUser)) {
                    c.send(new Gson().toJson(gameMessage));
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

    public void broadcastToRoot(ServerMessage userMessage, LoadGameMessage gameMessage,
                                Integer gameID, String username) throws IOException {
        for (Connection c: connections.get(gameID)) {
            if (c.session.isOpen()) {
                if (c.username.equals(username)) {
                    if (userMessage == null) {
                        c.send(new Gson().toJson(gameMessage));
                    }
                    else {
                        c.send(new Gson().toJson(userMessage));
                    }
                }
            }
        }
    }

}
