package bf.io.openshop.entities.cart;

import com.google.gson.annotations.SerializedName;

public class Discount {

    private long id;
    private String name;
    private String type;
    private String value;

    @SerializedName("value_formatted")
    private String valueFormatted;

    @SerializedName("min_cart_amount")
    private String minCartAmount;

    public Discount() {
    }

    public Discount(long id, String name, String type, String value, String valueFormatted, String minCartAmount) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
        this.valueFormatted = valueFormatted;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueFormatted() {
        return valueFormatted;
    }

    public void setValueFormatted(String valueFormatted) {
        this.valueFormatted = valueFormatted;
    }

    public String getMinCartAmount() {
        return minCartAmount;
    }

    public void setMinCartAmount(String minCartAmount) {
        this.minCartAmount = minCartAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Discount discount = (Discount) o;

        if (id != discount.id) return false;
        if (name != null ? !name.equals(discount.name) : discount.name != null) return false;
        if (type != null ? !type.equals(discount.type) : discount.type != null) return false;
        if (value != null ? !value.equals(discount.value) : discount.value != null) return false;
        if (valueFormatted != null ? !valueFormatted.equals(discount.valueFormatted) : discount.valueFormatted != null)
            return false;
        return !(minCartAmount != null ? !minCartAmount.equals(discount.minCartAmount) : discount.minCartAmount != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (valueFormatted != null ? valueFormatted.hashCode() : 0);
        result = 31 * result + (minCartAmount != null ? minCartAmount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", valueFormatted='" + valueFormatted + '\'' +
                ", minCartAmount='" + minCartAmount + '\'' +
                '}';
    }
}
