package Models;

public class Offer {

    public Offer(int ItemID, double Quantity, double ForAdditional, int storeID) {
        this.ItemID = ItemID;
        this.Quantity = Quantity;
        this.ForAdditional = ForAdditional;
        this.storeID = storeID;

    }

    public int ItemID;
    public int storeID;
    public double Quantity;
    public double ForAdditional;
}