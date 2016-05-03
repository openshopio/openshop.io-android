package bf.io.openshop.ux.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.Shop;

/**
 * Simple arrayAdapter for shop selection.
 */
public class ShopSpinnerAdapter extends ArrayAdapter<Shop> {
    private static final int layoutID = R.layout.list_item_shops;
    private final boolean viewTextWhite;
    private LayoutInflater layoutInflater;
    private List<Shop> shops;

    /**
     * Creates an adapter for shop selection.
     *
     * @param activity      activity context.
     * @param shops         list of items.
     * @param viewTextWhite true if text should be white.
     */
    public ShopSpinnerAdapter(Activity activity, List<Shop> shops, boolean viewTextWhite) {
        super(activity, layoutID, shops);
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.shops = shops;
        this.viewTextWhite = viewTextWhite;
    }

    public int getCount() {
        return shops.size();
    }

    public Shop getItem(int position) {
        return shops.get(position);
    }

    public long getItemId(int position) {
        return shops.get(position).getId();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, false);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent, boolean dropdown) {
//        Timber.d("getView Position: " + position + ". ConvertView: " + convertView);
        View v = convertView;
        ListItemHolder holder;

        if (v == null) {
            v = layoutInflater.inflate(layoutID, parent, false);
            holder = new ListItemHolder();
            holder.shopLanguageName = (TextView) v.findViewById(R.id.shop_language_name);
            holder.shopFlagIcon = (ImageView) v.findViewById(R.id.shop_flag_icon);
            v.setTag(holder);
        } else {
            holder = (ListItemHolder) v.getTag();
        }

        Shop shop = shops.get(position);

        if (dropdown || !viewTextWhite) {
            holder.shopLanguageName.setTextColor(ContextCompat.getColor(getContext(), R.color.textPrimary));
        } else {
            holder.shopLanguageName.setTextColor(ContextCompat.getColor(getContext(), R.color.textIconColorPrimary));
        }

        Picasso.with(getContext()).cancelRequest(holder.shopFlagIcon);
        if (shop != null) {
            holder.shopLanguageName.setText(shop.getName());
            Picasso.with(getContext()).load(shop.getFlagIcon()).into(holder.shopFlagIcon);
        }

        return v;
    }

    static class ListItemHolder {
        TextView shopLanguageName;
        ImageView shopFlagIcon;
    }
}