package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

    SQLAuthDAO authDao = new SQLAuthDAO();
    SQLGameDAO gameDao = new SQLGameDAO();
    SQLUserDAO userDao = new SQLUserDAO();

    Connection conn = DatabaseManager.getConnection();

    public DataAccessTests() throws DataAccessException {
    }

    @Test
    @Order(1)
    @DisplayName("clearUsersTest")
    public void clearUsers() throws DataAccessException, SQLException {
        userDao.createUser("Im","Just","Trash");
        userDao.createUser("Scrap","Metal","User");
        userDao.createUser("Into","The","Incinerator");

        userDao.clearUsers();

        try (PreparedStatement preparedStatement =
                     conn.prepareStatement("SELECT count(*) FROM users")){
            try (var result = preparedStatement.executeQuery()){
                if (result.next()){
                    assertEquals(0, result.getInt(1));
                }
            }
        }
    }


    @Test
    @Order(2)
    @DisplayName("createUserTest")
    public void create() throws DataAccessException, SQLException {
        userDao.clearUsers();
        userDao.createUser("Agh","Not","Again!");

        try (PreparedStatement preparedStatement =
                     conn.prepareStatement("SELECT username FROM users WHERE username = ?")){
            preparedStatement.setString(1, "Agh");
            try (var result = preparedStatement.executeQuery()){
                if (result.next()){
                    assertEquals("Agh", result.getString(1));
                }
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("createUserTestFailure")
    public void createFail() throws DataAccessException {
        userDao.clearUsers();
        userDao.createUser("Agh","Not","Again!");

        assertThrows(DataAccessException.class, ()-> userDao.createUser("Agh","Not","Again!"));
    }

    @Test
    @Order(4)
    @DisplayName("checkUserTest")
    public void check() throws DataAccessException {
        userDao.clearUsers();
        userDao.createUser("Agh","Not","Again!");

        assertTrue(userDao.checkUser("Agh"));

    }

    @Test
    @Order(5)
    @DisplayName("checkUserTestFailure")
    public void checkFail() throws DataAccessException {
        userDao.clearUsers();
        userDao.createUser("Agh","Not","Again!");

        assertFalse(userDao.checkUser("null"));

    }

    @Test
    @Order(6)
    @DisplayName("getPasswordTest")
    public void getPassword() throws DataAccessException {
        userDao.clearUsers();
        userDao.createUser("Agh", "Not", "Again!");

        assertEquals("Not", userDao.getPassword("Agh"));

    }

    @Test
    @Order(7)
    @DisplayName("getPasswordTestFailure")
    public void getPasswordFail() throws DataAccessException {
        userDao.clearUsers();
        userDao.createUser("Agh","Not","Again!");

        assertThrows(DataAccessException.class, ()-> userDao.getPassword("null"));

    }

    @Test
    @Order(8)
    @DisplayName("clearGamesTest")
    public void clearGames() throws DataAccessException, SQLException {
        gameDao.createGame(new GameName("Trash"));
        gameDao.createGame(new GameName("Junk"));
        gameDao.createGame(new GameName("Rubbish"));

        gameDao.clearGames();

        try (PreparedStatement preparedStatement =
                     conn.prepareStatement("SELECT count(*) FROM games")){
            try (var result = preparedStatement.executeQuery()){
                if (result.next()){
                    assertEquals(0, result.getInt(1));
                }
            }
        }
    }

    @Test
    @Order(9)
    @DisplayName("createGamesTest")
    public void createGames() throws DataAccessException, SQLException {
        gameDao.clearGames();

        gameDao.createGame(new GameName("MyGame"));

        try (PreparedStatement preparedStatement =
                     conn.prepareStatement("SELECT gameName FROM games WHERE gameName = ?")){
            preparedStatement.setString(1, "MyGame");
            try (var result = preparedStatement.executeQuery()){
                if (result.next()){
                    assertEquals("MyGame", result.getString(1));
                }
            }
        }
    }

    @Test
    @Order(10)
    @DisplayName("createGamesTestFail")
    public void createGamesFailure() throws DataAccessException {
        gameDao.clearGames();

        assertThrows(DataAccessException.class, () -> gameDao.createGame(null));
    }

    @Test
    @Order(11)
    @DisplayName("listGamesTest")
    public void listGames() throws DataAccessException {
        gameDao.clearGames();
        gameDao.createGame(new GameName("MyGame"));
        gameDao.createGame(new GameName("MyGame2"));

        Map<Integer, GameData> gameMap = gameDao.listGames();
        assertEquals(2, gameMap.size());
    }


    @Test
    @Order(12)
    @DisplayName("listGamesTestFail")
    public void listGamesFailure() throws DataAccessException {
        gameDao.clearGames();

        Map<Integer, GameData> gameMap = gameDao.listGames();
        assertEquals(0, gameMap.size());

    }

    @Test
    @Order(13)
    @DisplayName("findGameTest")
    public void findGame() throws DataAccessException {
        gameDao.clearGames();
        gameDao.createGame(new GameName("MyGame"));


        assertEquals("MyGame", gameDao.findGame(1).gameName());

    }

    @Test
    @Order(14)
    @DisplayName("findGameTestFail")
    public void findGameFailure() throws DataAccessException {
        gameDao.clearGames();
        gameDao.createGame(new GameName("MyGame"));

        assertThrows(DataAccessException.class, () -> gameDao.findGame(null));
    }

    @Test
    @Order(15)
    @DisplayName("addPlayerTest")
    public void addPlayer() throws DataAccessException {
        gameDao.clearGames();
        gameDao.createGame(new GameName("MyGame"));
        gameDao.addPlayer(gameDao.findGame(1), "WHITE", "Snow");
        gameDao.addPlayer(gameDao.findGame(1), "BLACK", "Coal");

        assertEquals("Snow", gameDao.findGame(1).whiteUsername());
        assertEquals("Coal", gameDao.findGame(1).blackUsername());
    }

    @Test
    @Order(16)
    @DisplayName("addPlayerTestFail")
    public void addPlayerFail() throws DataAccessException {
        gameDao.clearGames();
        gameDao.createGame(new GameName("MyGame"));
        gameDao.addPlayer(gameDao.findGame(1), "WHITE", "Snow");


        assertNotEquals("Coal", gameDao.findGame(1).blackUsername());

    }

    @Test
    @Order(17)
    @DisplayName("clearAuthTest")
    public void clearAuth() throws DataAccessException, SQLException {
        authDao.generateToken("Trash");
        authDao.generateToken("Junk");
        authDao.generateToken("Rubbish");

        authDao.clearAuthData();

        try (PreparedStatement preparedStatement =
                     conn.prepareStatement("SELECT count(*) FROM auth")){
            try (var result = preparedStatement.executeQuery()){
                if (result.next()){
                    assertEquals(0, result.getInt(1));
                }
            }
        }
    }

    @Test
    @Order(18)
    @DisplayName("generateTokenTest")
    public void generateToken() throws DataAccessException, SQLException {
        authDao.clearAuthData();
        authDao.generateToken("MyUsername");

        try (PreparedStatement preparedStatement =
                     conn.prepareStatement("SELECT username FROM auth WHERE username = ?")){
            preparedStatement.setString(1, "MyUsername");
            try (var result = preparedStatement.executeQuery()){
                if (result.next()){
                    assertEquals("MyUsername", result.getString(1));
                }
            }
        }
    }

    @Test
    @Order(19)
    @DisplayName("generateTokenTestFail")
    public void generateTokenFailure() throws DataAccessException {
        authDao.clearAuthData();
        assertThrows(DataAccessException.class, () -> authDao.generateToken(null));
    }

}


