package Models;

import com.google.gson.annotations.Expose;

public class QuantityObject {

    public QuantityObject(int integerQuantity, double KGQuantity){
        this.integerQuantity = integerQuantity;
        this.KGQuantity = KGQuantity;

    }
    public QuantityObject(){

    }
    @Expose
    public int integerQuantity;
    @Expose
    public double KGQuantity;

}
