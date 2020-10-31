package Servlets;

import DTO.InnerJSON;
import DTO.KeyValueDTO;
import DTO.OrderData;
import DataStore.DataStore;
import Enums.OperatorTypeOfSale;
import Handlers.OrderDetailsHandler;
import Handlers.OrderManager;
import Handlers.SuperDuperHandler;
import Models.*;
import UIUtils.ServletHelper;
import UIUtils.SessionUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(
        urlPatterns = "/sales"
)

public class SalesServlet extends HttpServlet {

    private SuperDuperHandler superDuperHandler;
    private OrderManager orderManager;
    private OrderDetailsHandler orderDetailsHandler;

    public SalesServlet() {
        superDuperHandler = new SuperDuperHandler();
        orderManager = new OrderManager();
        orderDetailsHandler = new OrderDetailsHandler();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String area = request.getParameter("area");
        int orderID = new Integer(request.getParameter("orderID"));
        StoreOwner storeOwner = DataStore.getInstance().userConfigurationDataStore.getByArea(area);
        SuperDuperMarket sdm = storeOwner.superDuperMarket;

        List<Discount> sales = orderManager.checkForSales(storeOwner.superDuperMarket, sdm.Orders.ordersMap.get(orderID));

        List<Map<String, Object>> rows = new ArrayList();
        KeyValueDTO keyValueDTO = new KeyValueDTO();
        for (Discount discount :
                sales) {
            Map<String, Object> map = new HashMap<>();
            map.put("saleName", discount.Name);
            map.put("operatorType", discount.OperatorType);
            map.put("discountID", discount.Id);


            List<Map<String, Object>> items = new ArrayList();
            for (Offer offer : discount.Offers) {

                Item item = superDuperHandler.getItemById(sdm, offer.ItemID);

                Map<String, Object> rowItem = new HashMap<>();
                rowItem.put("quantity", offer.Quantity);
                rowItem.put("forAdditional", offer.ForAdditional);
                rowItem.put("purchaseType", offer.ItemID);
                rowItem.put("itemName", item.name);
                rowItem.put("itemID", item.serialNumber);

                items.add(rowItem);
            }
            map.put("offers", items);

            rows.add(map);

        }

        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("Rows", rows);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    private void getAllStore(HttpServletRequest request, HttpServletResponse response) {

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

        OrderData sales = gson.fromJson(jb.toString(), OrderData.class);
        String area = request.getParameter("area");
        int orderID = new Integer(request.getParameter("orderID"));

        StoreOwner storeOwner = DataStore.getInstance().userConfigurationDataStore.getByArea(area);
        SuperDuperMarket sdm = storeOwner.superDuperMarket;
        List<Offer> selectedOffers = new ArrayList<>();
        for (InnerJSON json :
                sales.data) {

            int discountID = new Integer(json.name.split("_")[1]);
            Discount discount = superDuperHandler.getDiscountByID(sdm, discountID);
            OperatorTypeOfSale operatorTypeOfSale = OperatorTypeOfSale.ONE_OF;
            if (json.value.equals("on")) {
                selectedOffers.addAll(discount.Offers);
                operatorTypeOfSale = OperatorTypeOfSale.ALL_OR_NOTHING;
            } else {
                int itemID = new Integer(json.value);
                selectedOffers.add(discount.Offers.stream().filter(x -> x.ItemID == itemID).collect(Collectors.toList()).get(0));
            }
        }
        Order order = sdm.Orders.ordersMap.get(orderID);
        orderDetailsHandler.updateOrderWithDiscount(sdm, order, selectedOffers);

        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("Order", order);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

}
