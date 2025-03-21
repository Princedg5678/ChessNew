package client;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.ClearDataService;
import ui.ResponseException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;


public class ServerFacadeTests {

    SQLAuthDAO authDao = new SQLAuthDAO();
    SQLGameDAO gameDao = new SQLGameDAO();
    SQLUserDAO userDao = new SQLUserDAO();
    ClearDataService clear = new ClearDataService(authDao, gameDao, userDao);

    private static ServerFacade serverFacade;


    private static Server server;

    public ServerFacadeTests() throws DataAccessException {
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    private void clearData() throws DataAccessException {
        clear.clearData();
    }

    @Test
    @Order(1)
    @DisplayName("Register")
    public void register() throws ResponseException, DataAccessException {
        clearData();
        RegisterUser registerUser = new RegisterUser("1","2","3");
        UserData newUser = serverFacade.registerUser(registerUser);
        assertEquals("1", newUser.username());
    }

    @Test
    @Order(2)
    @DisplayName("RegisterFail")
    public void registerFail() throws DataAccessException {
        clearData();
        RegisterUser registerUser = new RegisterUser("","2","3");
        assertThrows(ResponseException.class, () -> serverFacade.registerUser(registerUser));

    }

    @Test
    @Order(3)
    @DisplayName("Logout")
    public void logout() throws ResponseException, DataAccessException {
        clearData();
        RegisterUser registerUser = new RegisterUser("1","2","3");
        UserData newUser = serverFacade.registerUser(registerUser);

        serverFacade.logoutUser(newUser.authToken());
        assertThrows(ResponseException.class, () ->
                serverFacade.createGame(new GameName("testName"), newUser.authToken()));
    }

    @Test
    @Order(4)
    @DisplayName("LogoutFail")
    public void logoutFail() throws ResponseException, DataAccessException {
        clearData();
        RegisterUser registerUser = new RegisterUser("1","2","3");
        UserData newUser = serverFacade.registerUser(registerUser);
        serverFacade.logoutUser(newUser.authToken());

        assertThrows(ResponseException.class, () -> serverFacade.logoutUser(newUser.authToken()));
    }

    @Test
    @Order(5)
    @DisplayName("Login")
    public void login() throws ResponseException, DataAccessException {
        clearData();
        RegisterUser registerUser = new RegisterUser("1","2","3");
        UserData newUser = serverFacade.registerUser(registerUser);
        serverFacade.logoutUser(newUser.authToken());
        UserData returningUser = serverFacade.loginUser(new LoginUser("1","2"));

        assertEquals("1", returningUser.username());
    }

    @Test
    @Order(6)
    @DisplayName("LoginFail")
    public void loginFail() {
        assertThrows(ResponseException.class, () -> serverFacade.loginUser(new LoginUser("4","5")));
    }

    @Test
    @Order(7)
    @DisplayName("create")
    public void create() throws ResponseException, DataAccessException {
        clearData();
        RegisterUser registerUser = new RegisterUser("1","2","3");
        UserData newUser = serverFacade.registerUser(registerUser);
        GameID gameID = serverFacade.createGame(new GameName("testName"), newUser.authToken());
        assertNotNull(gameID);
    }

    @Test
    @Order(8)
    @DisplayName("createFail")
    public void createFail() throws ResponseException, DataAccessException {
        clearData();
        RegisterUser registerUser = new RegisterUser("1","2","3");
        UserData newUser = serverFacade.registerUser(registerUser);

        assertThrows(ResponseException.class, () -> serverFacade.createGame(null ,newUser.authToken()));
    }

    @Test
    @Order(9)
    @DisplayName("list")
    public void list() throws ResponseException, DataAccessException {
        clearData();
        RegisterUser registerUser = new RegisterUser("1","2","3");
        UserData newUser = serverFacade.registerUser(registerUser);
        serverFacade.createGame(new GameName("testName1"), newUser.authToken());
        serverFacade.createGame(new GameName("testName2"), newUser.authToken());

        GameList gameList = serverFacade.listGames(newUser.authToken());
        assertEquals(2, gameList.games().size());

    }

    @Test
    @Order(10)
    @DisplayName("listFail")
    public void listFail() throws ResponseException, DataAccessException {
        clearData();
        RegisterUser registerUser = new RegisterUser("1","2","3");
        UserData newUser = serverFacade.registerUser(registerUser);
        serverFacade.createGame(new GameName("testName1"), newUser.authToken());

        assertThrows(ResponseException.class, () -> serverFacade.listGames(null));
    }

    @Test
    @Order(11)
    @DisplayName("play")
    public void play() throws ResponseException, DataAccessException {
        clearData();
        RegisterUser registerUser = new RegisterUser("1","2","3");
        UserData newUser = serverFacade.registerUser(registerUser);
        serverFacade.createGame(new GameName("testName1"), newUser.authToken());
        GameList gameList = serverFacade.listGames(newUser.authToken());
        ArrayList<GameResult> games = gameList.games();
        Integer gameID = games.get(0).gameID();
        JoinRequest joinRequest = new JoinRequest("WHITE", gameID);

        serverFacade.playGame(joinRequest, newUser.authToken());
        gameList = serverFacade.listGames(newUser.authToken());
        games = gameList.games();
        assertEquals("1", games.get(0).whiteUsername());
    }

    @Test
    @Order(12)
    @DisplayName("playFail")
    public void playFail() throws ResponseException, DataAccessException {
        clearData();
        RegisterUser registerUser = new RegisterUser("1","2","3");
        UserData newUser = serverFacade.registerUser(registerUser);
        serverFacade.createGame(new GameName("testName1"), newUser.authToken());
        GameList gameList = serverFacade.listGames(newUser.authToken());
        ArrayList<GameResult> games = gameList.games();
        Integer gameID = games.get(0).gameID();
        JoinRequest joinRequest = new JoinRequest("WHITE", gameID);

        serverFacade.playGame(joinRequest, newUser.authToken());
        assertThrows(ResponseException.class, () -> serverFacade.playGame(joinRequest, newUser.authToken()));
    }


}
