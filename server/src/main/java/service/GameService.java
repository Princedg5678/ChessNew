package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import model.GameList;
import model.GameName;
import model.GameResult;

import java.util.ArrayList;
import java.util.Map;

public class GameService {

    AuthDAO authDAO;
    GameDAO gameDAO;

    public GameService(AuthDAO authDao, GameDAO gameDao){
        this.gameDAO = gameDao;
        this.authDAO = authDao;
    }


    public GameList listGames(String authToken) throws DataAccessException {
        if (!authDAO.checkToken(authToken)){
            throw new DataAccessException("Error: unauthorized");
        }
        Map<Integer, GameData> gameMap = gameDAO.listGames();
        ArrayList<GameResult> gameResults = new ArrayList<>();

        for (GameData gameData: gameMap.values()){
            GameResult tempResult = new GameResult(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName());
            gameResults.add(tempResult);
        }


        return new GameList(gameResults);
    }

    public Integer createGame(String authToken, GameName gameName) throws DataAccessException {
        if (!authDAO.checkToken(authToken)){
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameName == null){
            throw new DataAccessException("Error: bad request");
        }
        return gameDAO.createGame(gameName);
    }

    public void joinGame(String authToken, String playerColor, Integer gameID) throws DataAccessException{
        if (!authDAO.checkToken(authToken)){
            throw new DataAccessException("Error: unauthorized");
        }
        if (playerColor == null || gameID == null){
            throw new DataAccessException("Error: bad request");
        }

        GameData gameData = gameDAO.findGame(gameID);

        if (gameData == null){
            throw new DataAccessException("Error: bad request");
        }

        String username = authDAO.getUsername(authToken);

        if (playerColor.equalsIgnoreCase("WHITE")){
            if (gameData.whiteUsername() != null){
                throw new DataAccessException("Error: already taken");
            }
            else {
                gameDAO.addPlayer(gameData, playerColor, username);
            }
        }
        else if (playerColor.equalsIgnoreCase("BLACK")){
            if (gameData.blackUsername() != null){
                throw new DataAccessException("Error: already taken");
            }
            else {
                gameDAO.addPlayer(gameData, playerColor, username);
            }
        }
        else {
            throw new DataAccessException("Error: bad request");
        }


    }


}
