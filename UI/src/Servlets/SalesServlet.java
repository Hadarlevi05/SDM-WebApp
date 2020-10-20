package Servlets;

import DTO.KeyValueDTO;
import DataStore.DataStore;
import Handlers.OrderManager;
import Handlers.SuperDuperHandler;
import Models.*;
import UIUtils.ServletHelper;
import UIUtils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(
        urlPatterns = "/sales"
)

public class SalesServlet extends HttpServlet {

    private SuperDuperHandler superDuperHandler;
    private OrderManager orderManager;

    public SalesServlet() {
        superDuperHandler = new SuperDuperHandler();
        orderManager = new OrderManager();
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

            List<Map<String, Object>> items = new ArrayList();
            for (Offer offer : discount.Offers) {

                Item item = superDuperHandler.getItemById(sdm,offer.ItemID);

                Map<String, Object> rowItem = new HashMap<>();
                rowItem.put("quantity", offer.Quantity);
                rowItem.put("forAdditional", offer.ForAdditional);
                rowItem.put("purchaseType", offer.ItemID);
                rowItem.put("itemName", item.name);
                items.add(rowItem);
            }
            rows.add(map);

        }

        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("Rows", rows);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    private void getAllStore(HttpServletRequest request, HttpServletResponse response) {

    }

}
