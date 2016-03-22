package bf.io.openshop.interfaces;

import android.view.View;

import bf.io.openshop.entities.product.Product;

public interface RelatedProductsRecyclerInterface {

    void onRelatedProductSelected(View v, int position, Product product);
}
