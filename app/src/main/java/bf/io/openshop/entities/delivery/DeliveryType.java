package bf.io.openshop.entities.delivery;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DeliveryType implements Serializable {

    private long id;
    private String name;

    @SerializedName("shipping_list")
    private List<Shipping> shippingList;

    public DeliveryType(long id, String name) {
        this.id = id;
        this.name = name;
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

    public List<Shipping> getShippingList() {
        return shippingList;
    }

    public void setShippingList(List<Shipping> shippingList) {
        this.shippingList = shippingList;
    }

    @Override
    public String toString() {
        return name;
    }

}
