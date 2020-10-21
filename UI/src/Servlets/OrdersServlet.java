
package Servlets;

import DTO.InnerJSON;
import DTO.KeyValueDTO;
import DTO.OrderData;
import DataStore.DataStore;
import Handlers.OrderDetailsHandler;
import Handlers.OrderManager;
import Handlers.StoreHandler;
import Handlers.SuperDuperHandler;
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
                //.excludeFieldsWithoutExposeAnnotation()
                .create();

        OrderData fromData = gson.fromJson(jb.toString(), OrderData.class);


        SdmUser user = SessionUtils.getUser(request);
        String area = request.getParameter("area");

        StoreOwner storeOwner = DataStore.getInstance().userConfigurationDataStore.getByArea(area);



        Order order = new Order();

        order.customerId = user.id;
        String dateString = Arrays.asList(fromData.data).stream().filter(x->x.name.equals("purchaseDate")).collect(Collectors.toList()).get(0).value;
        SimpleDateFormat formatter2=new SimpleDateFormat("yyyy-MM-dd");
        try {
            order.purchaseDate=formatter2.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int storeId = new Integer(Arrays.asList(fromData.data).stream().filter(x->x.name.equals("storeCombo")).collect(Collectors.toList()).get(0).value);
        if (!order.storesID.contains(storeId)) {
            order.storesID.add(storeId);
        }
        int locx = new Integer(Arrays.asList(fromData.data).stream().filter(x->x.name.equals("locationX")).collect(Collectors.toList()).get(0).value);
        int locy = new Integer(Arrays.asList(fromData.data).stream().filter(x->x.name.equals("locationY")).collect(Collectors.toList()).get(0).value);
        storeOwner.superDuperMarket.Orders.addOrder(storeOwner.superDuperMarket, order);

        for (InnerJSON json :
                fromData.data) {
            if (json.name.contains("qs")) {
                double qountity = new Integer(json.value);
                if (qountity > 0) {
                    int itemID = new Integer(json.name.substring(3));
                    OrderItem oi = new OrderItem(itemID, qountity, storeId);
                    order.orderItems.add(oi);

                    QuantityObject quantityObject = new QuantityObject();
                    if ((qountity == Math.floor(qountity)) && !Double.isInfinite(qountity)) {
                        quantityObject.integerQuantity = (int) qountity;
                    } else {
                        quantityObject.KGQuantity = qountity;

                    }
                    Store store = new StoreHandler().getStoreById(storeOwner.superDuperMarket, storeId);
                    SDMLocation sdmLocation = new SDMLocation(locx, locy);
                    Customer customer = new Customer(user.id, user.username, sdmLocation);
                    new OrderDetailsHandler().updateOrderDetails(storeOwner.superDuperMarket, customer, order, oi, store, sdmLocation, order.purchaseDate, quantityObject);

                }
            }
        }
/*        try {
            JSONObject jsonObject =  HTTP.toJSONObject(jb.toString());
        } catch (JSONException e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }       */


        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("OrderID", order.id);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    private void getAllStore(HttpServletRequest request, HttpServletResponse response) {

    }

}
