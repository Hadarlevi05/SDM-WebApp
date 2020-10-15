package Handlers;

import Models.*;

public class ItemHandler {

    private OrderManager orderManager;

    public ItemHandler(){
        orderManager = new OrderManager();
    }

    public double CalculateSoldItemsAmountPerStore(SuperDuperMarket superDuperMarket,int itemId, int storeId) {
        double count = 0;
        for (Order order:superDuperMarket.Orders.ordersMap.values()) {
            for (OrderItem orderItem:order.orderItems) {
               if (orderItem.itemId == itemId && orderItem.storeId == storeId){
                   count += (orderItem.quantityObject.integerQuantity+ orderItem.quantityObject.KGQuantity);
               }
            }
        }
        return  count;
    }

    public double CalculateSoldItemsAmount(SuperDuperMarket superDuperMarket,int itemId) {
        double count = 0;
        for (Order order:superDuperMarket.Orders.ordersMap.values()) {
            for (OrderItem orderItem:order.orderItems) {
                if (orderItem.itemId == itemId){
                    count += (orderItem.quantityObject.integerQuantity+ orderItem.quantityObject.KGQuantity);
                }
            }
            for (OrderItem orderItem:order.orderItemsFromSales) {
                if (orderItem.itemId == itemId){
                    count += (orderItem.quantityObject.integerQuantity+ orderItem.quantityObject.KGQuantity);
                }
            }
        }
        return  count;
    }
}
