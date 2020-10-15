package Models;

import Handlers.OrderManager;

import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "superDuperMarket")
public class SuperDuperMarketXmlDescriptor {

    @XmlElement(name = "Orders", required = true)
    protected OrderManager Orders;

    public OrderManager getOrderManager() {
        return Orders;
    }
}

