package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bf.io.openshop.R;
import bf.io.openshop.entities.order.Order;
import bf.io.openshop.utils.Utils;
import bf.io.openshop.views.ResizableImageView;
import timber.log.Timber;

/**
 * Adapter handling list of order items.
 */
public class OrderRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM_ORDER = 1;

    private LayoutInflater layoutInflater;
    private Context context;
    private Order order;

    /**
     * Creates an adapter that handles a list of order items.
     *
     * @param context activity context.
     */
    public OrderRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_ITEM_ORDER) {
            View view = layoutInflater.inflate(R.layout.list_item_order_product_image, parent, false);
            return new ViewHolderOrderProduct(view);
        } else {
            View view = layoutInflater.inflate(R.layout.list_item_order_header, parent, false);
            return new ViewHolderHeader(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderOrderProduct) {
            ViewHolderOrderProduct viewHolderOrderProduct = (ViewHolderOrderProduct) holder;

            Picasso.with(context).load(order.getProducts().get(position - 1).getVariant().getMainImage())
                    .fit().centerInside()
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(viewHolderOrderProduct.productImage);

        } else if (holder instanceof ViewHolderHeader) {
            ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;

            viewHolderHeader.orderId.setText(order.getRemoteId());
            viewHolderHeader.orderName.setText(order.getName());
            viewHolderHeader.orderDateCreated.setText(Utils.parseDate(order.getDateCreated()));
            viewHolderHeader.orderTotal.setText(order.getTotalFormatted());
            viewHolderHeader.orderShippingMethod.setText(order.getShippingName());
            viewHolderHeader.orderShippingPrice.setText(order.getShippingPriceFormatted());
        } else {
            Timber.e(new RuntimeException(), "Unknown holder type.");
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        if (order != null) {
            if (order.getProducts() != null && order.getProducts().size() > 0) {
                return order.getProducts().size() + 1; // the number of items in the list, +1 for header view.
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else
            return TYPE_ITEM_ORDER;
    }


    /**
     * Add item to list, and notify dataSet changed.
     *
     * @param order item to add.
     */
    public void addOrder(Order order) {
        if (order != null) {
            this.order = order;
            notifyDataSetChanged();
        } else {
            Timber.e("Setting null order object.");
        }
    }

    // Provide a reference to the views for each data item
    public static class ViewHolderOrderProduct extends RecyclerView.ViewHolder {
        ResizableImageView productImage;

        public ViewHolderOrderProduct(View itemView) {
            super(itemView);
            productImage = (ResizableImageView) itemView.findViewById(R.id.list_item_product_images_view);
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {

        public TextView orderId;
        public TextView orderName;
        public TextView orderDateCreated;
        public TextView orderTotal;
        public TextView orderShippingMethod;
        public TextView orderShippingPrice;

        public ViewHolderHeader(View headerView) {
            super(headerView);
            orderId = (TextView) headerView.findViewById(R.id.list_item_order_header_id);
            orderName = (TextView) headerView.findViewById(R.id.list_item_order_header_name);
            orderDateCreated = (TextView) headerView.findViewById(R.id.list_item_order_header_dateCreated);
            orderTotal = (TextView) headerView.findViewById(R.id.list_item_order_header_total);
            orderShippingMethod = (TextView) headerView.findViewById(R.id.list_item_order_header_shipping_method);
            orderShippingPrice = (TextView) headerView.findViewById(R.id.list_item_order_header_shipping_price);
        }
    }
}