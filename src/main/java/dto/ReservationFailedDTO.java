package dto;

public class ReservationFailedDTO {
    private long orderId;
    private long itemId;
    private long amount;

    ReservationFailedDTO(long orderId, long itemId, long amount) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.amount = amount;
    }

    ReservationFailedDTO() {}

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
