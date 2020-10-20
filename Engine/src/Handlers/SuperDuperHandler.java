package Handlers;

import DTO.KeyValueDTO;
import DataStore.DataStore;
import Models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SuperDuperHandler {

    private StoreHandler storeHandler;
    private ItemHandler itemHandler;

    public SuperDuperHandler() {
        storeHandler = new StoreHandler();
        itemHandler = new ItemHandler();
    }

    private DataStore dataStore = DataStore.getInstance();

    public Item getItemById(SuperDuperMarket sdm, int serialNumber) {
        List<Item> items = sdm.Items.stream().filter(item -> item.serialNumber == serialNumber).collect(Collectors.toList());
        if (!items.isEmpty()) {
            return items.get(0);
        }
        return null;
    }

    public OrderItem getOrderItemById(SuperDuperMarket sdm, int serialNumber) {
        for (Store store : sdm.Stores) {
            for (OrderItem oi : store.Inventory) {
                if (oi.itemId == serialNumber) {
                    return oi;
                }
            }
        }
        return null;
    }

    public OrderItem getOrderItemById(Store store, int serialNumber) {
        for (OrderItem oi : store.Inventory) {
            if (oi.itemId == serialNumber) {
                return oi;
            }
        }
        return null;
    }

    public OrderItem getOrderItemByStoreIdAndItemID(SuperDuperMarket sdm, int storeID, int itemID) {
        List<Store> stores = sdm.Stores.stream().filter(store -> store.serialNumber == storeID).collect(Collectors.toList());
        List<OrderItem> orderItems = stores.get(0).Inventory.stream().filter(oi -> oi.itemId == itemID).collect(Collectors.toList());
        if (!orderItems.isEmpty()) {
            return orderItems.get(0);
        }
        return null;
    }

    public Store GetStoreByLocation(SuperDuperMarket sdm, SDMLocation location) {
        List<Store> stores = sdm.Stores;
        for (Store store : stores) {
            if (store.Location.x == location.x && store.Location.y == location.y) {
                return store;
            }
        }
        return null;
    }

    public Store FindTheCheapestbasket(SuperDuperMarket sdm, List<OrderItem> orderItems) {

        for (OrderItem orderItem : orderItems) {

        }
        return null;
    }

    public List<Map<String, Object>> getStoreAreaDetails(String username) {
        List<Map<String, Object>> rows = new ArrayList();
        KeyValueDTO keyValueDTO = new KeyValueDTO();
        for (StoreOwner storeOwner :
                dataStore.userConfigurationDataStore.list()) {
            Map<String, Object> map = new HashMap<>();
            map.put("storeowner", storeOwner.username);
            map.put("area", storeOwner.area);
            map.put("itemstypes", CalculateNumOfItemsTypes(storeOwner.superDuperMarket));

            map.put("storesnumber", storeOwner.superDuperMarket.Stores.size());
            map.put("ordersnumber", storeOwner.superDuperMarket.Orders.ordersMap.values().size());
            map.put("avgordersprice", CalculateAvgOfOrders(storeOwner.superDuperMarket));
            rows.add(map);
        }
        return rows;
    }

    public List<Map<String, Object>> getStoresDetails(String area) {

        DataStore dataStore = DataStore.getInstance();
        StoreOwner storeOwner = dataStore.userConfigurationDataStore.getByArea(area);
        SuperDuperMarket sdm = storeOwner.superDuperMarket;
        List<Map<String, Object>> rows = new ArrayList();
        KeyValueDTO keyValueDTO = new KeyValueDTO();
        for (Store store :
                sdm.Stores) {
            Map<String, Object> map = new HashMap<>();
            map.put("serialnumber", store.serialNumber);
            map.put("name", store.name);
            map.put("owner", storeOwner.username);

            map.put("location", "[" + store.Location.x + " , " + store.Location.y + "]");


            List<Map<String, Object>> items = new ArrayList();
            for (OrderItem oi : store.Inventory) {
                Item item = getItemById(sdm, oi.itemId);

                Map<String, Object> rowItem = new HashMap<>();
                rowItem.put("serialnumber", oi.itemId);
                rowItem.put("name", item.name);
                rowItem.put("purchaseType", item.purchaseType.toString());
                rowItem.put("price", oi.price);
                rowItem.put("numOfSoldItems", itemHandler.CalculateSoldItemsAmount(sdm, oi.itemId));
                items.add(rowItem);
            }

            map.put("items", items);
            map.put("PPK", store.PPK);
            double totalDeliveriesCost = storeHandler.getStoreById(sdm, store.serialNumber).CalculateTotalDeliveriesCost(sdm);
            map.put("TotalCostOfDeliveriesFromStore", String.format("%.2f", totalDeliveriesCost));
            rows.add(map);
        }
        return rows;
    }

    public List<Map<String, Object>> getItemsDetails(String area) {
        DataStore dataStore = DataStore.getInstance();
        StoreOwner storeOwner = dataStore.userConfigurationDataStore.getByArea(area);
        SuperDuperMarket sdm = storeOwner.superDuperMarket;
        List<Map<String, Object>> rows = new ArrayList();
        KeyValueDTO keyValueDTO = new KeyValueDTO();
        for (Item item :
                sdm.Items) {
            Map<String, Object> map = new HashMap<>();
            map.put("serialnumber", item.serialNumber);
            map.put("name", item.name);
            map.put("purchaseType", item.purchaseType.toString());
            map.put("numOfStoresSellingItems", storeHandler.countSellingStores(sdm, item.serialNumber));
            map.put("averagePrice", storeHandler.countAveragePriceOfSellingStores(sdm, item.serialNumber));
            map.put("soldItemsAmount", itemHandler.CalculateSoldItemsAmount(sdm, item.serialNumber));

            rows.add(map);
        }
        return rows;
    }

    public List<Map<String, Object>> getItemsByStore(SuperDuperMarket sdm, int storeID) {
        List<Map<String, Object>> rows = new ArrayList();

        for (Store store :
                sdm.Stores) {
            for (OrderItem oi :
                    store.Inventory) {
                if (oi.storeId == storeID) {
                    Item item = getItemById(sdm, oi.itemId);
                    Map<String, Object> map = new HashMap<>();
                    map.put("serialnumber", oi.itemId);
                    map.put("name", item.name);
                    map.put("price", oi.price);
                    rows.add(map);
                }
            }
        }

        return rows;
    }


    public List<Map<String, Object>> getOrdersHistoryDetails(String area, int userID) {

        DataStore dataStore = DataStore.getInstance();
        StoreOwner storeOwner = dataStore.userConfigurationDataStore.getByArea(area);
        List<Map<String, Object>> rows = new ArrayList();
        KeyValueDTO keyValueDTO = new KeyValueDTO();
        for (Order order :
                storeOwner.superDuperMarket.Orders.ordersMap.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("serialnumber", order.id);
            map.put("date", order.purchaseDate);
            map.put("location", "[" + order.CustomerLocation.x + " , " + order.CustomerLocation.y + "]");
            map.put("numOfStores", order.storesID.size());
            map.put("numOfItems", order.totalItemsNum);
            map.put("totalItemsPrice", order.totalItemsPrice);
            map.put("deliveryPrice", order.deliveryPrice);
            map.put("totalOrderPrice", order.totalPrice);
            List<Map<String, Object>> orderItems = new ArrayList();

            for (int storeID : order.storesID) {
                Store store = storeHandler.getStoreById(storeOwner.superDuperMarket, storeID);

                for (OrderItem oi : order.orderItems) {
                    if (oi.storeId == store.serialNumber) {
                        Map<String, Object> orderOtemDetails = getOrderDetailsOfOrderItem(storeOwner.superDuperMarket, storeID, oi, false);
                        orderItems.add(orderOtemDetails);
                    }
                }
                for (OrderItem oi : order.orderItemsFromSales) {
                    if (oi.storeId == store.serialNumber) {
                        Map<String, Object> orderOtemDetails = getOrderDetailsOfOrderItem(storeOwner.superDuperMarket, storeID, oi, true);
                        orderItems.add(orderOtemDetails);
                    }
                }
            }
            map.put("orderItems", orderItems);
        }
        return rows;
    }

    private Map<String, Object> getOrderDetailsOfOrderItem(SuperDuperMarket superDuperMarket, int storeID, OrderItem oi, Boolean boughtOnSale) {
        Map<String, Object> orderItemsOfOrder = new HashMap<>();

        orderItemsOfOrder.put("boughtOnSale", boughtOnSale);
        if (oi.storeId == storeID) {
            Item item = getItemById(superDuperMarket, oi.itemId);
            orderItemsOfOrder.put("serialNumber", item.serialNumber);
            orderItemsOfOrder.put("name", item.name);
            orderItemsOfOrder.put("purchaseType", item.purchaseType);
            orderItemsOfOrder.put("totalPrice", oi.price);

            if (oi.quantityObject.KGQuantity > 0) {
                double quantiy = oi.quantityObject.KGQuantity;
                orderItemsOfOrder.put("quantity", quantiy);
                orderItemsOfOrder.put("totalPrice", quantiy * oi.price);

                if (!boughtOnSale) {
                    orderItemsOfOrder.put("totalPricePerItem", oi.price);

                } else {
                    double totalPricePerItem = oi.price / quantiy;
                    orderItemsOfOrder.put("totalPricePerItem", Double.parseDouble((String.format("%.2f", totalPricePerItem))));
                }
            } else {
                int quantiy = oi.quantityObject.integerQuantity;
                orderItemsOfOrder.put("quantity", quantiy);
                if (!boughtOnSale) {
                    orderItemsOfOrder.put("totalPrice", quantiy * oi.price);
                    orderItemsOfOrder.put("totalPricePerItem", oi.price);

                } else {
                    double totalPricePerItem = (double) orderItemsOfOrder.get("totalPrice") / (double) orderItemsOfOrder.get("quantiy");
                    orderItemsOfOrder.put("totalPricePerItem", Double.parseDouble((String.format("%.2f", totalPricePerItem))));

                }
            }
        }
        return orderItemsOfOrder;
    }

    public int CalculateNumOfItemsTypes(SuperDuperMarket sdm) {
        int numOfItemsTypes = 0;
        for (Store store : sdm.Stores
        ) {
            numOfItemsTypes += store.Inventory.stream().map(x -> x.itemId).collect(Collectors.toList()).size();
        }
        return numOfItemsTypes;
    }

    public double CalculateAvgOfOrders(SuperDuperMarket sdm) {
        double sumTotalPrice = 0;

        for (Order order : sdm.Orders.ordersMap.values()
        ) {
            sumTotalPrice += order.totalItemsPrice;
        }
        if (sdm.Orders.ordersMap.values().size() == 0) {
            return 0;
        } else {
            return sumTotalPrice / sdm.Orders.ordersMap.values().size();
        }
    }


}
