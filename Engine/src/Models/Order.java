package Models;

import Enums.OrderStatus;
import com.google.gson.annotations.Expose;

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
        this.orderType = "STATIC" ;

    }
    @XmlElement
    @Expose
    public int id;
    @Expose
    public String orderType;
    @XmlElement
    @Expose
    public int customerId;
    @XmlElement
    @Expose
    public Date purchaseDate;
    @XmlElement
    @Expose
    public double totalItemsNum;
    @XmlElement
    @Expose
    public int numOfItemsTypes;
    @XmlElement
    @Expose
    public double deliveryPrice;
    @XmlElement
    @Expose
    public Map<Integer, Double> deliveryPriceByStore;
    @XmlElement
    @Expose
    public List<Integer> storesID;
    @XmlElement
    @Expose
    public double totalPrice;
    @XmlElement
    @Expose
    public double totalItemsPrice;
    @XmlElement
    @Expose
    public List<OrderItem> orderItems;
    public List<OrderItem> orderItemsFromSales;
    @Expose
    public OrderStatus orderStatus ;
    @XmlElement
    public SDMLocation CustomerLocation;

}
