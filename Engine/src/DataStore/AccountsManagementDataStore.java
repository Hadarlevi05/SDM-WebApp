package DataStore;

import Models.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountsManagementDataStore {
    public Map<String, List<Transaction>> accMap = new HashMap<String,List<Transaction>>();

    public void add(Models.SdmUser user, Transaction acc) {
        List<Transaction> lst = accMap.getOrDefault(user.username, null);
        if(lst == null){
            lst = new ArrayList<>();
            accMap.put(user.username, lst);
        }

        lst.add(acc);
    }

    public List<Transaction> get(Models.SdmUser user) {
        return accMap.getOrDefault(user.username, new ArrayList<>());
    }
}