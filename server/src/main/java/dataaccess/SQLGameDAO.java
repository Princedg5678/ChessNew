package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.GameID;
import model.GameName;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SQLGameDAO implements GameDAO {
    @Override
    public Integer createGame(GameName gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement =
                         conn.prepareStatement("INSERT INTO games (whiteUsername, blackUsername, " +
                                 "gameName, game) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, null);
                preparedStatement.setString(2, null);
                preparedStatement.setString(3, gameName.gameName());
                preparedStatement.setString(4, new Gson().toJson(new ChessGame()));
                preparedStatement.executeUpdate();
                var returnKeys = preparedStatement.getGeneratedKeys();
                if (returnKeys.next()){
                    return returnKeys.getInt(1);
                }
            }

        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        return null;
    }

    @Override
    public Map<Integer, GameData> listGames() throws DataAccessException {
        Map<Integer, GameData> gameMap = new HashMap<>();
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM games")) {
                try (var result = preparedStatement.executeQuery()){
                    while (result.next()){
                        int gameID = result.getInt(1);
                        GameData currentGame = makeGame(result);
                        gameMap.put(gameID, currentGame);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        return gameMap;
    }

    @Override
    public GameData findGame(Integer gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement =
                         conn.prepareStatement("SELECT * FROM games WHERE gameID = ?;")) {
                preparedStatement.setInt(1, gameID);
                try (var result = preparedStatement.executeQuery()){
                    if (result.next()){
                        return makeGame(result);
                    }
                    else {
                        throw new DataAccessException("Error: unauthorized");
                    }
                }
            }
        }  catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private GameData makeGame(ResultSet result) throws SQLException{
        int gameID = result.getInt(1);
        String whiteUsername = result.getString(2);
        String blackUsername = result.getString(3);
        String gameName = result.getString(4);
        ChessGame game = new Gson().fromJson(result.getString(5), ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public void addPlayer(GameData gameData, String playerColor, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            int gameID = gameData.gameID();
            if (playerColor.equalsIgnoreCase("WHITE")) {
                try (PreparedStatement preparedStatement = conn.prepareStatement("UPDATE games " +
                        "SET whiteUsername = ? WHERE gameID = ?")) {

                    preparedStatement.setInt(2, gameID);
                    preparedStatement.setString(1, username);
                    preparedStatement.executeUpdate();
                }
            }
            else {
                try (PreparedStatement preparedStatement = conn.prepareStatement("UPDATE games " +
                        "SET blackUsername = ? WHERE gameID = ?")) {

                    preparedStatement.setInt(2, gameID);
                    preparedStatement.setString(1, username);
                    preparedStatement.executeUpdate();
                }
            }

        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement = conn.prepareStatement("TRUNCATE TABLE games")) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    public void updateGame(ChessGame game, int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement = conn.prepareStatement("UPDATE games "  +
                    "SET game = ? WHERE gameID = ?")) {
                preparedStatement.setString(1, new Gson().toJson(game));
                preparedStatement.setInt(2, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void removePlayer(int gameID, String playerColor) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            if (playerColor.equalsIgnoreCase("WHITE")){
                try (PreparedStatement preparedStatement = conn.prepareStatement("UPDATE games "
                        + "SET whiteUsername = ? WHERE gameID = ?")) {
                    preparedStatement.setString(1, null);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            }
            else {
                try (PreparedStatement preparedStatement = conn.prepareStatement("UPDATE games "
                        + "SET blackUsername = ? WHERE gameID = ?")) {
                    preparedStatement.setString(1, null);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            }

        }
        catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public String getPlayerColor(int gameID, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT whiteUsername, " +
                    "blackUsername FROM games WHERE gameID = ?;")) {
                preparedStatement.setInt(1, gameID);
                try (var result = preparedStatement.executeQuery()){
                    if (result.next()){
                        String whiteUsername = result.getString(1);
                        String blackUsername = result.getString(2);
                        if (username.equalsIgnoreCase(whiteUsername)){
                            return "WHITE";
                        }
                        else if (username.equalsIgnoreCase(blackUsername)){
                            return "BLACK";
                        }
                        else {
                            return null;
                        }
                    }
                    else {
                        throw new DataAccessException("Error: game does not exist");
                    }
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }


}

