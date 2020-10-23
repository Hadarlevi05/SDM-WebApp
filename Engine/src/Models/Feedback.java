package Models;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class Feedback {
    @Expose
    public  int rate;
    @Expose
    public String message;
    @Expose
    public String username;
    @Expose
    public Date date;

    public  Feedback( int rate, String message,String username,Date date){
        this.rate = rate;
        this.message = message;
        this.username = username;
        this.date = date;

    }
}
