package service;

import model.LoginUser;
import org.mindrot.jbcrypt.BCrypt;
import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.RegisterUser;
import model.UserData;

public class UserService {

    AuthDAO authDAO;
    UserDAO userDAO;

    public UserService(AuthDAO authDao, UserDAO userDao) {
        this.authDAO = authDao;
        this.userDAO = userDao;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean verifyUser(String username, String password) throws DataAccessException {
        // read the previously hashed password from the database
        String comparisonPassword = userDAO.getPassword(username);

        return BCrypt.checkpw(password, comparisonPassword);
    }



    public UserData registerUser(RegisterUser newUser) throws DataAccessException{

        String username = newUser.username();
        String password = newUser.password();
        String email = newUser.email();

        if (username == null || password == null || email == null
                || username.isEmpty() || password.isEmpty() || email.isEmpty()){
            throw new DataAccessException("Error: bad request");
        }

        if (userDAO.checkUser(username)){
            throw new DataAccessException("Error: already taken");
        }

        String hashedPassword = hashPassword(password);

        userDAO.createUser(username, hashedPassword, email);

        String authToken = authDAO.generateToken(username);

        return new UserData(authToken, username);
    }

    public UserData loginUser(LoginUser loginRequest) throws DataAccessException{

        String username = loginRequest.username();
        String password = loginRequest.password();

        if (!userDAO.checkUser(username)){
            throw new DataAccessException("Error: unauthorized");
        }

        if (!verifyUser(username, password)){
            throw new DataAccessException("Error: unauthorized");
        }

        String authToken = authDAO.generateToken(username);
        return new UserData(authToken, username);

    }

    public void logoutUser(String authToken) throws DataAccessException{

        if (!authDAO.checkToken(authToken)){
            throw new DataAccessException("Error: unauthorized");
        }
        else {
            authDAO.deleteToken(authToken);
        }
    }

}
