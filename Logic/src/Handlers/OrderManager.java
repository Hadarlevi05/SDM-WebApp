package Handlers;

import Models.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@XmlRootElement
public class OrderManager {

    public String ordersHistoryPath;
    private int _orderID = 1;

    @XmlElement
    public Map<Integer, Order> ordersMap = new HashMap<Integer, Order>();

    private StoreHandler storeHandler;

    public OrderManager() {

        storeHandler = new StoreHandler();

    }

    public OrderItem FindCheapestStoreForItem(SuperDuperMarket superDuperMarket, int itemSerialNumber) {

        OrderItem cheapestOrderItem = null;

        for (int i = 0; i < superDuperMarket.Stores.toArray().length; i++) {

            Store store = superDuperMarket.Stores.get(i);

            OrderItem orderItem = new StoreHandler().GetOrderItemByItemId(store, itemSerialNumber, true);

            if (cheapestOrderItem == null && orderItem != null) {
                cheapestOrderItem = storeHandler.CloneOrderItem(orderItem);

            }
            if (orderItem != null && cheapestOrderItem != null) {
                if (cheapestOrderItem.price > orderItem.price) {
                    cheapestOrderItem = storeHandler.CloneOrderItem(orderItem);
                }
            }
        }
        return cheapestOrderItem;
    }

    public Order GetOrderByOrderID(SuperDuperMarket sdm, int orderId) {
        return sdm.Orders.ordersMap.get(orderId);
    }

    public void addOrder(SuperDuperMarket superDuperMarket, Order order) {

        order.id = _orderID;
        _orderID++;

        for (OrderItem orderItem : order.orderItems) {

            Store store = storeHandler.getStoreById(superDuperMarket, orderItem.storeId);

            if (store.OrderHistoryIDs.contains(order.id) == false) {
                store.OrderHistoryIDs.add(order.id);
            }
        }

        ordersMap.put(order.id, order);
    }

    public List<Discount> checkForSales(SuperDuperMarket superDuperMarket, Order order) {

        List<Discount> sales = new ArrayList<>();
        for (int storeID : order.storesID) {
            Store store = storeHandler.getStoreById(superDuperMarket, storeID);
            if (store.Sales != null && store.Sales.size() > 0) {
                for (Discount discount : store.Sales) {
                    List<OrderItem> itemsInSale = order.orderItems.stream().filter(item -> item.itemId == discount.ItemID).collect(Collectors.toList());
                    if (itemsInSale != null && itemsInSale.size() > 0) {
                        OrderItem itemOnSale = itemsInSale.get(0);
                        double quantity = itemOnSale.quantityObject.integerQuantity > 0 ? itemOnSale.quantityObject.integerQuantity : itemOnSale.quantityObject.KGQuantity;
                        if (quantity >= discount.Quantity) {
                            int numOfOffers = (int) (quantity / discount.Quantity);
                            for (int i = 1; i <= numOfOffers; i++) {
                                Discount disc = CloneDiscount(discount);
                                disc.Id = i;
                                sales.add(disc);
                            }
                        }

                    }
                }
            }
        }
        for (int i = 0 ;i < sales.size(); i++) {
            sales.get(i).Id = i+1;
        }
        return sales;
    }

    public Discount CloneDiscount(Discount discount) {

        return new Discount(discount.Id, discount.Name, discount.ItemID, discount.Quantity, discount.OperatorType, discount.Offers, discount.StoreID);
    }


}
