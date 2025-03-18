package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameName;

import java.util.Map;

public interface GameDAO {
    public Integer createGame(GameName gameName) throws DataAccessException;
    public Map<Integer, GameData> listGames() throws DataAccessException;
    public GameData findGame(Integer gameID) throws DataAccessException;
    public void addPlayer(GameData gameData, String playerColor, String username) throws DataAccessException;
    public void clearGames() throws DataAccessException;
    public void updateGame(ChessGame game, int gameID) throws DataAccessException;

}
