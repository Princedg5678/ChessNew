package dataaccess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    public void clear() throws DataAccessException, SQLException {
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

}
