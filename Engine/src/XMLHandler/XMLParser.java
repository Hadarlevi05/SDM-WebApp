
package XMLHandler;

import Models.SuperDuperMarket;
import generatedClasses.SuperDuperMarketDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class XMLParser {

    public static SuperDuperMarketDescriptor unMarshalXMLDataToSdm(String data) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance("Parser");
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        SuperDuperMarketDescriptor sdm = (SuperDuperMarketDescriptor) jaxbUnmarshaller.unmarshal(new StringReader(data));
        return sdm;
    }

/*

    public String getMagitRepositoryPath(){
        return magitRepositoryPath;
    }

    public void setMagitRepositoryPath(String path){
        magitRepositoryPath = path;
    }
    public MagitRepository getMagitRepository() {
        return parsedRepo;
    }

    private void unMarshalXMLData(String data) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance("Parser");
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        parsedRepo = (MagitRepository) jaxbUnmarshaller.unmarshal(new StringReader(data));

    }

    public boolean isRepoValid(){
        XMLValidator validation = new XMLValidator(parsedRepo, magitRepositoryPath);
        XMLValidationResult validationResult = validation.StartChecking();
        return validationResult.isValid();
    }
*/

}


