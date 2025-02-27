package websocket.commands;

public class colorExtension extends UserGameCommand {

    public String playerColor;

    public colorExtension(UserGameCommand userGameCommand, String playerColor) {
        super(userGameCommand.getCommandType(), userGameCommand.getAuthToken(), userGameCommand.getGameID());
        this.playerColor = playerColor;
    }

    public String getColor(){
        return playerColor;
    }
}
