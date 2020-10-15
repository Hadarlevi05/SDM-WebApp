package Models;

public class OrderItem {
    public int itemId;
    public int storeId;
    public double price;
    public QuantityObject quantityObject;
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
