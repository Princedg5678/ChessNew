package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameName;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MemoryGameDAO implements GameDAO{

    Map<Integer, GameData> gameMap = new HashMap<>();

    public Integer createGame(GameName gameName){
        int gameID = generateID();
        GameData newGame = new GameData(gameID, null, null, gameName.gameName(), new ChessGame());
        gameMap.put(gameID, newGame);
        return gameID;
    }

    private Integer generateID(){
        return new Random().nextInt(9000) + 1000;
    }

    public Map<Integer, GameData> listGames(){
        return gameMap;
    }

    public GameData findGame(Integer gameID){
        return gameMap.get(gameID);
    }

    public void addPlayer(GameData gameData, String playerColor, String username){
        GameData updatedData;
        if (playerColor.equalsIgnoreCase("WHITE")){
            updatedData = new GameData(gameData.gameID(), username, gameData.blackUsername(),
                    gameData.gameName(), gameData.game());
        }
        else {
            updatedData = new GameData(gameData.gameID(), gameData.whiteUsername(), username,
                    gameData.gameName(), gameData.game());
        }

        gameMap.put(gameData.gameID(), updatedData);
    }

    public void clearGames(){
        gameMap.clear();
    }

    public void updateGame(ChessGame game, int gameID){
        GameData currentData = findGame(gameID);
        GameData updatedData = new GameData(gameID, currentData.whiteUsername(), currentData.blackUsername(),
                currentData.gameName(), game);
        gameMap.replace(gameID, updatedData);
    }

    public void removePlayer(int gameID, String playerColor) throws DataAccessException {
        GameData currentData = findGame(gameID);
        if (playerColor.equalsIgnoreCase("WHITE")){
            GameData updatedData = new GameData(gameID, null, currentData.blackUsername(),
                    currentData.gameName(), currentData.game());
            gameMap.replace(gameID, updatedData);
        }
        else{
            GameData updatedData = new GameData(gameID, currentData.whiteUsername(), null,
                    currentData.gameName(), currentData.game());
            gameMap.replace(gameID, updatedData);
        }
    }

    public String getPlayerColor(int gameID, String username) throws DataAccessException {
        GameData currentData = findGame(gameID);
        String blackUsername = currentData.blackUsername();
        if (username.equalsIgnoreCase(blackUsername)){
            return "BLACK";
        }
        else {
            return "WHITE";
        }
    }


}
