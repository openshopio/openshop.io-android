package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.wishlist.WishlistItem;
import bf.io.openshop.interfaces.WishlistInterface;
import bf.io.openshop.views.ResizableImageView;
import timber.log.Timber;

/**
 * Adapter handling list of wishlist items.
 */
public class WishListRecyclerAdapter extends RecyclerView.Adapter<WishListRecyclerAdapter.ViewHolder> {

    private final List<WishlistItem> wishlistItems;
    private final Context context;
    private final WishlistInterface wishlistInterface;

    /**
     * Creates an adapter that handles a list of wishlist items.
     *
     * @param context           activity context.
     * @param wishlistInterface listener indicating events that occurred.
     */
    public WishListRecyclerAdapter(Context context, WishlistInterface wishlistInterface) {
        this.wishlistItems = new ArrayList<>();
        this.context = context;
        this.wishlistInterface = wishlistInterface;
    }

    private WishlistItem getItem(int position) {
        return wishlistItems.get(position);
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WishListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_wishlist, parent, false);
        // set the view's size, margins, padding and layout parameters
        return new ViewHolder(v, new ViewHolder.ViewHolderClickListener() {
            @Override
            public void onProductSelected(final View caller, final WishlistItem wishlistItem) {
                Timber.d("Product click: %s", wishlistItem.getVariant().getName());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        wishlistInterface.onWishlistItemSelected(caller, wishlistItem);
                    }
                }, 200);
            }

            @Override
            public void onRemoveProductFromWishList(final View caller, final WishlistItem product, int adapterPosition) {
                wishlistInterface.onRemoveItemFromWishList(caller, product, adapterPosition);
            }
        });
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your data set at this position
        WishlistItem wishlistItem = getItem(position);
        if (wishlistItem.getVariant() != null) {
            holder.bindContent(wishlistItem);
            // - replace the contents of the view with that element
            holder.tvProductName.setText(holder.wishlistItem.getVariant().getName());

            Picasso.with(context).load(holder.wishlistItem.getVariant().getMainImage())
                    .fit().centerInside()
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(holder.ivThumb);

            // Determine if product is on sale
            double pr = holder.wishlistItem.getVariant().getPrice();
            double dis = holder.wishlistItem.getVariant().getDiscountPrice();
            if (pr == dis || Math.abs(pr - dis) / Math.max(Math.abs(pr), Math.abs(dis)) < 0.000001) {
                holder.tvProductPrice.setVisibility(View.VISIBLE);
                holder.tvProductPriceDiscount.setVisibility(View.GONE);
                holder.tvProductPrice.setText(holder.wishlistItem.getVariant().getPriceFormatted());
                holder.tvProductPrice.setPaintFlags(holder.tvProductPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.tvProductPrice.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
            } else {
                holder.tvProductPrice.setVisibility(View.VISIBLE);
                holder.tvProductPriceDiscount.setVisibility(View.VISIBLE);
                holder.tvProductPrice.setText(holder.wishlistItem.getVariant().getPriceFormatted());
                holder.tvProductPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                holder.tvProductPrice.setTextColor(ContextCompat.getColor(context, R.color.textSecondary));
                holder.tvProductPriceDiscount.setText(holder.wishlistItem.getVariant().getDiscountPriceFormatted());
            }
        } else {
            Timber.e(new RuntimeException(), "Show wishlist item with null variant");
        }
    }

    /**
     * Remove item from list, and notify concrete removal.
     *
     * @param position item at position.
     */
    public void remove(int position) {
        if (wishlistItems.size() > position) {
            notifyItemRemoved(position);
            wishlistItems.remove(position);
        } else {
            Timber.e(new RuntimeException(), "Removing wishlist item at non existing position.");
        }
    }

    /**
     * Add item to list, and notify concrete addition.
     *
     * @param position     list position where item should be added.
     * @param wishlistItem item to add.
     */
    public void add(int position, WishlistItem wishlistItem) {
        notifyItemInserted(position);
        wishlistItems.add(position, wishlistItem);
    }

    /**
     * Check if adapter contains items.
     *
     * @return true if adapter is empty. False otherwise.
     */
    public boolean isEmpty() {
        return wishlistItems.isEmpty();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ResizableImageView ivThumb;
        public ImageView wishList;
        public TextView tvProductName;
        public TextView tvProductPrice;
        public TextView tvProductPriceDiscount;
        public ViewHolderClickListener viewHolderClickListenerThis;
        // each data item is just a string in this case
        private WishlistItem wishlistItem = new WishlistItem();

        public ViewHolder(View v, ViewHolderClickListener viewHolderClickListener) {
            super(v);
            this.viewHolderClickListenerThis = viewHolderClickListener;
            tvProductName = (TextView) v.findViewById(R.id.list_item_wishlist_name);
            tvProductPrice = (TextView) v.findViewById(R.id.list_item_wishlist_price);
            tvProductPriceDiscount = (TextView) v.findViewById(R.id.list_item_wishlist_discount);
            ivThumb = (ResizableImageView) v.findViewById(R.id.list_item_wishlist_image);
            wishList = (ImageView) v.findViewById(R.id.list_item_wishlist_button);
            wishList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolderClickListenerThis.onRemoveProductFromWishList(v, wishlistItem, getAdapterPosition());
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolderClickListenerThis.onProductSelected(v, wishlistItem);
                }
            });
        }

        public void bindContent(WishlistItem wishlistItem) {
            this.wishlistItem = wishlistItem;
        }

        public interface ViewHolderClickListener {
            void onProductSelected(View caller, WishlistItem product);

            void onRemoveProductFromWishList(View caller, WishlistItem product, int adapterPosition);
        }
    }
}

