package bf.io.openshop.entities.delivery;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Shipping {

    private long id;
    private String name;
    private int price;

    @SerializedName("price_formatted")
    private String priceFormatted;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("total_price_formatted")
    private String totalPriceFormatted;
    private String currency;

    @SerializedName("min_cart_amount")
    private int minCartAmount;
    private List<Payment> payment;
    private Branch branch;
    private String description;
    private String availabilityTime;
    private String availabilityDate;

    public Shipping() {
    }

    public Shipping(String name) {
        this.name = name;
    }

    public Shipping(String name, String availabilityTime, String availabilityDate, String description) {
        this.name = name;
        this.availabilityTime = availabilityTime;
        this.availabilityDate = availabilityDate;
        this.description = description;
    }

    public Shipping(long id, String name, int price, String currency, int minCartAmount) {
        super();
        this.id = id;
        this.name = name;
        this.price = price;
        this.currency = currency;
        this.minCartAmount = minCartAmount;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPriceFormatted() {
        return priceFormatted;
    }

    public void setPriceFormatted(String priceFormatted) {
        this.priceFormatted = priceFormatted;
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

    public int getMinCartAmount() {
        return minCartAmount;
    }

    public void setMinCartAmount(int minCartAmount) {
        this.minCartAmount = minCartAmount;
    }

    public List<Payment> getPayment() {
        return payment;
    }

    public void setPayment(List<Payment> payment) {
        this.payment = payment;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvailabilityTime() {
        return availabilityTime;
    }

    public void setAvailabilityTime(String availabilityTime) {
        this.availabilityTime = availabilityTime;
    }

    public String getAvailabilityDate() {
        return availabilityDate;
    }

    public void setAvailabilityDate(String availabilityDate) {
        this.availabilityDate = availabilityDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shipping shipping = (Shipping) o;

        if (id != shipping.id) return false;
        if (price != shipping.price) return false;
        if (Double.compare(shipping.totalPrice, totalPrice) != 0) return false;
        if (minCartAmount != shipping.minCartAmount) return false;
        if (name != null ? !name.equals(shipping.name) : shipping.name != null) return false;
        if (priceFormatted != null ? !priceFormatted.equals(shipping.priceFormatted) : shipping.priceFormatted != null)
            return false;
        if (totalPriceFormatted != null ? !totalPriceFormatted.equals(shipping.totalPriceFormatted) : shipping.totalPriceFormatted != null)
            return false;
        if (currency != null ? !currency.equals(shipping.currency) : shipping.currency != null)
            return false;
        if (payment != null ? !payment.equals(shipping.payment) : shipping.payment != null)
            return false;
        if (branch != null ? !branch.equals(shipping.branch) : shipping.branch != null)
            return false;
        if (description != null ? !description.equals(shipping.description) : shipping.description != null)
            return false;
        if (availabilityTime != null ? !availabilityTime.equals(shipping.availabilityTime) : shipping.availabilityTime != null)
            return false;
        return !(availabilityDate != null ? !availabilityDate.equals(shipping.availabilityDate) : shipping.availabilityDate != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + price;
        result = 31 * result + (priceFormatted != null ? priceFormatted.hashCode() : 0);
        temp = Double.doubleToLongBits(totalPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (totalPriceFormatted != null ? totalPriceFormatted.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + minCartAmount;
        result = 31 * result + (payment != null ? payment.hashCode() : 0);
        result = 31 * result + (branch != null ? branch.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (availabilityTime != null ? availabilityTime.hashCode() : 0);
        result = 31 * result + (availabilityDate != null ? availabilityDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
