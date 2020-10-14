package Models;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    public Customer(int serialNumber, String name, SDMLocation location){
        this.serialNumber = serialNumber;
        this.name = name;
        this.location = location;
    }

    public int serialNumber;
    public String name;
    public SDMLocation location;
    public List<Integer> OrderIDs = new ArrayList<>();

    @Override
    public String toString() {
        return "Serial Number: " + serialNumber + ", Name: " + name ;
    }
}
