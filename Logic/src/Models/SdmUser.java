package Models;

import java.util.ArrayList;
import java.util.List;

import src.Enums.UserType;

public class SdmUser {

    public SdmUser(int username, String userType){
        this.username = username;
        this.userType = userType;
    }

    public String username;
    public UserType userType;

}
