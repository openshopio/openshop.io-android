package bf.io.openshop.ux.adapters;

import android.content.Context;
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
import bf.io.openshop.entities.cart.Cart;
import bf.io.openshop.entities.cart.CartDiscountItem;
import bf.io.openshop.entities.cart.CartProductItem;
import bf.io.openshop.interfaces.CartRecyclerInterface;
import bf.io.openshop.listeners.OnSingleClickListener;
import timber.log.Timber;

/**
 * Adapter handling list of cart items.
 */
public class CartRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM_PRODUCT = 0;
    private static final int TYPE_ITEM_DISCOUNT = 1;
    private static final int TYPE_FOOTER = 2;
    private final List<CartProductItem> cartProductItems = new ArrayList<>();
    private final List<CartDiscountItem> cartDiscountItems = new ArrayList<>();
    private final CartRecyclerInterface cartRecyclerInterface;
    private final Context context;
    LayoutInflater layoutInflater;
    private String cartTotalPriceFormatted;
    private int cartQuantity = 0;

    /**
     * Creates an adapter that handles a list of cart items.
     *
     * @param context               activity context.
     * @param cartRecyclerInterface listener indicating events that occurred.
     */
    public CartRecyclerAdapter(Context context, CartRecyclerInterface cartRecyclerInterface) {
        this.context = context;
        this.cartRecyclerInterface = cartRecyclerInterface;
    }

    @Override
    public int getItemCount() {
        return cartProductItems.size() + cartDiscountItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_FOOTER;
        } else if (position <= cartDiscountItems.size()) {
            return TYPE_ITEM_DISCOUNT;
        } else
            return TYPE_ITEM_PRODUCT;
    }

    private CartDiscountItem getCartDiscountItem(int position) {
        return cartDiscountItems.get(position - 1);
    }

    private CartProductItem getCartProductItem(int position) {
        return cartProductItems.get(position - 1 - cartDiscountItems.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_ITEM_DISCOUNT) {
            View view = layoutInflater.inflate(R.layout.list_item_cart_discount, parent, false);
            return new ViewHolderDiscount(view, cartRecyclerInterface);
        } else if (viewType == TYPE_ITEM_PRODUCT) {
            View view = layoutInflater.inflate(R.layout.list_item_cart_product, parent, false);
            return new ViewHolderProduct(view, cartRecyclerInterface);
        } else {
            View view = layoutInflater.inflate(R.layout.list_item_cart_footer, parent, false);
            return new ViewHolderFooter(view, cartRecyclerInterface);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        Timber.d("Bind position: " + position);
        if (holder instanceof ViewHolderProduct) {
            ViewHolderProduct viewHolderProduct = (ViewHolderProduct) holder;

            CartProductItem cartProductItem = getCartProductItem(position);
            viewHolderProduct.bindContent(cartProductItem);

            viewHolderProduct.cartProductName.setText(cartProductItem.getVariant().getName());
            viewHolderProduct.cartProductPrice.setText(cartProductItem.getTotalItemPriceFormatted());
            viewHolderProduct.cartProductDetails.setText(context.getString(
                    R.string.format_string_division, cartProductItem.getVariant().getColor().getValue(),
                    cartProductItem.getVariant().getSize().getValue()));
            viewHolderProduct.cartProductQuantity.setText(context.getString(R.string.format_quantity,
                    cartProductItem.getQuantity()));

            Picasso.with(context).load(cartProductItem.getVariant().getMainImage())
                    .fit().centerInside()
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(viewHolderProduct.cartProductImage);

        } else if (holder instanceof ViewHolderDiscount) {
            ViewHolderDiscount viewHolderDiscount = (ViewHolderDiscount) holder;

            CartDiscountItem cartDiscountItem = getCartDiscountItem(position);
            viewHolderDiscount.bindContent(cartDiscountItem);
            viewHolderDiscount.cartDiscountName.setText(cartDiscountItem.getDiscount().getName());
            viewHolderDiscount.cartDiscountValue.setText(cartDiscountItem.getDiscount().getValueFormatted());
        } else if (holder instanceof ViewHolderFooter) {
            ViewHolderFooter viewHolderFooter = (ViewHolderFooter) holder;

            viewHolderFooter.cartQuantity.setText(context.getString(R.string.format_item_quantity, cartQuantity));
            viewHolderFooter.cartTotalPrice.setText(cartTotalPriceFormatted);
        } else {
            Timber.e(new RuntimeException(), "Unknown ViewHolder in class: " + CartRecyclerAdapter.class.getSimpleName());
        }
    }

    public void refreshItems(Cart cart) {
        if (cart != null) {
            cartProductItems.clear();
            cartDiscountItems.clear();
            cartProductItems.addAll(cart.getItems());
            cartDiscountItems.addAll(cart.getDiscounts());
            cartTotalPriceFormatted = cart.getTotalPriceFormatted();
            cartQuantity = cart.getProductCount();
            notifyDataSetChanged();
        } else {
            Timber.e("Setting cart content with null cart");
        }
    }

    public void cleatCart() {
        cartProductItems.clear();
        cartDiscountItems.clear();
        notifyDataSetChanged();
    }


    // Provide a reference to the views for each data item
    public static class ViewHolderDiscount extends RecyclerView.ViewHolder {

        public TextView cartDiscountName;
        public TextView cartDiscountValue;
        private CartDiscountItem cartDiscountItem;

        public ViewHolderDiscount(View itemView, final CartRecyclerInterface cartRecyclerInterface) {
            super(itemView);
            cartDiscountName = (TextView) itemView.findViewById(R.id.cart_discount_name);
            cartDiscountValue = (TextView) itemView.findViewById(R.id.cart_discount_value);
            View deleteDiscount = itemView.findViewById(R.id.cart_discount_delete);
            deleteDiscount.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    cartRecyclerInterface.onDiscountDelete(cartDiscountItem);
                }
            });
        }

        public void bindContent(CartDiscountItem cartDiscountItem) {
            this.cartDiscountItem = cartDiscountItem;
        }
    }

    // Provide a reference to the views for each data item
    public static class ViewHolderProduct extends RecyclerView.ViewHolder {

        ImageView cartProductImage;
        TextView cartProductQuantity;
        TextView cartProductName;
        TextView cartProductPrice;
        TextView cartProductDetails;
        CartProductItem cartProductItem;

        public ViewHolderProduct(View itemView, final CartRecyclerInterface cartRecyclerInterface) {
            super(itemView);
            cartProductImage = (ImageView) itemView.findViewById(R.id.cart_product_image);
            cartProductQuantity = (TextView) itemView.findViewById(R.id.cart_product_quantity);
            cartProductName = (TextView) itemView.findViewById(R.id.cart_product_name);
            cartProductPrice = (TextView) itemView.findViewById(R.id.cart_product_price);
            cartProductDetails = (TextView) itemView.findViewById(R.id.cart_product_details);

            View deleteProduct = itemView.findViewById(R.id.cart_product_delete);
            deleteProduct.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    cartRecyclerInterface.onProductDelete(cartProductItem);
                }
            });
            View updateProduct = itemView.findViewById(R.id.cart_product_update);
            updateProduct.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    cartRecyclerInterface.onProductUpdate(cartProductItem);
                }
            });
            itemView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    cartRecyclerInterface.onProductSelect(cartProductItem.getVariant().getProductId());
                }
            });
        }

        public void bindContent(CartProductItem cartProductItem) {
            this.cartProductItem = cartProductItem;
        }
    }

    public static class ViewHolderFooter extends RecyclerView.ViewHolder {

        public TextView discountAction;
        public TextView cartQuantity;
        public TextView cartTotalPrice;

        public ViewHolderFooter(View headerView, final CartRecyclerInterface cartRecyclerInterface) {
            super(headerView);
            discountAction = (TextView) headerView.findViewById(R.id.list_item_cart_footer_action);
            cartQuantity = (TextView) headerView.findViewById(R.id.list_item_cart_footer_quantity);
            cartTotalPrice = (TextView) headerView.findViewById(R.id.list_item_cart_footer_price);

            discountAction.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    cartRecyclerInterface.onDiscountAdd();
                }
            });
        }
    }
}
