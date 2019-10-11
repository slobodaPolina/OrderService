package service;

import org.slf4j.Logger;

public class ItemService {
    private Logger logger;
    public ItemService(Logger logger) {
        this.logger = logger;
    }

    boolean reserveItems(long itemId, long amount) {
        //call real ItemService here, if it is ok...
        logger.info("Reserved " + amount + " items with id " + itemId);
        return true;
    }

    boolean releaseItems(long itemId, long amount) {
        //call real ItemService here
       logger.info("Released " + amount + " items with id " + itemId);
        return true;
    }
}
