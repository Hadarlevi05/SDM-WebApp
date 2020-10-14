package Models;

import Enums.UserType;

public class SdmUser {

    public SdmUser(String username, UserType userType){
        this.username = username;
        this.userType = userType;
    }

    public String username;
    public UserType userType;
}

