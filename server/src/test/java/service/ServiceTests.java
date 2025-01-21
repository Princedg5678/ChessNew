package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {

    MemoryAuthDAO authDao = new MemoryAuthDAO();
    MemoryGameDAO gameDao = new MemoryGameDAO();
    MemoryUserDAO userDao = new MemoryUserDAO();
    UserService userService = new UserService(authDao, userDao);
    GameService gameService = new GameService(authDao, gameDao);
    ClearDataService clearDataService = new ClearDataService(authDao, gameDao, userDao);
    RegisterUser newUser = new RegisterUser("Testing", "is", "fun!");


    @Test
    @Order(1)
    @DisplayName("registerUserTest")
    public void register() throws DataAccessException {
        userService.registerUser(newUser);
        assertTrue(userDao.checkUser(newUser.username()));
    }

    @Test
    @Order(2)
    @DisplayName("registerUserTest2")
    public void registerBadRequest() throws DataAccessException {
        RegisterUser badUser = new RegisterUser("Hello", "World!", "");
        assertThrows(DataAccessException.class, ()-> userService.registerUser(badUser));
    }

    @Test
    @Order(3)
    @DisplayName("registerUserTest3")
    public void registerRepeatedUser() throws DataAccessException {
        RegisterUser repeatUser = new RegisterUser("Hungry", "For", "Cheese");
        userService.registerUser(repeatUser);
        assertThrows(DataAccessException.class, ()-> userService.registerUser(repeatUser));
    }

    @Test
    @Order(4)
    @DisplayName("loginTest")
    public void loginUser() throws DataAccessException {
        RegisterUser user = new RegisterUser("Cool", "Awesome", "Crazy");
        userService.registerUser(user);
        LoginUser loginUser =  new LoginUser("Cool", "Awesome");
        userService.loginUser(loginUser);
        assertTrue(userDao.checkUser(loginUser.username()));
    }

    @Test
    @Order(5)
    @DisplayName("loginTest2")
    public void failedLogin() throws DataAccessException {
        LoginUser loginUser =  new LoginUser("Oh", "No");
        assertThrows(DataAccessException.class, ()-> userService.loginUser(loginUser));
    }

    @Test
    @Order(6)
    @DisplayName("logoutTest")
    public void logoutUser() throws DataAccessException {
        RegisterUser user = new RegisterUser("Crispy", "Chicken", "Nuggets");
        UserData registerResult = userService.registerUser(user);
        userService.logoutUser(registerResult.authToken());
        assertFalse(authDao.checkToken(registerResult.authToken()));
    }

    @Test
    @Order(7)
    @DisplayName("logoutTest2")
    public void logoutFail() throws DataAccessException {
        RegisterUser user = new RegisterUser("Cheeseburger", "Fries", "Milkshake");
        userService.registerUser(user);
        String badToken = "McDonald's";
        assertThrows(DataAccessException.class, ()-> userService.logoutUser(badToken));

    }

    @Test
    @Order(8)
    @DisplayName("createGameTest")
    public void createGame() throws DataAccessException {
        RegisterUser user = new RegisterUser("Yet", "Another", "Test");
        UserData loginResult = userService.registerUser(user);

        GameName gameName = new GameName("FIGHT ME");

        gameService.createGame(loginResult.authToken(), gameName);
        assertNotNull(gameService.listGames(loginResult.authToken()));
    }

    @Test
    @Order(9)
    @DisplayName("createGameTest2")
    public void createGameFail() throws DataAccessException {
        RegisterUser user = new RegisterUser("Mac", "And", "Cheese");
        userService.registerUser(user);
        String badToken = "Bowl";
        GameName gameName = new GameName("SHOWTIME");

        assertThrows(DataAccessException.class, ()-> gameService.createGame(badToken, gameName));

    }

    @Test
    @Order(10)
    @DisplayName("listGamesTest")
    public void listGames() throws DataAccessException {
        clearDataService.clearData();
        RegisterUser user = new RegisterUser("Hate", "Being", "Sick");
        UserData loginResult = userService.registerUser(user);

        GameName gameName = new GameName("FINISH HIM");
        GameName gameName2 = new GameName("PUNCH-OUT");
        gameService.createGame(loginResult.authToken(), gameName);
        gameService.createGame(loginResult.authToken(), gameName2);

        GameList gameList = gameService.listGames(loginResult.authToken());
        assertEquals(2, gameList.games().size());

    }

    @Test
    @Order(11)
    @DisplayName("listGamesTest2")
    public void listGamesFail() throws DataAccessException {
        clearDataService.clearData();
        RegisterUser user = new RegisterUser("Sore", "Throats", "Hurt");
        UserData loginResult = userService.registerUser(user);
        String badToken = "Virus";

        GameName gameName = new GameName("I AM IN PAIN");
        GameName gameName2 = new GameName("HELP");
        gameService.createGame(loginResult.authToken(), gameName);
        gameService.createGame(loginResult.authToken(), gameName2);

        assertThrows(DataAccessException.class, ()-> gameService.listGames(badToken));

    }


    @Test
    @Order(12)
    @DisplayName("JoinGameTest")
    public void joinGame() throws DataAccessException {
        RegisterUser user = new RegisterUser("Getting", "There", "Soon");
        UserData loginResult = userService.registerUser(user);

        GameName gameName = new GameName("Almost there");
        Integer gameID = gameService.createGame(loginResult.authToken(), gameName);

        gameService.joinGame(loginResult.authToken(), "WHITE", gameID);
        gameService.joinGame(loginResult.authToken(), "BLACK", gameID);

        GameData ourGame = gameDao.findGame(gameID);

        assertEquals(user.username(), ourGame.whiteUsername());
        assertEquals(user.username(), ourGame.blackUsername());



    }

    @Test
    @Order(13)
    @DisplayName("JoinGameTest2")
    public void joinGameFail() throws DataAccessException {
        RegisterUser user = new RegisterUser("One", "More", "Left");
        UserData loginResult = userService.registerUser(user);

        GameName gameName = new GameName("So Close");
        Integer gameID = gameService.createGame(loginResult.authToken(), gameName);

        assertThrows(DataAccessException.class, ()-> gameService.joinGame(loginResult.authToken(),
                "", gameID));


    }

    @Test
    @Order(14)
    @DisplayName("clearDBTest")
    public void clearDB() throws DataAccessException {
        RegisterUser user = new RegisterUser("The", "Final", "Test");
        UserData loginResult = userService.registerUser(user);
        GameName gameName = new GameName("AT LAST");
        gameService.createGame(loginResult.authToken(), gameName);

        clearDataService.clearData();

        assertEquals(0, userDao.getUsers().size());
        assertEquals(0, gameDao.listGames().size());
        assertEquals(0, authDao.getAuthData().size());
    }

}
