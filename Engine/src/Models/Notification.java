package Models;

public class Notification {
    public Notification(int itemID, String itemType,String username) {
        this.ItemID = itemID;
        this.ItemType = itemType;
        this.UserName = username;
        this.sent = false;
    }

    public int Id;
    public int ItemID;
    public String ItemType;
    public String  UserName;
    public boolean sent;
}
