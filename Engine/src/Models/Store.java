package Models;

import Handlers.LocationHandler;
import Handlers.OrderManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Store {

    private OrderManager orderManager;
    private LocationHandler  locationHandler;


    public Store(int serialNumber, String name, int PPK, SDMLocation location){
        this.serialNumber = serialNumber;
        this.name = name;
        this.PPK = PPK;
        this.Location = location;
        orderManager= new OrderManager();
        locationHandler = new LocationHandler();
    }
    public int serialNumber;
    public String name = null;
    public int PPK;
    public SDMLocation Location;

    public List<OrderItem> Inventory;
    public List<Integer> OrderHistoryIDs = new ArrayList<>();
    public List<Discount> Sales = new ArrayList<>();

    public double CalculateTotalDeliveriesCost(SuperDuperMarket superDuperMarket) {
        double totalDeliveriesCost = 0;
        for (int orderId: OrderHistoryIDs) {
            Order order =superDuperMarket.Orders.GetOrderByOrderID(superDuperMarket, orderId);
            totalDeliveriesCost+= locationHandler.calculateDeliveryCost(Location, order.CustomerLocation, PPK);
        }
        return  totalDeliveriesCost;
    }

    public OrderItem GetOrderItemByID(int orderItemID){

        List<OrderItem> items= this.Inventory.stream().filter(item->item.itemId == orderItemID).collect(Collectors.toList());

        if (items.isEmpty()) {
            return null;
        }

        return items.get(0);
    }

    @Override
    public String toString() {
        return "Serial number:" + serialNumber + " name:" + name +  " location:" + Location.x + ", " +  Location.y;
    }

}

