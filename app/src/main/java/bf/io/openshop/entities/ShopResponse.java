package bf.io.openshop.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopResponse {

    Metadata metadata;

    @SerializedName("records")
    List<Shop> shopList;

    public ShopResponse() {
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<Shop> getShopList() {
        return shopList;
    }

    public void setShopList(List<Shop> shopList) {
        this.shopList = shopList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShopResponse)) return false;

        ShopResponse that = (ShopResponse) o;

        if (getMetadata() != null ? !getMetadata().equals(that.getMetadata()) : that.getMetadata() != null)
            return false;
        return !(getShopList() != null ? !getShopList().equals(that.getShopList()) : that.getShopList() != null);

    }

    @Override
    public int hashCode() {
        int result = getMetadata() != null ? getMetadata().hashCode() : 0;
        result = 31 * result + (getShopList() != null ? getShopList().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ShopResponse{" +
                "metadata=" + metadata +
                ", shopList=" + shopList +
                '}';
    }
}
