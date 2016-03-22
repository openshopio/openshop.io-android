package bf.io.openshop.entities.cart;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cart {

    private long id;

    @SerializedName("product_count")
    private int productCount;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("total_price_formatted")
    private String totalPriceFormatted;
    private String currency;
    private List<CartProductItem> items;
    private List<CartDiscountItem> discounts;

    public Cart() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalPriceFormatted() {
        return totalPriceFormatted;
    }

    public void setTotalPriceFormatted(String totalPriceFormatted) {
        this.totalPriceFormatted = totalPriceFormatted;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<CartProductItem> getItems() {
        return items;
    }

    public void setItems(List<CartProductItem> items) {
        this.items = items;
    }

    public List<CartDiscountItem> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<CartDiscountItem> discounts) {
        this.discounts = discounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cart cart = (Cart) o;

        if (id != cart.id) return false;
        if (productCount != cart.productCount) return false;
        if (Double.compare(cart.totalPrice, totalPrice) != 0) return false;
        if (totalPriceFormatted != null ? !totalPriceFormatted.equals(cart.totalPriceFormatted) : cart.totalPriceFormatted != null)
            return false;
        if (currency != null ? !currency.equals(cart.currency) : cart.currency != null)
            return false;
        if (items != null ? !items.equals(cart.items) : cart.items != null) return false;
        return !(discounts != null ? !discounts.equals(cart.discounts) : cart.discounts != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + productCount;
        temp = Double.doubleToLongBits(totalPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (totalPriceFormatted != null ? totalPriceFormatted.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        result = 31 * result + (discounts != null ? discounts.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", productCount=" + productCount +
                ", totalPrice=" + totalPrice +
                ", totalPriceFormatted='" + totalPriceFormatted + '\'' +
                ", currency='" + currency + '\'' +
                ", items=" + items +
                ", discounts=" + discounts +
                '}';
    }
}
