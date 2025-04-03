package websocket.messages;

public class ErrorGameMessage extends ServerMessage{

    private final String errorMessage;

    public ErrorGameMessage(ServerMessageType type, String message, String e) {
        super(type, message);
        this.errorMessage = e;
    }

}


