package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.product.ProductVariant;
import timber.log.Timber;

/**
 * Simple arrayAdapter for size selection.
 */
public class CartSizeSpinnerAdapter extends ArrayAdapter<ProductVariant> {

    private static final int layoutID = R.layout.spinner_item_simple_text;
    private final LayoutInflater layoutInflater;

    private List<ProductVariant> sizes;

    /**
     * Creates an adapter for size selection.
     *
     * @param context activity context.
     * @param sizes   list of items.
     */
    public CartSizeSpinnerAdapter(Context context, List<ProductVariant> sizes) {
        super(context, layoutID, sizes);
        this.sizes = sizes;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return sizes.size();
    }

    public ProductVariant getItem(int position) {
        return sizes.get(position);
    }

    public long getItemId(int position) {
        return sizes.get(position).getId();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ListItemHolder holder;

        if (v == null) {
            v = layoutInflater.inflate(layoutID, parent, false);
            holder = new ListItemHolder();
            holder.text = (TextView) v.findViewById(R.id.text);
            v.setTag(holder);
        } else {
            holder = (ListItemHolder) v.getTag();
        }

        if (getItem(position) != null && getItem(position).getSize() != null) {
            holder.text.setText(getItem(position).getSize().getValue());
        } else {
            Timber.e("Received null productSize in %s", this.getClass().getSimpleName());
        }

        return v;
    }

    static class ListItemHolder {
        TextView text;
    }
}
