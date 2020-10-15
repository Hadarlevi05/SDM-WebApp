package XMLHandler;

import Handlers.OrderManager;
import Models.Order;
import Models.SuperDuperMarket;
import generatedClasses.SDMItem;
import generatedClasses.SuperDuperMarketDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class XMLUtils {

    private String ordersHistoryPath;

    public static List<SDMItem> getItemsOfStoreByStoreID(SuperDuperMarketDescriptor sdmDescriptor, int storeID) {
        List<Integer> itemsIds = sdmDescriptor.getSDMStores().getSDMStore().stream().
                filter(x-> x.getId() == storeID).
                collect(Collectors.toList()).get(0).getSDMPrices().getSDMSell().stream()
                .map(y->y.getItemId()).collect(Collectors.toList());

        List<SDMItem> sDMItems = new ArrayList<>();
        for (int itemId:itemsIds) {
            sDMItems.add(getItemByID(sdmDescriptor, itemId));
        }
        return sDMItems;
    }


    public static SDMItem getItemByID(SuperDuperMarketDescriptor sdmDescriptor, int itemId) {
        List<SDMItem> items = sdmDescriptor.getSDMItems().getSDMItem().stream().
                filter(x-> x.getId()== itemId).collect(Collectors.toList());
        if (!items.isEmpty()) {
            return items.get(0);
        }
        return null;
    }

    public void writrOrdersToFile(String path, SuperDuperMarket superDuperMarket)throws Exception {
        try {
            if (superDuperMarket.Orders.ordersMap.size() == 0){
                throw new Exception("There are no orders in the system yet.");
            }
            ordersHistoryPath = path;

            File file = new File(ordersHistoryPath);

            JAXBContext jaxbContext = JAXBContext.newInstance(SuperDuperMarket.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(superDuperMarket, file);

        } catch (Exception e) {
            throw e;
        }
    }

    public Order[] loadOrdersFromFile(String path) throws Exception {

        try {

            ordersHistoryPath = path;

            File xmlFile = new File(ordersHistoryPath);
            if (!xmlFile.exists()){
                throw new Exception("File doesn't exist.");
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(SuperDuperMarket.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            SuperDuperMarket descriptor = (SuperDuperMarket) jaxbUnmarshaller.unmarshal(xmlFile);

            OrderManager orderManager = descriptor.Orders;

            return orderManager.ordersMap.values().toArray(new Order[0]);

        } catch (Exception e) {
            throw e;
        }
    }


}