package DTO;

import Models.SdmUser;
import com.google.gson.annotations.Expose;

import java.util.List;

public class UsersDTO extends ResponseDTO {

        @Expose
        public List<SdmUser> Users;
}
