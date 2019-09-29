package dto;


public class ItemAdditionParametersDTO {
    private long id;
    private long amount;
    private String username;

    public ItemAdditionParametersDTO(long id, long amount, String username) {
        this.id = id;
        this.amount = amount;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
