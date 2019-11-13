package dto;

public class PayedOrderDTO {
    private long orderId;
    private boolean paymentSuccessful;

    PayedOrderDTO(long orderId, boolean paymentSuccessful) {
        this.orderId = orderId;
        this.paymentSuccessful = paymentSuccessful;
    }

    PayedOrderDTO() {}

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    public void setPaymentSuccessful(boolean paymentSuccessful) {
        this.paymentSuccessful = paymentSuccessful;
    }
}
