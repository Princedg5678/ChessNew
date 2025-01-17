package dataaccess;

import java.util.Map;

public interface AuthDAO {

    public String generateToken(String username) throws DataAccessException;
    public void deleteToken(String authToken) throws DataAccessException;
    public boolean checkToken(String authToken) throws DataAccessException;
    public String getUsername(String token) throws DataAccessException;
    public Map<String, String> getAuthData() throws DataAccessException;
    public void clearAuthData() throws DataAccessException;
}
