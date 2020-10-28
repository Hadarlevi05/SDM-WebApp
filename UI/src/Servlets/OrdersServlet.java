
package Servlets;

import DTO.InnerJSON;
import DTO.KeyValueDTO;
import DTO.OrderData;
import DataStore.DataStore;
import Enums.NotificationType;
import Enums.OrderStatus;
import Enums.TransactionType;
import Handlers.*;
import Models.*;
import UIUtils.ServletHelper;
import UIUtils.SessionUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(
        urlPatterns = "/orders"
)

public class OrdersServlet extends HttpServlet {

    private SuperDuperHandler superDuperHandler;

    public OrdersServlet() {
        superDuperHandler = new SuperDuperHandler();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .create();

        Order order = gson.fromJson(jb.toString(), Order.class);

        SdmUser user = SessionUtils.getUser(request);
        String area = request.getParameter("area");
        StoreOwner storeOwner = DataStore.getInstance().userConfigurationDataStore.getByArea(area);

        //int orderID = new Integer(Arrays.asList(fromData.data).stream().filter(x->x.name.equals("orderID")).collect(Collectors.toList()).get(0).value);
        //OrderStatus status = OrderStatus.values()[new Integer(Arrays.asList(fromData.data).stream().filter(x->x.name.equals("Status")).collect(Collectors.toList()).get(0).value)];

        // Order order = null;

        if (order.orderStatus == OrderStatus.NEW) {

            order = CreateNewOrder(request, order, storeOwner);
        }
        else if (order.orderStatus == OrderStatus.IN_PROGRESS) {
            order = storeOwner.superDuperMarket.Orders.ordersMap.get(order.id);

            new TransactionsHandler().doTransaction(user.username, -order.totalPrice, TransactionType.PAYMENT_TRANSFERENCE.toString(),null);
            new TransactionsHandler().doTransaction(storeOwner.username, order.totalPrice, TransactionType.RECEIVE_PAYMENT.toString(), null);

            order.orderStatus = OrderStatus.DONE;

            new NotificationsHandler().AddOrder(order, storeOwner.username, user.username );
        }

        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("Order", order);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    private Order CreateNewOrder(HttpServletRequest request, Order fromData, StoreOwner storeOwner) {
        SdmUser user = SessionUtils.getUser(request);

        Order order = new Order();
        order.orderStatus = OrderStatus.IN_PROGRESS;
        order.customerId = user.id;
        //String dateString = fromData.purchaseDate;//Arrays.asList(fromData.data).stream().filter(x -> x.name.equals("purchaseDate")).collect(Collectors.toList()).get(0).value;
        //SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        // try {
        order.purchaseDate = fromData.purchaseDate;// formatter2.parse(dateString);
/*        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        int storeId = fromData.storesID.get(0);//new Integer(Arrays.asList(fromData.data).stream().filter(x -> x.name.equals("storeCombo")).collect(Collectors.toList()).get(0).value);
        if (!order.storesID.contains(storeId)) {
            order.storesID.add(storeId);
        }
        storeOwner.superDuperMarket.Orders.addOrder(storeOwner.superDuperMarket, order);

        for (OrderItem orderItem :
                fromData.orderItems) {
            if (order.orderType.equals("purchase-type-dynamic")){

                orderItem.storeId = new OrderManager().FindCheapestStoreForItem(storeOwner.superDuperMarket,orderItem.itemId).storeId;
            }
            else{
                orderItem.storeId = fromData.storesID.get(0);
            }
            QuantityObject qauntity = orderItem.quantityObject;
/*            if (qauntity > 0) {
                int itemID = new Integer(json.name.substring(3));
                OrderItem oi = new OrderItem(itemID, qauntity, storeId);
                order.orderItems.add(oi);

                QuantityObject quantityObject = new QuantityObject();
                if ((qauntity == Math.floor(qauntity)) && !Double.isInfinite(qauntity)) {
                    quantityObject.integerQuantity = (int) qauntity;
                } else {
                    quantityObject.KGQuantity = qauntity;

                }*/
            Store store = new StoreHandler().getStoreById(storeOwner.superDuperMarket, storeId);
            SDMLocation sdmLocation = fromData.CustomerLocation;
            Customer customer = new Customer(user.id, user.username, sdmLocation);
            new OrderDetailsHandler().updateOrderDetails(storeOwner.superDuperMarket, customer, order, orderItem, store, sdmLocation, order.purchaseDate, qauntity);

        }
        return order;
    }



    private void getAllStore(HttpServletRequest request, HttpServletResponse response) {

    }

}
