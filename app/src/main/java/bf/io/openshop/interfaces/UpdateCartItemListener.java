package bf.io.openshop.interfaces;

public interface UpdateCartItemListener {

    void updateProductInCart(long productCartId, long newVariantId, int newQuantity);

}

