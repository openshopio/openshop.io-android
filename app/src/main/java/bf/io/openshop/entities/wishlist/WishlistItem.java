package bf.io.openshop.entities.wishlist;

public class WishlistItem {

    private long id;
    private WishlistProductVariant variant;

    public WishlistItem(long id, WishlistProductVariant variant) {
        this.id = id;
        this.variant = variant;
    }

    public WishlistItem() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public WishlistProductVariant getVariant() {
        return variant;
    }

    public void setVariant(WishlistProductVariant variant) {
        this.variant = variant;
    }

    @Override
    public String toString() {
        return "WishlistItem{" +
                "id=" + id +
                ", variant=" + variant +
                '}';
    }
}
