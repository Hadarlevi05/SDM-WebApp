package Enums;

public enum  OrderType {
    STATIC,
    DYNAMIC;

    public String toString() {
        switch (this) {
            case STATIC:
                return "Static";
            case DYNAMIC:
                return "Dynamic";
            default:
                throw new IllegalArgumentException();
        }
    }
}