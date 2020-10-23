package Models;

import com.google.gson.annotations.Expose;

public class OrderItem {

    @Expose
    public int itemId;
    @Expose
    public int storeId;
    @Expose
    public double price;
    @Expose
    public QuantityObject quantityObject;
    @Expose
    public Boolean isFromSale;

    public OrderItem(){
        this.quantityObject= new QuantityObject();
    }

    public OrderItem(int itemId, double price, int storeId) {
        this.itemId= itemId;
        this.storeId= storeId;
        this.price= price;
        this.quantityObject= new QuantityObject();

    }

}
