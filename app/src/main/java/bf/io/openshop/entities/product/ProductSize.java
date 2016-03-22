package bf.io.openshop.entities.product;

import com.google.gson.annotations.SerializedName;

public class ProductSize {

    private long id;

    @SerializedName("remote_id")
    private long remoteId;
    private String value;

    public ProductSize() {
    }

    public ProductSize(long id, long remoteId, String value) {
        this.id = id;
        this.remoteId = remoteId;
        this.value = value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductSize that = (ProductSize) o;

        if (id != that.id) return false;
        if (remoteId != that.remoteId) return false;
        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (remoteId ^ (remoteId >>> 32));
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductSize{" +
                "id=" + id +
                ", remoteId=" + remoteId +
                ", value='" + value + '\'' +
                '}';
    }
}

