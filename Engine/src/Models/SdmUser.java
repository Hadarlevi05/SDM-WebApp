package Models;

import Enums.UserType;

public class SdmUser {

    public SdmUser(int id, String username, UserType userType){
        this.id = id;
        this.username = username;
        this.userType = userType;
    }

    public String username;
    public UserType userType;
    public int id;
}

