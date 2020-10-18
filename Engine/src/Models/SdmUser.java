package Models;

import Enums.UserType;
import com.google.gson.annotations.Expose;

public class SdmUser {

    public SdmUser(int id, String username, UserType userType){
        this.id = id;
        this.username = username;
        this.userType = userType;
    }
    @Expose
    public String username;
    @Expose
    public UserType userType;
    @Expose
    public int id;
}

