package XMLHandler;
import generatedClasses.*;
import com.sun.media.sound.InvalidDataException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class XMLValidator {

    private String xmlPath;
    private SuperDuperMarketDescriptor sdm;

    public XMLValidator(SuperDuperMarketDescriptor superMarket){
        sdm = superMarket;
        //xmlPath = xmlFilePath;
    }

    public XMLValidationResult StartChecking () {
        XMLValidationResult validationResult = new XMLValidationResult();
        validationResult.setIsValid(true);
        try{
            //checkFileExitsAndXml(xmlPath);
            checkDoubledIdOfItems(sdm.getSDMItems());
            checkDoubledIdOfStores(sdm.getSDMStores());
            checkSellItemsExistInStore(sdm.getSDMStores(),sdm.getSDMItems());
            checkAllItemsExistInAtListOneStore(sdm.getSDMStores(),sdm.getSDMItems());
            checkItemSelledOnceInStore(sdm.getSDMStores(),sdm.getSDMItems());
            checkStoreCoordinates(sdm.getSDMStores());
            //checkDoubledIdOfCustomers(sdm.getSDMCustomers());
            //checkUniqueCoordinated(sdm.getSDMCustomers(), sdm.getSDMStores());

        }
        catch (InvalidDataException e) {
            validationResult.setIsValid(false);
            validationResult.setMessage(e.getMessage());
        } catch (Exception e) {
            validationResult.setIsValid(false);
            validationResult.setMessage(e.getMessage());
        }
        return validationResult;
    }

    // =====================
    // validation number 3.1
    // =====================
    private void checkFileExitsAndXml(String path) throws Exception{
        String errorMsg;
        File file = new File(path);
        if (!file.exists()) {
            errorMsg = String.format("Xml File: %s does not exist.", path);
            throw new Exception(errorMsg);
        }
        if (!path.endsWith(".xml")) {
            errorMsg = String.format("The file should be XML", path);
            throw new Exception(errorMsg);
        }

    }


    // =====================
    // validation number 3.2
    // =====================
    private void checkDoubledIdOfItems(SDMItems sdmItems)  throws Exception{
        String errorMsg;
        Map<Integer, SDMItem> mapItem = new HashMap<>();
        for (SDMItem curr : sdmItems.getSDMItem()) {
            if (mapItem.containsKey(curr.getId())) {
                errorMsg = "Found two items with the same ID in the XML file.";
                throw new Exception(errorMsg);
            }
            mapItem.put(curr.getId(), curr);
        }

        /*if (!XMLUtils.isEmptyRepo(currentRepo)) {
            for (MagitSingleBranch curr : branches.getMagitSingleBranch()) {
                if (XMLUtils.getMagitSingleCommitByID(currentRepo,
                        curr.getPointedCommit().getId()) == null) {
                    errorMsg = String.format("Branch %s points to commit that doesn't exists", curr.getName());
                    throw new InvalidDataException(errorMsg);
                }
            }
        }
*/
    }

   // ===============================
   // validation number 3.3
   // ===============================
    private void checkDoubledIdOfStores(SDMStores sdmStores) throws Exception {
        String errorMsg;
        Map<Integer, SDMStore> mapStore = new HashMap<>();
        for (SDMStore curr : sdmStores.getSDMStore()) {
            if (mapStore.containsKey(curr.getId())) {
                errorMsg = "Found two stores with the same ID in the XML file.";
                throw new Exception(errorMsg);
            }
            mapStore.put(curr.getId(), curr);
        }
    }

   // ===============================
   // validation number 3.4
   // ===============================
    private void checkSellItemsExistInStore(SDMStores sdmStores, SDMItems sdmItems) throws Exception {
        String errorMsg;
        for (SDMStore store : sdmStores.getSDMStore()) {
            for (SDMSell sell : store.getSDMPrices().getSDMSell()) {
                if (!sdmItems.getSDMItem().stream().map(item->item.getId()).collect(Collectors.toList()).contains(sell.getItemId())){
                    errorMsg = "Trying to sell an item that doesn't exist in any store.";
                    throw new Exception(errorMsg);
                }
            }
        }
    }

    // ===============================
    // validation number 3.5
    // ===============================
    private void checkAllItemsExistInAtListOneStore(SDMStores sdmStores, SDMItems sdmItems) throws Exception{
        String errorMsg;
        for (SDMItem item : sdmItems.getSDMItem()) {
            boolean isItemForSell = false;
            for (SDMStore store : sdmStores.getSDMStore()) {
                if (store.getSDMPrices().getSDMSell().stream().map(sell->sell.getItemId()).collect(Collectors.toList()).contains(item.getId())){
                    isItemForSell = true;
                    break;
                }
            }
            if (!isItemForSell){
                errorMsg = "Trying to sell an item that doesn't exist in any store.";
                throw new Exception(errorMsg);
            }
        }

    }

    // ===============================
    // validation number 3.6
    // ===============================
    private void checkItemSelledOnceInStore(SDMStores sdmStores, SDMItems sdmItems)  throws Exception{
        String errorMsg;
        for (SDMStore store : sdmStores.getSDMStore()) {
            for (SDMSell sell : store.getSDMPrices().getSDMSell()) {
                if (store.getSDMPrices().getSDMSell().stream().filter(x->x.getItemId() == sell.getItemId()).collect(Collectors.toList()).size() > 1){
                    errorMsg = "Trying to sell an item more than once in store.";
                    throw new Exception(errorMsg);
                }
            }
        }
    }

    // ===============================
    // validation number 3.7
    // ===============================

   private void checkStoreCoordinates(SDMStores sdmStores)  throws Exception{
      String errorMsg;
    for (SDMStore store : sdmStores.getSDMStore()) {
      if(store.getLocation().getX()<1 || store.getLocation().getX()>50 || store.getLocation().getY()<1 || store.getLocation().getY()>50)
            {
                  errorMsg = "Store coordinates of Store ID: " + store.getId() + " are out of range. ";
                 throw new Exception(errorMsg);
               }
           }
       }


    // ===============================
    // ex 2 - validation number 1
    // ===============================

    private void checkDoubledIdOfCustomers(SDMCustomers sdmCustomers) throws Exception {
        String errorMsg;
        Map<Integer, SDMCustomer> mapCustomer = new HashMap<>();
        for (SDMCustomer curr : sdmCustomers.getSDMCustomer()) {
            if (mapCustomer.containsKey(curr.getId())) {
                errorMsg = "Found two customers with the same ID in the XML file.";
                throw new Exception(errorMsg);
            }
            mapCustomer.put(curr.getId(), curr);
        }
    }

    // ===============================
    // ex 2 - validation number 2
    // ===============================

    private void checkUniqueCoordinated(SDMCustomers sdmCustomers, SDMStores sdmStores)  throws Exception{
        Map<Integer, Integer> coordinates = new HashMap<>();

        String errorMsg = "Duplicated entries on the same coordinate";;
        for (SDMStore store : sdmStores.getSDMStore()) {
            Location loc = store.getLocation();
            if(coordinates.containsKey(loc.getX()) && coordinates.get(loc.getX()) ==loc.getY() )
            {
                throw new Exception(errorMsg);
            }
            coordinates.put(loc.getX(), loc.getY());
        }

        for (SDMCustomer customer : sdmCustomers.getSDMCustomer()) {
            Location loc = customer.getLocation();
            if(coordinates.containsKey(loc.getX()) && coordinates.get(loc.getX()) ==loc.getY() )
            {
                throw new Exception(errorMsg);
            }
            coordinates.put(loc.getX(), loc.getY());
        }
    }
    // ===============================
    // ex 2 - validation number 3
    // ===============================

    private void validateSaleItems(SDMCustomers sdmCustomers, SDMStores sdmStores)  throws Exception{
        // TODO
    }
}