package bf.io.openshop.entities.product;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import bf.io.openshop.entities.Metadata;

public class ProductListResponse {

    private Metadata metadata;

    @SerializedName("records")
    private List<Product> products;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductListResponse)) return false;

        ProductListResponse that = (ProductListResponse) o;

        if (getMetadata() != null ? !getMetadata().equals(that.getMetadata()) : that.getMetadata() != null) return false;
        return !(getProducts() != null ? !getProducts().equals(that.getProducts()) : that.getProducts() != null);

    }

    @Override
    public int hashCode() {
        int result = getMetadata() != null ? getMetadata().hashCode() : 0;
        result = 31 * result + (getProducts() != null ? getProducts().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductListResponse{" +
                "metadata=" + metadata +
                ", products=" + products +
                '}';
    }
}
