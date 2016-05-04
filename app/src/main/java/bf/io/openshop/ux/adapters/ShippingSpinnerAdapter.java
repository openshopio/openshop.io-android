package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import bf.io.openshop.CONST;
import bf.io.openshop.R;
import bf.io.openshop.entities.delivery.DeliveryType;
import bf.io.openshop.entities.delivery.Shipping;
import bf.io.openshop.ux.dialogs.MapDialogFragment;
import bf.io.openshop.ux.dialogs.ShippingDialogFragment;

/**
 * BaseAdapter for shipping selection.
 */
public class ShippingSpinnerAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private Context context;
    private ShippingDialogFragment fragment;
    private List<Shipping> shippingList = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();
    private LayoutInflater layoutInflater;

    private long selectedId = CONST.DEFAULT_EMPTY_ID;

    /**
     * Creates an adapter for shipping selection.
     *
     * @param context  activity context.
     * @param fragment corresponding fragment used for a dialog creation.
     */
    public ShippingSpinnerAdapter(Context context, ShippingDialogFragment fragment) {
        this.context = context;
        this.fragment = fragment;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Set new data content.
     *
     * @param deliveryTypes List of all possible shipping types.
     */
    public void setData(List<DeliveryType> deliveryTypes) {
        shippingList.clear();
        sectionHeader.clear();

        for (DeliveryType deliveryType : deliveryTypes) {

            // Add section header
            if (deliveryType.getId() != CONST.DEFAULT_EMPTY_ID) {
                shippingList.add(new Shipping(deliveryType.getName()));
                sectionHeader.add(shippingList.size() - 1);
            }

            // Add shipping types
            for (Shipping shipping : deliveryType.getShippingList()) {
                shippingList.add(shipping);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * If in data exist equals shipping, it will be highlighted.
     *
     * @param selectedShippingType selected shipping.
     */
    public void preselectShipping(Shipping selectedShippingType) {
        if (selectedShippingType != null) {
            selectedId = selectedShippingType.getId();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return shippingList.size();
    }

    @Override
    public Shipping getItem(int position) {
        return shippingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = layoutInflater.inflate(R.layout.list_item_shipping, parent, false);
                    holder.title = (TextView) convertView.findViewById(R.id.shipping_title);
                    holder.description = (TextView) convertView.findViewById(R.id.shipping_description);
                    holder.price = (TextView) convertView.findViewById(R.id.shipping_price);
                    holder.isSelected = convertView.findViewById(R.id.shipping_is_selected);
                    holder.separator = convertView.findViewById(R.id.shipping_separator);
                    holder.shopInfo = (ImageView) convertView.findViewById(R.id.shipping_shop_info);
                    break;
                default:
                    convertView = layoutInflater.inflate(R.layout.list_item_shipping_section, parent, false);
                    holder.title = (TextView) convertView.findViewById(R.id.shipping_title);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Shipping item = shippingList.get(position);
        if (rowType == TYPE_SEPARATOR) {
            holder.title.setText(item.getName());
        } else {
            // First item without top line separator
            if (position == 0) {
                holder.separator.setVisibility(View.GONE);
            } else {
                holder.separator.setVisibility(View.VISIBLE);
            }

            // Highlight selected item
            if (selectedId != CONST.DEFAULT_EMPTY_ID && item.getId() == selectedId) {
                holder.title.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.isSelected.setVisibility(View.VISIBLE);
            } else {
                holder.title.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
                holder.isSelected.setVisibility(View.INVISIBLE);
            }

            holder.title.setText(item.getName());

            // Set price
            if (item.getPrice() == 0) {
                holder.price.setText(R.string.free);
            } else {
                holder.price.setText(context.getResources().getString(R.string.format_plus, item.getPriceFormatted()));
            }


            if (item.getBranch() != null) {
                if (item.getBranch().getName() != null && !item.getBranch().getName().isEmpty())
                    holder.title.setText(item.getBranch().getName());
                holder.description.setText(item.getBranch().getAddress());
                holder.description.setVisibility(View.VISIBLE);
                holder.shopInfo.setVisibility(View.VISIBLE);
                holder.shopInfo.setOnClickListener(new View.OnClickListener() {
                    private long mLastClickTime = 0;

                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                            return;
                        mLastClickTime = SystemClock.elapsedRealtime();

                        FragmentManager fm = fragment.getFragmentManager();
                        MapDialogFragment mapDialog = MapDialogFragment.newInstance(fragment, item, item.getBranch());
                        mapDialog.setRetainInstance(true);
                        mapDialog.show(fm, MapDialogFragment.class.getSimpleName());
                    }
                });
            } else {
                holder.shopInfo.setVisibility(View.INVISIBLE);
                if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                    holder.description.setText(item.getDescription());
                    holder.description.setVisibility(View.VISIBLE);
                } else
                    holder.description.setVisibility(View.GONE);
            }
        }
        return convertView;
    }


    public static class ViewHolder {
        public TextView title;
        public TextView description;
        public TextView price;
        public View isSelected;
        public View separator;
        public ImageView shopInfo;
    }
}
