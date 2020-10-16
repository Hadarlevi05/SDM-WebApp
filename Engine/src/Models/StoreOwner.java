package Models;

import Handlers.LocationHandler;
import Handlers.OrderManager;

public class StoreOwner {


    public StoreOwner(String username, String area, SuperDuperMarket superDuperMarket){
        this.username = username;
        this.area = area;
        this.superDuperMarket = superDuperMarket;
    }

    public String username;
    public String area;
    public SuperDuperMarket superDuperMarket;

}
