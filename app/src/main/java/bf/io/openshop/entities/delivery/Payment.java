package bf.io.openshop.entities.delivery;

import com.google.gson.annotations.SerializedName;

public class Payment {

    private long id;
    private String name;
    private String description;
    private double price;

    @SerializedName("price_formatted")
    private String priceFormatted;
    private String currency;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("total_price_formatted")
    private String totalPriceFormatted;

    public Payment() {
    }

    public Payment(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPriceFormatted() {
        return priceFormatted;
    }

    public void setPriceFormatted(String priceFormatted) {
        this.priceFormatted = priceFormatted;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

        Payment payment = (Payment) o;

        if (id != payment.id) return false;
        if (Double.compare(payment.price, price) != 0) return false;
        if (Double.compare(payment.totalPrice, totalPrice) != 0) return false;
        if (name != null ? !name.equals(payment.name) : payment.name != null) return false;
        if (description != null ? !description.equals(payment.description) : payment.description != null)
            return false;
        if (priceFormatted != null ? !priceFormatted.equals(payment.priceFormatted) : payment.priceFormatted != null)
            return false;
        if (currency != null ? !currency.equals(payment.currency) : payment.currency != null)
            return false;
        return !(totalPriceFormatted != null ? !totalPriceFormatted.equals(payment.totalPriceFormatted) : payment.totalPriceFormatted != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (priceFormatted != null ? priceFormatted.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        temp = Double.doubleToLongBits(totalPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (totalPriceFormatted != null ? totalPriceFormatted.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
