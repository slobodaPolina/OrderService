package service;

import org.slf4j.*;

public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

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
