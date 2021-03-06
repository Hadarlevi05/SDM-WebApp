package Models;

import Handlers.Mapper;
import Handlers.OrderManager;
import Handlers.StoreHandler;
import Handlers.SuperDuperHandler;
import XMLHandler.XMLValidator;
import com.google.gson.annotations.Expose;
import generatedClasses.SDMStore;
import generatedClasses.SuperDuperMarketDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.List;

@XmlRootElement
public class SuperDuperMarket {

    @Expose
    @XmlTransient
    public List<Store> Stores;
    @Expose
    @XmlTransient
    public List<Item> Items;
    @Expose
    @XmlElement
    public OrderManager Orders;
    @Expose
    public String Area;


    Mapper sdmMapper;
    SuperDuperHandler sdmHandler;
    StoreHandler storeHandler;

    public SuperDuperMarket(){

        sdmHandler = new SuperDuperHandler();
        storeHandler = new StoreHandler();
        Orders = new OrderManager();
    }

    public SDMResultObject loadSDMFromXML(String xmlFilePath) throws JAXBException {
        String msg;
        SDMResultObject result = new SDMResultObject();
        try {
            File xmlFile = new File(xmlFilePath);
            if (!xmlFile.exists()){
                throw new Exception("File doesn't exist.");
            }
            JAXBContext jaxbContext = JAXBContext.newInstance("generatedClasses");
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SuperDuperMarketDescriptor SuperDuperMarketDescriptor = (SuperDuperMarketDescriptor) jaxbUnmarshaller.unmarshal(xmlFile);
            sdmMapper = new Mapper(SuperDuperMarketDescriptor);
            XMLValidator validation = new XMLValidator(SuperDuperMarketDescriptor);
            XMLHandler.XMLValidationResult validationResult = validation.StartChecking();

            if (validationResult.isValid()) {
                msg = "SDM File loaded successfully";
                result.setIsHasError(false);

                this.Area = SuperDuperMarketDescriptor.getSDMZone().getName();

                this.Stores = sdmMapper.CastSDMStoresToListOfStore(SuperDuperMarketDescriptor.getSDMStores(), "not-in-use");
                this.Items = sdmMapper.CastSDMItemsToListOfItem(SuperDuperMarketDescriptor.getSDMItems().getSDMItem());
                for (SDMStore sdmStore: SuperDuperMarketDescriptor.getSDMStores().getSDMStore()) {
                    Store store = storeHandler.getStoreById(this,sdmStore.getId());
                    store.Inventory = sdmMapper.CastSDMItemsToListOfOrderItemsList(sdmStore);
                    store.Sales = sdmMapper.CastSDMSalesToListOfDiscounts(sdmStore);
                }
                //this.Customers = sdmMapper.CastSDMCustomersToListOfCustomer(SuperDuperMarketDescriptor.get());

            } else {
                result.setIsHasError(true);
                result.setErrorMSG(validationResult.getMessage());
            }
        }
        catch (Exception ex) {
            result.setIsHasError(true);
            result.setErrorMSG(ex.getMessage());
        }
        return result;
    }
}
