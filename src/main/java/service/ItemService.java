package service;

public class ItemService {

    public boolean reserveItems(long itemId, long amount) {
        //call real ItemService here, if it is ok...
        System.out.println("ORDER SERVICE INFO: Reserved " + amount + " items with id " + itemId);
        return true;
    }

    public boolean releaseItems(long itemId, long amount) {
        //call real ItemService here
        System.out.println("ORDER SERVICE INFO: Released " + amount + " items with id " + itemId);
        return true;
    }
}
