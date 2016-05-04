package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.product.Product;
import bf.io.openshop.interfaces.RelatedProductsRecyclerInterface;
import bf.io.openshop.views.ResizableImageViewHeight;
import timber.log.Timber;

/**
 * Adapter handling list of related products.
 */
public class RelatedProductsRecyclerAdapter extends RecyclerView.Adapter<RelatedProductsRecyclerAdapter.ViewHolder> {

    private final Context context;
    private final RelatedProductsRecyclerInterface relatedProductsRecyclerInterface;
    private List<Product> relatedProducts;
    private LayoutInflater layoutInflater;

    /**
     * Creates an adapter that handles a list of related products.
     *
     * @param context                          activity context.
     * @param relatedProductsRecyclerInterface listener indicating events that occurred.
     */
    public RelatedProductsRecyclerAdapter(Context context, RelatedProductsRecyclerInterface relatedProductsRecyclerInterface) {
        this.context = context;
        this.relatedProductsRecyclerInterface = relatedProductsRecyclerInterface;
        relatedProducts = new ArrayList<>();
    }

    @Override
    public RelatedProductsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.list_item_recommended_products, parent, false);
        return new ViewHolder(view, relatedProductsRecyclerInterface);
    }

    @Override
    public void onBindViewHolder(RelatedProductsRecyclerAdapter.ViewHolder holder, int position) {
//        Timber.e("RecommendedProduct position: " + position);

        Product product = getItem(position);
        if (product != null) {
            holder.setPosition(position);
            holder.setProduct(product);

            holder.productName.setText(product.getName());

            // Determine if product is on sale
            double pr = holder.product.getPrice();
            double dis = holder.product.getDiscountPrice();
            if (pr == dis || Math.abs(pr - dis) / Math.max(Math.abs(pr), Math.abs(dis)) < 0.000001) {
                holder.productPrice.setVisibility(View.VISIBLE);
                holder.productDiscount.setVisibility(View.GONE);
                holder.productPrice.setText(holder.product.getPriceFormatted());
                holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.productPrice.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
            } else {
                holder.productPrice.setVisibility(View.VISIBLE);
                holder.productDiscount.setVisibility(View.VISIBLE);
                holder.productPrice.setText(holder.product.getPriceFormatted());
                holder.productPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                holder.productPrice.setTextColor(ContextCompat.getColor(context, R.color.textSecondary));
                holder.productDiscount.setText(holder.product.getDiscountPriceFormatted());
            }

            Picasso.with(context)
                    .load(product.getMainImage())
                    .fit().centerInside()
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(holder.productImage);
        } else {
            Timber.e("%s tried setting null product!", this.getClass().getSimpleName());
        }
    }

    private Product getItem(int position) {
        return relatedProducts.get(position);
    }

    @Override
    public int getItemCount() {
        return relatedProducts.size();
    }

    /**
     * Add the product to the list.
     *
     * @param position list position where item should be added.
     * @param product  item to add.
     */
    public void add(int position, Product product) {
        relatedProducts.add(position, product);
        notifyItemInserted(position);
    }

    /**
     * Add the product at the end of the list.
     *
     * @param product item to add.
     */
    public void addLast(Product product) {
        relatedProducts.add(relatedProducts.size(), product);
        notifyItemInserted(relatedProducts.size());
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        int position;
        Product product;

        ResizableImageViewHeight productImage;
        TextView productName;
        TextView productPrice;
        TextView productDiscount;


        public ViewHolder(View v, final RelatedProductsRecyclerInterface relatedProductsRecyclerInterface) {
            super(v);
            productImage = (ResizableImageViewHeight) v.findViewById(R.id.list_item_recommended_products_image);
            productName = (TextView) v.findViewById(R.id.list_item_recommended_products_name);
            productPrice = (TextView) v.findViewById(R.id.list_item_recommended_products_price);
            productDiscount = (TextView) v.findViewById(R.id.list_item_recommended_products_discount);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (relatedProductsRecyclerInterface != null)
                        relatedProductsRecyclerInterface.onRelatedProductSelected(v, position, product);
                }
            });
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

    }
}
