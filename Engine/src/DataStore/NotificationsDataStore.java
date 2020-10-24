package DataStore;

import Models.Notification;
import java.util.ArrayList;
import java.util.List;

public class NotificationsDataStore {
    private static NotificationsDataStore _NotificationsDataStore = null;
    private static int id = 1;

    private List<Notification> notifications;

    public NotificationsDataStore () {
        notifications = new ArrayList();
    }

    public List<Notification> get() {
        return notifications;
    }

    public void UpdateRead(List<Integer> notificationIds) {
        for (Integer notificationId: notificationIds) {
            for (Notification notification: notifications) {
                if(notification.Id == notificationId){
                    notification.sent = true;
                }
            }
        }
    }

    public void add(Notification notification) {
        notification.Id = id++;
        this.notifications.add(notification);
    }

    // static method to create instance of Singleton class
    public static NotificationsDataStore getInstance() {
        if (_NotificationsDataStore == null) {
            _NotificationsDataStore = new NotificationsDataStore();
        }
        return _NotificationsDataStore;
    }
}