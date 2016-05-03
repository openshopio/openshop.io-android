package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.product.ProductColor;

/**
 * Simple arrayAdapter for color selection. Color is represented only by text.
 */
public class CartColorTextSpinnerAdapter extends ArrayAdapter<ProductColor> {

    private static final int layoutID = R.layout.spinner_item_simple_text;
    private final LayoutInflater layoutInflater;

    private List<ProductColor> colors;

    /**
     * Creates an adapter for color selection. Color is represented only by text.
     *
     * @param context activity context.
     * @param colors  list of items.
     */
    public CartColorTextSpinnerAdapter(Context context, List<ProductColor> colors) {
        super(context, layoutID, colors);
        this.colors = colors;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return colors.size();
    }

    public ProductColor getItem(int position) {
        return colors.get(position);
    }

    public long getItemId(int position) {
        return colors.get(position).getId();
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
            holder.colorText = (TextView) v.findViewById(R.id.text);
            v.setTag(holder);
        } else {
            holder = (ListItemHolder) v.getTag();
        }

        holder.colorText.setText(getItem(position).getValue());
        return v;
    }

    static class ListItemHolder {
        TextView colorText;
    }
}
