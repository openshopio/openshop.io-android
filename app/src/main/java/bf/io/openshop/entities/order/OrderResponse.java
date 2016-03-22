package bf.io.openshop.entities.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import bf.io.openshop.entities.Metadata;

public class OrderResponse {

    private Metadata metadata;

    @SerializedName("records")
    private List<Order> orders;

    public OrderResponse() {
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderResponse that = (OrderResponse) o;

        if (metadata != null ? !metadata.equals(that.metadata) : that.metadata != null) return false;
        return !(orders != null ? !orders.equals(that.orders) : that.orders != null);

    }

    @Override
    public int hashCode() {
        int result = metadata != null ? metadata.hashCode() : 0;
        result = 31 * result + (orders != null ? orders.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "metadata=" + metadata +
                ", orders=" + orders +
                '}';
    }
}
