package websocket;

import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void notify(String stringMessage);
}
