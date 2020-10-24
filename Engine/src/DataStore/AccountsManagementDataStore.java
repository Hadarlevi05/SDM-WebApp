package DataStore;

import Models.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountsManagementDataStore {
    public Map<String, List<Transaction>> accMap = new HashMap<String,List<Transaction>>();

    public void add(String username, Transaction acc) {
        List<Transaction> lst = accMap.getOrDefault(username, null);
        if(lst == null){
            lst = new ArrayList<>();
            accMap.put(username, lst);
        }

        lst.add(acc);
    }

    public List<Transaction> get(String username) {
        return accMap.getOrDefault(username, new ArrayList<>());
    }
}