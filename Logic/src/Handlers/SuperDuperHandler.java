package Handlers;

import Models.*;
import java.util.List;
import java.util.stream.Collectors;

public class SuperDuperHandler {


    public Item getItemById(SuperDuperMarket sdm, int serialNumber) {
        List<Item> items= sdm.Items.stream().filter(item->item.serialNumber == serialNumber).collect(Collectors.toList());
        if (!items.isEmpty()){
            return items.get(0);
        }
        return null;
    }

    public OrderItem getOrderItemById(SuperDuperMarket sdm, int serialNumber) {
        for (Store store:sdm.Stores) {
            for (OrderItem oi:store.Inventory) {
                if (oi.itemId == serialNumber){
                    return oi;
                }
            }
        }
        return null;
    }

    public OrderItem getOrderItemById(Store store, int serialNumber) {
            for (OrderItem oi:store.Inventory) {
                if (oi.itemId == serialNumber){
                    return oi;
                }
            }
        return null;
    }

    public OrderItem getOrderItemByStoreIdAndItemID(SuperDuperMarket sdm, int storeID, int itemID) {
        List<Store> stores= sdm.Stores.stream().filter(store->store.serialNumber == storeID).collect(Collectors.toList());
        List<OrderItem> orderItems = stores.get(0).Inventory.stream().filter(oi->oi.itemId == itemID).collect(Collectors.toList());
        if (!orderItems.isEmpty()){
            return orderItems.get(0);
        }
        return null;
    }

    public Store GetStoreByLocation(SuperDuperMarket sdm, SDMLocation location){
        List<Store> stores= sdm.Stores;
        for (Store store: stores) {
            if (store.Location.x == location.x && store.Location.y ==location.y){
                return store;
            }
        }
        return  null;
    }

    public Store FindTheCheapestbasket(SuperDuperMarket sdm, List<OrderItem> orderItems){

        for (OrderItem orderItem: orderItems) {

        }
        return  null;
    }




}
