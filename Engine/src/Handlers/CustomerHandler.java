package Handlers;

import Models.Customer;
import Models.Order;
import Models.SuperDuperMarket;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerHandler {

    public Customer getCustomerByID(SuperDuperMarket sdm, int serialNumber) {
        List<Customer> customers = sdm.Customers.stream().filter(customer -> customer.serialNumber == serialNumber).collect(Collectors.toList());
        if (!customers.isEmpty()) {
            return customers.get(0);
        }
        return null;
    }

    public double calculateAvgOfOrderedItems(SuperDuperMarket superDuperMarket, Customer cust) {
        double sumOrderedItems = 0;
        int numOrderedItems = 0;

        for (Order order : superDuperMarket.Orders.ordersMap.values()) {
            if (order.customerId == cust.serialNumber) {
                sumOrderedItems += order.totalItemsPrice;
                numOrderedItems += order.totalItemsNum;
            }
        }
        if (numOrderedItems == 0) {
            return 0;
        } else {
            return sumOrderedItems / numOrderedItems;
        }
    }

    public double calculateAvgOfdeliveries(SuperDuperMarket superDuperMarket, Customer cust) {
        double sumDeleviries = 0;

        for (Order order : superDuperMarket.Orders.ordersMap.values()) {
            if (order.customerId == cust.serialNumber) {
                sumDeleviries += order.deliveryPrice;
            }
        }
        if (cust.OrderIDs.size() == 0) {
            return 0;
        } else {
            return sumDeleviries / cust.OrderIDs.size();
        }
    }
}
