package Models;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class SDMLocation {
    public SDMLocation(){

    }
    public SDMLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
    @Expose
    public int x;
    @Expose
    public int y;

    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "]";
    }
}
