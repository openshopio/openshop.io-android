package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bf.io.openshop.entities.product.ProductQuantity;

/**
 * Simple arrayAdapter for quantity selection.
 */
public class QuantitySpinnerAdapter extends ArrayAdapter<ProductQuantity> {

    public Context context;
    public List<ProductQuantity> quantities;

    /**
     * Creates an adapter for quantity selection.
     *
     * @param context  activity context.
     * @param quantities list of items.
     */
    public QuantitySpinnerAdapter(Context context, int resource, List<ProductQuantity> quantities) {
        super(context, resource, quantities);
        this.context = context;
        this.quantities = quantities;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView label = new TextView(context);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        label.setText(quantities.get(position).getValue());

        return label;
    }

}