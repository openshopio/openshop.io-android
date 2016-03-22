package bf.io.openshop.entities.cart;

import com.google.gson.annotations.SerializedName;

import bf.io.openshop.entities.product.ProductColor;
import bf.io.openshop.entities.product.ProductSize;

public class CartProductItemVariant {

    private long id;

    @SerializedName("remote_id")
    private long remoteId;

    @SerializedName("product_id")
    private long productId;
    private String url;
    private String name;
    private double price;

    @SerializedName("price_formatted")
    private String priceFormatted;
    private long category;

    @SerializedName("discount_price")
    private double discountPrice;

    @SerializedName("discount_price_formatted")
    private String discountPriceFormatted;
    private String currency;
    private String code;
    private String description;

    @SerializedName("main_image")
    private String mainImage;
    private ProductColor color;
    private ProductSize size;

    public CartProductItemVariant() {
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

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPriceFormatted() {
        return priceFormatted;
    }

    public void setPriceFormatted(String priceFormatted) {
        this.priceFormatted = priceFormatted;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getDiscountPriceFormatted() {
        return discountPriceFormatted;
    }

    public void setDiscountPriceFormatted(String discountPriceFormatted) {
        this.discountPriceFormatted = discountPriceFormatted;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartProductItemVariant that = (CartProductItemVariant) o;

        if (id != that.id) return false;
        if (remoteId != that.remoteId) return false;
        if (productId != that.productId) return false;
        if (Double.compare(that.price, price) != 0) return false;
        if (category != that.category) return false;
        if (Double.compare(that.discountPrice, discountPrice) != 0) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (priceFormatted != null ? !priceFormatted.equals(that.priceFormatted) : that.priceFormatted != null)
            return false;
        if (discountPriceFormatted != null ? !discountPriceFormatted.equals(that.discountPriceFormatted) : that.discountPriceFormatted != null)
            return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null)
            return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (mainImage != null ? !mainImage.equals(that.mainImage) : that.mainImage != null)
            return false;
        if (color != null ? !color.equals(that.color) : that.color != null) return false;
        return !(size != null ? !size.equals(that.size) : that.size != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (remoteId ^ (remoteId >>> 32));
        result = 31 * result + (int) (productId ^ (productId >>> 32));
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (priceFormatted != null ? priceFormatted.hashCode() : 0);
        result = 31 * result + (int) (category ^ (category >>> 32));
        temp = Double.doubleToLongBits(discountPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (discountPriceFormatted != null ? discountPriceFormatted.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (mainImage != null ? mainImage.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CartProductItemVariant{" +
                "id=" + id +
                ", remoteId=" + remoteId +
                ", productId=" + productId +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", priceFormatted='" + priceFormatted + '\'' +
                ", category=" + category +
                ", discountPrice=" + discountPrice +
                ", discountPriceFormatted='" + discountPriceFormatted + '\'' +
                ", currency='" + currency + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", mainImage='" + mainImage + '\'' +
                ", color=" + color +
                ", size=" + size +
                '}';
    }
}
