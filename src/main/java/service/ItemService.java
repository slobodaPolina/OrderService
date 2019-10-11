package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemService {

    static Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    public boolean reserveItems(long itemId, long amount) {
        //call real ItemService here, if it is ok...
        LOGGER.info("Reserved " + amount + " items with id " + itemId);
        return true;
    }

    public boolean releaseItems(long itemId, long amount) {
        //call real ItemService here
       LOGGER.info("Released " + amount + " items with id " + itemId);
        return true;
    }
}
