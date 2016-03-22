package bf.io.openshop.entities.cart;

import com.google.gson.annotations.SerializedName;

public class CartInfo {

    @SerializedName("product_count")
    private int productCount;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("total_price_formatted")
    private String totalPriceFormatted;

    public CartInfo() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartInfo cartInfo = (CartInfo) o;

        if (productCount != cartInfo.productCount) return false;
        if (Double.compare(cartInfo.totalPrice, totalPrice) != 0) return false;
        return !(totalPriceFormatted != null ? !totalPriceFormatted.equals(cartInfo.totalPriceFormatted) : cartInfo.totalPriceFormatted != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = productCount;
        temp = Double.doubleToLongBits(totalPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (totalPriceFormatted != null ? totalPriceFormatted.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CartInfo{" +
                "productCount=" + productCount +
                ", totalPrice=" + totalPrice +
                ", totalPriceFormatted='" + totalPriceFormatted + '\'' +
                '}';
    }
}
