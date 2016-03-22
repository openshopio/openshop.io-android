package bf.io.openshop.entities.cart;

import com.google.gson.annotations.SerializedName;

public class CartProductItem {

    private long id;

    @SerializedName("remote_id")
    private long remoteId;
    private int quantity;

    @SerializedName("total_price")
    private double totalItemPrice;

    @SerializedName("total_item_price_formatted")
    private String totalItemPriceFormatted;
    private CartProductItemVariant variant;
    private int expiration = 0;

    public CartProductItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalItemPrice() {
        return totalItemPrice;
    }

    public void setTotalItemPrice(double totalItemPrice) {
        this.totalItemPrice = totalItemPrice;
    }

    public String getTotalItemPriceFormatted() {
        return totalItemPriceFormatted;
    }

    public void setTotalItemPriceFormatted(String totalItemPriceFormatted) {
        this.totalItemPriceFormatted = totalItemPriceFormatted;
    }

    public CartProductItemVariant getVariant() {
        return variant;
    }

    public void setVariant(CartProductItemVariant variant) {
        this.variant = variant;
    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartProductItem that = (CartProductItem) o;

        if (id != that.id) return false;
        if (remoteId != that.remoteId) return false;
        if (quantity != that.quantity) return false;
        if (Double.compare(that.totalItemPrice, totalItemPrice) != 0) return false;
        if (expiration != that.expiration) return false;
        if (totalItemPriceFormatted != null ? !totalItemPriceFormatted.equals(that.totalItemPriceFormatted) : that.totalItemPriceFormatted != null)
            return false;
        return !(variant != null ? !variant.equals(that.variant) : that.variant != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (remoteId ^ (remoteId >>> 32));
        result = 31 * result + quantity;
        temp = Double.doubleToLongBits(totalItemPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (totalItemPriceFormatted != null ? totalItemPriceFormatted.hashCode() : 0);
        result = 31 * result + (variant != null ? variant.hashCode() : 0);
        result = 31 * result + expiration;
        return result;
    }

    @Override
    public String toString() {
        return "CartProductItem{" +
                "id=" + id +
                ", remoteId=" + remoteId +
                ", quantity=" + quantity +
                ", totalItemPrice=" + totalItemPrice +
                ", totalItemPriceFormatted='" + totalItemPriceFormatted + '\'' +
                ", variant=" + variant +
                ", expiration=" + expiration +
                '}';
    }
}
