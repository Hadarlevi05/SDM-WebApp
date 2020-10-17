package DataStore;

import Models.SdmUser;
import Models.StoreOwner;
import Models.SuperDuperMarket;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserConfigurationDataStore {

    List<StoreOwner> storeOwners;

    public UserConfigurationDataStore() {
        storeOwners = new ArrayList<>();
    }

    public StoreOwner get(StoreOwner storeowner) {
        List<StoreOwner> storeOwners = this.storeOwners.stream().filter(storeOwner -> storeOwner.equals(storeowner)).collect(Collectors.toList());
        if (storeOwners.isEmpty()) {
            return null;
        }
        return storeOwners.get(0);
    }

    public List<StoreOwner> list() {
        return storeOwners;
    }

    public List<StoreOwner> list(String username) {
        List<StoreOwner> storeOwners = this.storeOwners.stream().filter(storeOwner -> storeOwner.username.equals(username)).collect(Collectors.toList());
        if (storeOwners.isEmpty()) {
            return null;
        }
        return storeOwners;
    }

    public void create(StoreOwner storeowner) {
        this.storeOwners.add(storeowner);
    }

    public StoreOwner getByArea(String area) {

        List<StoreOwner> storeOwners = this.storeOwners.stream().filter(storeOwner -> storeOwner.area.equals(area)).collect(Collectors.toList());
        if (storeOwners.isEmpty()) {
            return null;
        }
        return storeOwners.get(0);
    }

    public void delete(StoreOwner storeowner) {

        StoreOwner user1 = get(storeowner);

        this.storeOwners.remove(user1);
    }
}
