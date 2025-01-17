package service;

import dataaccess.*;

public class ClearDataService {

    AuthDAO authDao;
    GameDAO gameDao;
    UserDAO userDao;

    public ClearDataService(AuthDAO auth, GameDAO game, UserDAO user){
        this.authDao = auth;
        this.gameDao = game;
        this.userDao = user;
    }

    public void clearData() throws DataAccessException{
        userDao.clearUsers();
        gameDao.clearGames();
        authDao.clearAuthData();
    }

}
