package Models;

import Handlers.LocationHandler;
import Handlers.OrderManager;

import java.util.ArrayList;
import java.util.List;

public class StoreOwner {


    public StoreOwner(String username, String area, SuperDuperMarket superDuperMarket){
        this.username = username;
        this.area = area;
        this.superDuperMarket = superDuperMarket;
        feedbacks = new ArrayList<>();
    }

    public String username;
    public String area;
    public SuperDuperMarket superDuperMarket;
    public List<Feedback> feedbacks;

}
