package Models;

public class Notification {
    public Notification(String message,String username) {
        this.Message = message;
        this.UserName = username;
        this.sent = false;
    }

    public int Id;
    public String Message;
    public String UserName;
    public boolean sent;
}
