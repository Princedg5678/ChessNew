package dataaccess;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO{
    @Override
    public String generateToken(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement =
                         conn.prepareStatement("INSERT INTO auth (authToken, username) " +
                                 "VALUES (?, ?);")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return authToken;
    }

    @Override
    public void deleteToken(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement =
                         conn.prepareStatement("DELETE FROM auth WHERE authToken = ?;")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public boolean checkToken(String authToken) throws DataAccessException {

        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement =
                         conn.prepareStatement("SELECT authToken FROM auth WHERE authToken = ?;")) {
                preparedStatement.setString(1, authToken);
                try (var resultStatement = preparedStatement.executeQuery()){
                    if (resultStatement.next()){
                        return true;
                    }
                }

            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        return false;
    }

    @Override
    public String getUsername(String token) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement =
                         conn.prepareStatement("SELECT username FROM auth WHERE authToken = ?;")) {
                preparedStatement.setString(1, token);
                try (var resultStatement = preparedStatement.executeQuery()){
                    if (resultStatement.next()){
                        return resultStatement.getString("username");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return "";
    }

    @Override
    public Map<String, String> getAuthData() throws DataAccessException {
        Map<String, String> authMap = new HashMap<>();
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM auth")) {
                try (var result = preparedStatement.executeQuery()) {
                    while (result.next()) {
                       String authToken = result.getString(1);
                       String username = result.getString(2);
                       authMap.put(authToken, username);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return authMap;
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement = conn.prepareStatement("TRUNCATE TABLE auth")) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    //test to make sure these work
}
