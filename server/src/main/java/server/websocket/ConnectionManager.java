package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session) {
        Connection connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String username){
        connections.remove(username);
    }

    public void broadcast(String excludedUser, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (Connection c: connections.values()){
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
            connections.remove(c.username);
        }
    }

}
