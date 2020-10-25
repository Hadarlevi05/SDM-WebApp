package Handlers;

import DataStore.NotificationsDataStore;
import Enums.NotificationType;
import Models.Feedback;
import Models.Notification;
import Models.Order;
import Models.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationsHandler {
    private NotificationsDataStore _NotificationsDataStore = NotificationsDataStore.getInstance();

    public NotificationsHandler(){
    }

    private List<Notification> GetNotificationsByUsername(String username) {
        List<Notification> all = _NotificationsDataStore.get();

        return all.stream().filter(notification -> notification.UserName.equals(username) && !notification.sent).collect(Collectors.toList());
    }

    public List<String> GetNotificationsMessages(String username) {
        List<Notification> all = GetNotificationsByUsername(username);
        _NotificationsDataStore.UpdateRead(all.stream().map(n -> n.Id).collect(Collectors.toList()));

        return all.stream().map(m -> m.Message).collect(Collectors.toList());
    }

    public void AddStore(Store store, String username, Integer numberOfItems) {
        String message = String.format(
                "New Store added! Name: %1$s, Owner: %2$s, Location: %3$s, Number Of Sold Items Out Of Items In Area: %5$s/%4$s ",
                store.name,
                store.Username,
                store.Location.toString(),
                numberOfItems,
                store.Inventory.stream().count());


        this.Add(message, username);
    }

    public void AddOrder(Order order, String usernameOwner,String usernameCustomer ) {

        String message = String.format(
                "New Order! Id: %1$d, Customer Name: %2$s, Number Of Item Types: %3$s, Total Items Price: %4$s, Delivery Price: %5$s",
                order.id,
                usernameCustomer, // TODO: NAME
                order.numOfItemsTypes,
                order.totalItemsPrice,
                order.deliveryPrice);
        this.Add(message, usernameOwner);
    }

    public void AddFeedback(Feedback[] feedbacks, String username) {
        for (int i = 0; i<feedbacks.length; i++){
            Feedback feedback = feedbacks[i];

            String message = String.format(
                    "New Feedback! Rate %1$s, Message: %2$s",
                    feedback.rate,
                    feedback.message);

            this.Add(message, username);
        }
    }

    private void Add(String message, String username) {
        Notification notification = new Notification(message, username);

        _NotificationsDataStore.add(notification);
    }
}