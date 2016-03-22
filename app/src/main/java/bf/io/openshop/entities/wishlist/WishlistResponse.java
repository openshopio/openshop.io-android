package bf.io.openshop.entities.wishlist;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WishlistResponse {

    private long id;
    @SerializedName("product_count")
    private int productCount;
    private List<WishlistItem> items;

    public WishlistResponse() {
    }


    public WishlistResponse(long id, int productCount, List<WishlistItem> items) {
        this.id = id;
        this.productCount = productCount;
        this.items = items;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public List<WishlistItem> getItems() {
        return items;
    }

    public void setItems(List<WishlistItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WishlistResponse that = (WishlistResponse) o;

        if (id != that.id) return false;
        if (productCount != that.productCount) return false;
        return !(items != null ? !items.equals(that.items) : that.items != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + productCount;
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WishlistResponse{" +
                "id=" + id +
                ", productCount=" + productCount +
                ", items=" + items +
                '}';
    }
}
