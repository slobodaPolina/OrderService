package dto;

public class PayedOrderDTO {
    private long orderId;
    private boolean payed;

    PayedOrderDTO(long orderId, boolean payed) {
        this.orderId = orderId;
        this.payed = payed;
    }

    PayedOrderDTO() {}

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public boolean isPayed() {
        return payed;
    }

    public void setPayed(boolean payed) {
        this.payed = payed;
    }
}
