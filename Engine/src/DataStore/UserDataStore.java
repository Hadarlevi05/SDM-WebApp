package DataStore;

import Models.SdmUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDataStore {

    private List<SdmUser> users;

    public UserDataStore() {
        users = new ArrayList();
    }

    public Models.SdmUser get(String username) {
        List<Models.SdmUser> users = this.users.stream().filter(user -> user.username == username).collect(Collectors.toList());
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    public void create(Models.SdmUser user) {
        this.users.add(user);
    }

    public void update(Models.SdmUser user) {
        Models.SdmUser sdmUser = get(user.username);
        user.userType = user.userType;
    }

    public void delete(Models.SdmUser user) {

        Models.SdmUser user1 = get(user.username);

        this.users.remove(user);
    }

}
