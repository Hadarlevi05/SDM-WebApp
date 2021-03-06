package DataStore;

import java.util.ArrayList;

public class DataStore {

    private static DataStore _dataStore = null;

    public UserDataStore userDataStore;
    public AccountsManagementDataStore transactionsStore;
    public UserConfigurationDataStore userConfigurationDataStore;

    // private constructor restricted to this class itself
    private DataStore() {
        userDataStore = new UserDataStore();
        userConfigurationDataStore = new UserConfigurationDataStore();
        transactionsStore = new AccountsManagementDataStore();
    }

    // static method to create instance of Singleton class
    public static DataStore getInstance() {
        if (_dataStore == null) {
            _dataStore = new DataStore();
        }
        return _dataStore;
    }

}
