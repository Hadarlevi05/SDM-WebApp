package Handlers;

import DTO.KeyValueDTO;
import DataStore.DataStore;
import Enums.OrderStatus;
import Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SuperDuperHandler {

    private StoreHandler storeHandler;
    private ItemHandler itemHandler;
    private LocationHandler locationHandler;
    private static DecimalFormat df = new DecimalFormat("0.00");

    public SuperDuperHandler() {
        storeHandler = new StoreHandler();
        itemHandler = new ItemHandler();
        locationHandler = new LocationHandler();
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


    public Discount getDiscountByID(SuperDuperMarket sdm, int discountID) {

        for (Store store : sdm.Stores) {
            for (Discount dis : store.Sales) {
                if (dis.Id == discountID)
                    return dis;
            }
        }
        return null;
    }


    public List<Map<String, Object>> getStoreAreaDetails(String username) {
        List<Map<String, Object>> rows = new ArrayList();
        KeyValueDTO keyValueDTO = new KeyValueDTO();
        List<StoreOwner> owners = dataStore.userConfigurationDataStore.list();
        for (StoreOwner storeOwner : owners) {
            Map<String, Object> map = new HashMap<>();

            map.put("storeowner", storeOwner.username);
            map.put("area", storeOwner.area);
            map.put("itemstypes", CalculateNumOfItemsTypes(storeOwner.superDuperMarket));

            map.put("storesnumber", storeOwner.superDuperMarket.Stores.size());
            map.put("ordersnumber", storeOwner.superDuperMarket.Orders.ordersMap.values().size());
            map.put("avgordersprice", String.format("%.2f", CalculateAvgOfOrders(storeOwner.superDuperMarket)));
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
        for (Store store : sdm.Stores) {
            Map<String, Object> map = new HashMap<>();
            map.put("serialnumber", store.serialNumber);
            map.put("name", store.name);
            map.put("owner", store.Username);
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
            map.put("numOfTakenOrders", store.OrderHistoryIDs.size());
            double totalItemsAmount = 0;
            for (int orderID : store.OrderHistoryIDs
            ) {
                Order order = sdm.Orders.GetOrderByOrderID(sdm, orderID);
                if (order.orderStatus == OrderStatus.DONE) {
                    totalItemsAmount += storeHandler.countTotalCostItemsOfStoreInOrder(order, store);
                }
            }

            map.put("itemsCost", String.format("%.2f", totalItemsAmount));

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
            map.put("averagePrice", String.format("%.2f", storeHandler.countAveragePriceOfSellingStores(sdm, item.serialNumber)));
            map.put("soldItemsAmount", String.format("%.2f",itemHandler.CalculateSoldItemsAmount(sdm, item.serialNumber)));

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
                    map.put("purchaseType", item.purchaseType);

                    rows.add(map);
                }
            }
        }

        return rows;
    }


    public List<Map<String, Object>> getOrderDetails(String area, int orderId) {

        DataStore dataStore = DataStore.getInstance();

        StoreOwner storeOwner = DataStore.getInstance().userConfigurationDataStore.getByArea(area);
        Order order = storeOwner.superDuperMarket.Orders.ordersMap.get(orderId);
        List<Map<String, Object>> rows = new ArrayList();
        KeyValueDTO keyValueDTO = new KeyValueDTO();
        for (int storeID : order.storesID
        ) {

            Store store = storeHandler.getStoreById(storeOwner.superDuperMarket, storeID);
            double distance = locationHandler.calculateDistanceOfTwoLocations(store.Location, order.CustomerLocation);

            Map<String, Object> map = new HashMap<>();
            map.put("storeID", store.serialNumber);
            map.put("storeName", store.name);
            map.put("distance", df.format(distance));
            map.put("PPK", store.PPK);
            map.put("deliveryPrice", df.format(order.deliveryPriceByStore.get(store.serialNumber)));
            List<Map<String, Object>> orderItemsDetails = new ArrayList();

            for (OrderItem oi : order.orderItems) {

                if (oi.storeId == store.serialNumber) {
                    Map<String, Object> orderItemDetails = getOrderDetailsOfOrderItemForCustomer(storeOwner.superDuperMarket, storeID, oi, false, true);
                    orderItemsDetails.add(orderItemDetails);
                }
            }
            for (OrderItem oi : order.orderItemsFromSales) {

                if (oi.storeId == store.serialNumber) {
                    Map<String, Object> orderItemDetails = getOrderDetailsOfOrderItemForCustomer(storeOwner.superDuperMarket, storeID, oi, true, true);
                    orderItemsDetails.add(orderItemDetails);
                }
            }


            map.put("orderItemsDetails", orderItemsDetails);
            rows.add(map);
        }


        return rows;
    }


    private Map<String, Object> getOrderDetailsOfOrderItemForCustomer(SuperDuperMarket superDuperMarket, int storeID, OrderItem oi, Boolean boughtOnSale, Boolean addStoreDetails) {

        Map<String, Object> OrderDetailsItem = new HashMap<>();
        OrderDetailsItem.put("boughtOnSale", boughtOnSale);
        if (oi.storeId == storeID) {
            Item item = getItemById(superDuperMarket, oi.itemId);

            OrderDetailsItem.put("itemID", item.serialNumber);
            OrderDetailsItem.put("name", item.name);
            OrderDetailsItem.put("purchaseType", item.purchaseType);
            OrderDetailsItem.put("price", oi.price);


            if (oi.quantityObject.KGQuantity > 0) {
                double quantiy = oi.quantityObject.KGQuantity;
                OrderDetailsItem.put("quantity", quantiy);
                OrderDetailsItem.put("totalPrice", String.format("%.2f", quantiy * oi.price));

                if (!boughtOnSale) {
                    OrderDetailsItem.put("totalPricePerItem", oi.price);
                } else {
                    double totalPricePerItem = (Double) OrderDetailsItem.get("totalPrice") / (Double) OrderDetailsItem.get("quantity");
                    OrderDetailsItem.put("totalPricePerItem", Double.parseDouble((String.format("%.2f", totalPricePerItem))));
                }
            } else {
                int quantiy = oi.quantityObject.integerQuantity;
                OrderDetailsItem.put("quantity", quantiy);

                if (!boughtOnSale) {
                    OrderDetailsItem.put("totalPrice", quantiy * oi.price);
                    OrderDetailsItem.put("totalPricePerItem", oi.price);

                } else {
                    OrderDetailsItem.put("totalPrice", oi.price);

                    double totalPricePerItem = (Double) OrderDetailsItem.get("totalPrice") / (Integer) OrderDetailsItem.get("quantity");
                    OrderDetailsItem.put("totalPricePerItem", Double.parseDouble((String.format("%.2f", totalPricePerItem))));
                }
            }
            if (addStoreDetails) {
                OrderDetailsItem.put("storeID", oi.storeId);
                Store store = storeHandler.getStoreById(superDuperMarket, oi.storeId);
                OrderDetailsItem.put("storeName", store.name);
            }

        }
        return OrderDetailsItem;
    }

    public List<Map<String, Object>> getOrdersHistoryDetailsForCustomer(String area, int userID) {

        DataStore dataStore = DataStore.getInstance();
        StoreOwner storeOwner = dataStore.userConfigurationDataStore.getByArea(area);
        List<Map<String, Object>> rows = new ArrayList();
        KeyValueDTO keyValueDTO = new KeyValueDTO();
        for (Order order :
                storeOwner.superDuperMarket.Orders.ordersMap.values()) {
            if (order.customerId == userID && order.orderStatus == OrderStatus.DONE) {
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
                            Map<String, Object> orderOtemDetails = getOrderDetailsOfOrderItemForCustomer(storeOwner.superDuperMarket, storeID, oi, false, true);
                            orderItems.add(orderOtemDetails);
                        }
                    }
                    for (OrderItem oi : order.orderItemsFromSales) {
                        if (oi.storeId == store.serialNumber) {
                            Map<String, Object> orderOtemDetails = getOrderDetailsOfOrderItemForCustomer(storeOwner.superDuperMarket, storeID, oi, true, true);
                            orderItems.add(orderOtemDetails);
                        }
                    }
                }
                map.put("orderItems", orderItems);
                rows.add(map);
            }

        }
        return rows;
    }

    private List<Map<String, Object>> GetOrdersOfStore(SdmUser sdmUser, SuperDuperMarket superDuperMarket, Store store) {
        List<Map<String, Object>> OrderDetails = new ArrayList<>();

        List<Integer> ordersIds = store.OrderHistoryIDs;
        if (ordersIds.size() > 0) {
            for (int orderID : ordersIds) {
                Map<String, Object> row = new HashMap<>();
                Order order = superDuperMarket.Orders.GetOrderByOrderID(superDuperMarket, orderID);
                DateFormat dateFormat = new SimpleDateFormat("dd/mm-hh:mm ");
                row.put("id", orderID);
                row.put("date", dateFormat.format(order.purchaseDate));
                row.put("customerName", sdmUser.username);
                row.put("customerLoc", "[" + order.CustomerLocation.x + "," + order.CustomerLocation.y + "]");
                row.put("numOfItems", String.format("%.2f", storeHandler.countTotalItemsAmountOfStoreInOrder(order, store)));
                row.put("itemsCost", String.format("%.2f", storeHandler.countTotalCostItemsOfStoreInOrder(order, store)));
                row.put("deleveryPrice", String.format("%.2f", storeHandler.countDeliveryPriceOfStoreInOrder(order, store)));
                row.put("totalPrice", String.format("%.2f", storeHandler.countTotalPriceOfStoreInOrder(order, store)));
                List<Map<String, Object>> orderItemsDetails = new ArrayList();
                for (OrderItem oi : order.orderItems) {
                    if (oi.storeId == store.serialNumber) {
                        Map<String, Object> orderOtemDetails = getOrderDetailsOfOrderItemForCustomer(superDuperMarket, store.serialNumber, oi, false, false);
                        orderItemsDetails.add(orderOtemDetails);
                    }
                }
                for (OrderItem oi : order.orderItemsFromSales) {
                    if (oi.storeId == store.serialNumber) {
                        Map<String, Object> orderOtemDetails = getOrderDetailsOfOrderItemForCustomer(superDuperMarket, store.serialNumber, oi, true, false);
                        orderItemsDetails.add(orderOtemDetails);
                    }
                }
                row.put("items", orderItemsDetails);
                OrderDetails.add(row);
            }


        }
        return OrderDetails;
    }

    public List<Map<String, Object>> getOrdersHistoryDetailsForStoreOwner(String area, SdmUser user) {

        List<Map<String, Object>> rows = new ArrayList();

        for (StoreOwner storeOwner :
                dataStore.userConfigurationDataStore.list()) {


            SuperDuperMarket sdm = storeOwner.superDuperMarket;
            List<Map<String, Object>> orders = new ArrayList<>();
            for (Store store : sdm.Stores) {

                if (store.Username.equals(user.username)) {
                    Map<String, Object> map = new HashMap<>();
                    orders = GetOrdersOfStore(user, sdm, store);
                    if (orders.size() > 0) {
                        map.put("storeName", store.name);
                        map.put("storeOrders", orders);
                        rows.add(map);
                    }
                }
            }
        }
        return rows;
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
