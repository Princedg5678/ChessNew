package dataaccess;

public interface UserDAO {

    public boolean checkUser(String username) throws DataAccessException;
    public void createUser(String username, String password, String email) throws DataAccessException;
    public void clearUsers() throws DataAccessException;
    public String getPassword(String username) throws DataAccessException;
}
