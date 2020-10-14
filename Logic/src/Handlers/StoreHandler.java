package Handlers;

import Models.*;

import java.util.List;
import java.util.stream.Collectors;

public class StoreHandler {

    public  int _dicountID = 1;

    public StoreHandler() {

    }

    public double countAveragePriceOfSellingStores(SuperDuperMarket SuperDuperMarket, int itemSerialNumber){

        List<Store> stores = SuperDuperMarket.Stores;
        double sum =0;
        double count=0;
        for (int i=0; i<stores.toArray().length; i++){
            Store store =stores.get(i);
            for(int j = 0; j<store.Inventory.toArray().length; j++){
                OrderItem orderItem = store.Inventory.get(j);
                if (orderItem.itemId == itemSerialNumber) {
                    sum+= orderItem.price;
                    count++;
                }
            }
        }
        return  sum/count;
    }

    public OrderItem GetOrderItemByItemId(Store store, int itemSerialNumber, boolean clone) {
        for (OrderItem orderItem : store.Inventory) {
            if (orderItem.itemId == itemSerialNumber) {
                return clone ? CloneOrderItem(orderItem) : orderItem;
            }
        }

        return null;
    }

    public OrderItem CloneOrderItem(OrderItem orderItem) {

        OrderItem res = new OrderItem();
        res.storeId = orderItem.storeId;
        res.itemId = orderItem.itemId;
        res.price = orderItem.price;
        if (orderItem.quantityObject!=null){
            res.quantityObject.integerQuantity = orderItem.quantityObject.integerQuantity;
            res.quantityObject.KGQuantity = orderItem.quantityObject.KGQuantity;
        }
        else {
            res.quantityObject = new QuantityObject();
        }
        return res;
    }

    public Store getStoreById(SuperDuperMarket sdm, int serialNumber) {
        List<Store> stores= sdm.Stores.stream().filter(store->store.serialNumber == serialNumber).collect(Collectors.toList());
        if (!stores.isEmpty()){
            return stores.get(0);
        }
        return null;
    }

    public Store getStoreItems(SuperDuperMarket sdm, int serialNumber) {
        List<Store> stores= sdm.Stores.stream().filter(store->store.serialNumber == serialNumber).collect(Collectors.toList());
        if (!stores.isEmpty()){
            return stores.get(0);
        }
        return null;
    }

    public int countSellingStores(SuperDuperMarket superDuperMarket, int itemSerialNumber) {
        int count = 0;
        for (Store order:superDuperMarket.Stores) {
            {
                for (OrderItem oi:order.Inventory) {
                    if (oi.itemId == itemSerialNumber){
                        count++;
                    }
                }
            }
        }
        return count;
}

    public double countTotalItemsAmountOfStoreInOrder(Order order, Store store) {

        double totalAmountOfItems = 0;
        for (OrderItem oi: order.orderItems) {
            if (oi.storeId == store.serialNumber){
                totalAmountOfItems+= oi.quantityObject.integerQuantity >0? oi.quantityObject.integerQuantity : oi.quantityObject.KGQuantity;
            }
        }
        for (OrderItem oi: order.orderItemsFromSales) {
            if (oi.storeId == store.serialNumber){
                totalAmountOfItems+= oi.quantityObject.integerQuantity >0? oi.quantityObject.integerQuantity : oi.quantityObject.KGQuantity;
            }
        }
        return totalAmountOfItems;
    }



    public double countTotalCostItemsOfStoreInOrder(Order order, Store store) {

        double totalCost = 0;
        for (OrderItem oi: order.orderItems) {
            if (oi.storeId == store.serialNumber){
                double quantity = oi.quantityObject.integerQuantity > 0 ? oi.quantityObject.integerQuantity : oi.quantityObject.KGQuantity;
                totalCost += oi.price * quantity;
            }
        }
        for (OrderItem oi: order.orderItemsFromSales) {
            if (oi.storeId == store.serialNumber){
                double quantity = oi.quantityObject.integerQuantity > 0 ? oi.quantityObject.integerQuantity : oi.quantityObject.KGQuantity;
                totalCost += oi.price;
            }
        }
        return totalCost;
    }

    public double countDeliveryPriceOfStoreInOrder(Order order, Store store) {
        return order.deliveryPriceByStore.get(store.serialNumber);
    }

    public double countTotalPriceOfStoreInOrder(Order order, Store store) {
        return countDeliveryPriceOfStoreInOrder(order, store)+countTotalCostItemsOfStoreInOrder(order, store);
    }

    public boolean deleteItemFromStore(Item itemToDelete, Store store,SuperDuperMarket superDuperMarket) {
           if (countSellingStores(superDuperMarket, itemToDelete.serialNumber) > 1 && store.Inventory.stream().count()>1 ){
               OrderItem oi = GetOrderItemByItemId(store, itemToDelete.serialNumber, false);
               return store.Inventory.remove(oi);
           }

           return false;
    }

    public boolean addItemToStore(OrderItem itemToAdd, Store store, SuperDuperMarket superDuperMarket) {
        return store.Inventory.add(itemToAdd);
    }

    public void updateItemPrice(OrderItem itemToUpdate, Store store, SuperDuperMarket superDuperMarket, double itemPrice) {

        itemToUpdate.price=itemPrice;
    }

    public void addStore(SuperDuperMarket superDuperMarket, Store storeToAdd) {
        superDuperMarket.Stores.add(storeToAdd);
    }

}
