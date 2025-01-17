package dataaccess;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        createDataTables();
    }

    private final String[] statements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` longtext NOT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX (username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void createDataTables() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            for (String currentStatement: statements){
                try (PreparedStatement preparedStatement = conn.prepareStatement(currentStatement)) {
                    preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public boolean checkUser(String username) throws DataAccessException {

        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement =
                         conn.prepareStatement("SELECT username FROM users WHERE username = ?;")) {
                preparedStatement.setString(1, username);
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
    public void createUser(String username, String password, String email) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement =
                         conn.prepareStatement("INSERT INTO users (username, password, email) " +
                                 "VALUES (?, ?, ?);")) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearUsers() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement = conn.prepareStatement("TRUNCATE TABLE users")) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    //do this for the other functions

    @Override
    public String getPassword(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement preparedStatement =
                         conn.prepareStatement("SELECT password FROM users WHERE username = ?;")) {
                preparedStatement.setString(1, username);
                try (var resultStatement = preparedStatement.executeQuery()){
                    if (resultStatement.next()){
                        return resultStatement.getString("password");
                    }
                    else {
                        throw new DataAccessException("Error: unauthorized");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
