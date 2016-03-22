package bf.io.openshop.entities.delivery;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Delivery {

    @SerializedName("personal_pickup")
    private List<Shipping> personalPickup;
    private List<Shipping> shipping;

    public Delivery() {
    }

    public List<Shipping> getPersonalPickup() {
        return personalPickup;
    }

    public void setPersonalPickup(List<Shipping> personalPickup) {
        this.personalPickup = personalPickup;
    }

    public List<Shipping> getShipping() {
        return shipping;
    }

    public void setShipping(List<Shipping> shipping) {
        this.shipping = shipping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Delivery delivery = (Delivery) o;

        if (personalPickup != null ? !personalPickup.equals(delivery.personalPickup) : delivery.personalPickup != null)
            return false;
        return !(shipping != null ? !shipping.equals(delivery.shipping) : delivery.shipping != null);

    }

    @Override
    public int hashCode() {
        int result = personalPickup != null ? personalPickup.hashCode() : 0;
        result = 31 * result + (shipping != null ? shipping.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "personalPickup=" + personalPickup +
                ", shipping=" + shipping +
                '}';
    }
}