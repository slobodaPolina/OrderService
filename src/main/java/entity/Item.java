package entity;

public class Item {
    private long id;
    /*
    it will be null until we get it from ItemService
     */
    private String name;
    /*
    it will be null until we get it from ItemService
     */
    private Long price;
    /*
    here it is amount of elements IN ORDER (NOT IN WAREHOUSE)
     */
    private long amount;
}
