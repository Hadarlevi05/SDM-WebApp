package Servlets;

import DTO.KeyValueDTO;
import DataStore.DataStore;
import Handlers.Mapper;
import Handlers.StoreHandler;
import Models.SdmUser;
import Models.Store;
import Models.StoreOwner;
import Models.SuperDuperMarket;
import UIUtils.ServletHelper;
import UIUtils.SessionUtils;
import XMLHandler.XMLParser;
import XMLHandler.XMLValidationResult;
import XMLHandler.XMLValidator;
import com.google.gson.Gson;
import generatedClasses.SDMStore;
import generatedClasses.SuperDuperMarketDescriptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        urlPatterns = "/LoadSdm"
)

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadSdmServlet extends HttpServlet {

    private StoreHandler storeHandler = new StoreHandler();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("text/html;charset=UTF-8");

        KeyValueDTO keyValueDTO = new KeyValueDTO();
        keyValueDTO.Status = 200;


        try {


            SdmUser user = SessionUtils.getUser(request);

            SuperDuperMarket sdm = new SuperDuperMarket();
            String msg;
            String content = request.getParameter("file");
            SuperDuperMarketDescriptor superDuperMarketDescriptor = XMLParser.unMarshalXMLDataToSdm(content);

            Mapper sdmMapper = new Mapper(superDuperMarketDescriptor);
            XMLValidator validation = new XMLValidator(superDuperMarketDescriptor);
            XMLValidationResult validationResult = validation.StartChecking();

            if (validationResult.isValid()) {
                msg = "SDM File loaded successfully";
                String area = superDuperMarketDescriptor.getSDMZone().getName();
                sdm.Area = area;
                sdm.Stores = sdmMapper.CastSDMStoresToListOfStore(superDuperMarketDescriptor.getSDMStores());
                sdm.Items = sdmMapper.CastSDMItemsToListOfItem(superDuperMarketDescriptor.getSDMItems().getSDMItem());
                for (SDMStore sdmStore : superDuperMarketDescriptor.getSDMStores().getSDMStore()) {
                    Store store = storeHandler.getStoreById(sdm, sdmStore.getId());
                    store.Inventory = sdmMapper.CastSDMItemsToListOfOrderItemsList(sdmStore);
                    store.Sales = sdmMapper.CastSDMSalesToListOfDiscounts(sdmStore);
                }
                StoreOwner storeOwner = new StoreOwner(user.username,area, sdm);
                if (DataStore.getInstance().userConfigurationDataStore.getByArea(area)!=null){
                    keyValueDTO.Status = 400;
                    keyValueDTO.ErrorMessage = "Area is already taken by another store owner";
                }else{
                    DataStore.getInstance().userConfigurationDataStore.create(new StoreOwner(user.username,area, sdm));
                    keyValueDTO.Values.put("sdm", sdm);
                }
                
            } else {
                keyValueDTO.Status = 400;
                keyValueDTO.ErrorMessage = validationResult.getMessage();

            }
        } catch (Exception ex) {
            keyValueDTO.Status = 400;
            keyValueDTO.ErrorMessage = ex.getMessage();
        }
        ServletHelper.WriteToOutput(response, keyValueDTO);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    private String finishedLoadingXmlFile(String msg) {
        Gson gson = new Gson();
        return gson.toJson(msg);
    }

    @Override
    public String getServletInfo() {
        return "Servlet handling parsing and validating of xml file.";
    }
}

