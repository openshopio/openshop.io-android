package bf.io.openshop.entities.product;

import java.util.Arrays;

public class ProductVariant {

    private long id;
    private ProductColor color;
    private ProductSize size;
    private String[] images;
    private String code;

    public ProductVariant() {
    }

    public ProductVariant(long id, ProductSize size) {
        this.id = id;
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProductColor getColor() {
        return color;
    }

    public void setColor(ProductColor color) {
        this.color = color;
    }

    public ProductSize getSize() {
        return size;
    }

    public void setSize(ProductSize size) {
        this.size = size;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariant)) return false;

        ProductVariant that = (ProductVariant) o;

        if (getId() != that.getId()) return false;
        if (getColor() != null ? !getColor().equals(that.getColor()) : that.getColor() != null) return false;
        if (getSize() != null ? !getSize().equals(that.getSize()) : that.getSize() != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(getImages(), that.getImages())) return false;
        return !(getCode() != null ? !getCode().equals(that.getCode()) : that.getCode() != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getColor() != null ? getColor().hashCode() : 0);
        result = 31 * result + (getSize() != null ? getSize().hashCode() : 0);
        result = 31 * result + (getImages() != null ? Arrays.hashCode(getImages()) : 0);
        result = 31 * result + (getCode() != null ? getCode().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductVariant{" +
                "id=" + id +
                ", color=" + color +
                ", size=" + size +
                ", images=" + Arrays.toString(images) +
                ", code='" + code + '\'' +
                '}';
    }
}
