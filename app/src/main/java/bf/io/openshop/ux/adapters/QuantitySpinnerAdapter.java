package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.product.ProductQuantity;
import timber.log.Timber;

/**
 * Simple arrayAdapter for quantity selection.
 */
public class QuantitySpinnerAdapter extends ArrayAdapter<ProductQuantity> {

    private static final int layoutID = R.layout.spinner_item_simple_text;
    private final LayoutInflater layoutInflater;

    private List<ProductQuantity> quantities;

    /**
     * Creates an adapter for quantity selection.
     *
     * @param context    activity context.
     * @param quantities list of items.
     */
    public QuantitySpinnerAdapter(Context context, List<ProductQuantity> quantities) {
        super(context, layoutID, quantities);
        this.quantities = quantities;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return quantities.size();
    }

    public ProductQuantity getItem(int position) {
        return quantities.get(position);
    }

    public long getItemId(int position) {
        return quantities.get(position).getQuantity();
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

        if (getItem(position) != null) {
            holder.text.setText(getItem(position).getValue());
        } else {
            Timber.e("Received null value in %s", this.getClass().getSimpleName());
        }

        return v;
    }

    static class ListItemHolder {
        TextView text;
    }

}