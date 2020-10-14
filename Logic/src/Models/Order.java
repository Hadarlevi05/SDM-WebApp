package Models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement
public class Order {

    public Order() {
        this.totalItemsNum = 0;
        this.numOfItemsTypes = 0;
        this.deliveryPrice = 0;
        this.totalPrice=0;
        this.totalItemsPrice = 0;
        this.orderItems = new ArrayList<>();
        this.orderItemsFromSales = new ArrayList<>();
        this.deliveryPriceByStore = new HashMap<>();
        this.storesID =  new ArrayList<>();
    }
    @XmlElement
    public int id;
    @XmlElement
    public int customerId;
    @XmlElement
    public Date purchaseDate;
    @XmlElement
    public double totalItemsNum;
    @XmlElement
    public int numOfItemsTypes;
    @XmlElement
    public double deliveryPrice;
    @XmlElement
    public Map<Integer, Double> deliveryPriceByStore;
    @XmlElement
    public List<Integer> storesID;
    @XmlElement
    public double totalPrice;
    @XmlElement
    public double totalItemsPrice;
    @XmlElement
    public List<OrderItem> orderItems;
    public List<OrderItem> orderItemsFromSales;

    @XmlElement
    public SDMLocation CustomerLocation;

}
