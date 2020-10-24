package Handlers;

import DataStore.NotificationsDataStore;
import Enums.NotificationType;
import Models.Notification;

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
        List<String> messages = new ArrayList<>();

        List<Notification> all = GetNotificationsByUsername(username);
        _NotificationsDataStore.UpdateRead(all.stream().map(n -> n.Id).collect(Collectors.toList()));

        for (Notification notification : all) {
            String msg = "";

            switch (notification.ItemType) {
                case "Store": {
                    msg = "This is a Store: " + notification.ItemID;
                    break;
                }
                case "Order": {
                    msg = "This is a Order: " + notification.ItemID;
                    break;
                }
                case "Feedback": {
                    msg = "This is a Feedback: " + notification.ItemID;
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
            messages.add(msg);
        }
        return messages;
    }

    public void Add(int itemId, NotificationType itemType, String username) {
        Notification notification = new Notification(itemId,itemType.toString(),username);

        _NotificationsDataStore.add(notification);
    }
}

