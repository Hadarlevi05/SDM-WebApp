package Servlets;
        import DTO.KeyValueDTO;
        import DataStore.DataStore;
        import Enums.NotificationType;
        import Handlers.NotificationsHandler;
        import Handlers.StoreHandler;
        import Handlers.SuperDuperHandler;
        import Models.*;
        import UIUtils.ServletHelper;
        import javax.servlet.ServletException;
        import javax.servlet.annotation.WebServlet;
        import javax.servlet.http.HttpServlet;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;
        import java.io.IOException;
        import java.util.*;

@WebServlet(
        urlPatterns = "/stores"
)

public class StoresServlet extends HttpServlet {
    private SuperDuperHandler superDuperHandler;
    private StoreHandler storeHandler;

    public StoresServlet() {
        superDuperHandler = new SuperDuperHandler();
        storeHandler = new StoreHandler();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String area = request.getParameter("area");

        List<Map<String, Object>> rows = superDuperHandler.getStoresDetails(area);

        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;
        keyValueDTO.Values.put("Rows", rows);

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String area = request.getParameter("area");
        String name = request.getParameter("name");
        String username = request.getParameter("username");
        Integer ppk = Integer.parseInt(request.getParameter("ppk"));
        Integer locationX = Integer.parseInt(request.getParameter("locationX"));
        Integer locationY = Integer.parseInt(request.getParameter("locationY"));
        String[] items = request.getParameterValues("items[]");

        SDMLocation loc = new SDMLocation(locationX,locationY);

        DataStore dataStore = DataStore.getInstance();
        StoreOwner storeOwner = dataStore.userConfigurationDataStore.getByArea(area);
        SuperDuperMarket sdm = storeOwner.superDuperMarket;

        int serialNumber = getNextSerialNumber(sdm);

        Store store = new Store(serialNumber, name, ppk, loc, username);
        store.Inventory = new ArrayList<>();

        storeHandler.addStore(sdm, store);

        for (String item: items) {
            Integer id = Integer.parseInt(item);
            OrderItem orderItem = superDuperHandler.getOrderItemById(sdm, id);

            storeHandler.addItemToStore(orderItem, store);
        }

        new NotificationsHandler().Add(serialNumber, NotificationType.Store, storeOwner.username);
        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;

        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    private int getNextSerialNumber(SuperDuperMarket sdm){
        Store lastStore = Collections.max(sdm.Stores, Comparator.comparing(s -> s.serialNumber));
        int serialNumber = lastStore.serialNumber + 1;

        return serialNumber;
    }
}