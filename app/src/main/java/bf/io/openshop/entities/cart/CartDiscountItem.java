package bf.io.openshop.entities.cart;

public class CartDiscountItem {

    private long id;
    private int quantity;
    private Discount discount;

    public CartDiscountItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "CartDiscountItem{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", discount=" + discount +
                '}';
    }
}
