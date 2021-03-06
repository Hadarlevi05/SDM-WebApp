package Handlers;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class UserHandler {

    private List<UserHandler> allUsers;

    public UserHandler(){
        allUsers = new LinkedList<>();
    }
/*
    public void addUser(String userName) {
        User newUser = new User(userName);
        allUsers.add(newUser);
    }

    public User getUserByName(String userName) {
        for(User curr : allUsers) {
            if(curr.getUserName().equals(userName)) {
                return curr;
            }
        }
        return null;
    }

    public List<String> getAllOnlineUsers(){
        Stream<User> users = allUsers.stream();
        return users.filter(User::getIsOnline).map(User::getUserName).collect(Collectors.toList());
    }

    public List<String> getAllUsersName(){
        Stream<User> users = allUsers.stream();
        return users.map(User::getUserName).collect(Collectors.toList());
    }

    public boolean isUserOnline(String userName) {
        User currUser = getUserByName(userName);
        if(currUser != null) {
            return currUser.getIsOnline();
        }
        return false;
    }

    public boolean isUserExists(String userName) {
        User currUser = getUserByName(userName);
        return currUser != null;
    }

    public List<Repository> showUserRepos(String userName) {
        User currUser = getUserByName(userName);
        if(currUser != null) {
            return currUser.getActiveRepositories();
        }
        return null;
    }

    public void disconnectUser(String userName) {
        User currUser = getUserByName(userName);
        if(currUser != null) {
            currUser.setOnline(false);
            currUser.logout();
        }
    }*/

}
