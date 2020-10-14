package Models;

import Enums.PurchaseType;

public class Item {

    public Item(int serialNumber, String name, String purchaseType){//,double price, int soldItemsNumber ){
        this.serialNumber = serialNumber;
        this.name = name;
        this.purchaseType = PurchaseType.valueOf(purchaseType.toUpperCase());
    }
    public Item(){

    }
    public int serialNumber;
    public String name = null;
    public PurchaseType purchaseType;

    public String getName() {
        return name;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    @Override
    public String toString() {
        return name + " " + "[" + serialNumber + "]";
    }
}