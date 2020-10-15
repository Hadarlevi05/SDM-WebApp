package Models;


import Enums.OperatorTypeOfSale;

import java.util.List;

public class Discount {

    public Discount(int discountId, String Name, int ItemID, double Quantity, OperatorTypeOfSale OperatorType, List<Offer> offers, int storeId){
        this.Id = discountId;
        this.Name = Name;
        this.ItemID = ItemID;
        this.Quantity = Quantity;
        this.OperatorType = OperatorType;
        this.StoreID = storeId;
        this.Offers = offers;
    }

    public String Name;
    public int ItemID;
    public double Quantity;
    public int Id;
    public int StoreID;

    public OperatorTypeOfSale OperatorType;
    public List<Offer> Offers;

}