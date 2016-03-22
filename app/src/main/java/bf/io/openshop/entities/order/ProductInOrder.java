package bf.io.openshop.entities.order;

import bf.io.openshop.entities.product.Product;

public class ProductInOrder {

    private long id;
    private int quantity;
    private Product product;


    public ProductInOrder() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductInOrder that = (ProductInOrder) o;

        if (id != that.id) return false;
        if (quantity != that.quantity) return false;
        return !(product != null ? !product.equals(that.product) : that.product != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + quantity;
        result = 31 * result + (product != null ? product.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductInOrder{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", product=" + product +
                '}';
    }
}
