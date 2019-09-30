package service;

public class ItemService {
    public boolean releaseItems(long itemId, long amount) {
        //call real ItemService here
        System.out.println("ORDER SERVICE INFO: Released " + amount + " items with id " + itemId);
        return true;
    }
}
