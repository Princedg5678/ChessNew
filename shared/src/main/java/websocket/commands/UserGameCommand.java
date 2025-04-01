package websocket.commands;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;
    private final String authToken;
    private final Integer gameID;
    private final String playerColor;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID,
                           String playerColor) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        HIGHLIGHT,
        REDRAW,
        LEAVE,
        RESIGN
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    public String getColor(){
        return playerColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand)) {
            return false;
        }
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID()) &&
                Objects.equals(getColor(), that.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID(), getColor());
    }
}
