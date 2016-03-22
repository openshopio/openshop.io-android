package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bf.io.openshop.entities.product.ProductColor;

/**
 * Simple arrayAdapter for color selection. Color is represented only by text.
 */
public class ColorTextSpinnerAdapter extends ArrayAdapter<ProductColor> {

    public Context context;
    public List<ProductColor> colors;

    /**
     * Creates an adapter for color selection. Color is represented only by text.
     *
     * @param context  activity context.
     * @param resource id of a required layout.
     * @param colors   list of items.
     */
    public ColorTextSpinnerAdapter(Context context, int resource, List<ProductColor> colors) {
        super(context, resource, colors);
        this.context = context;
        this.colors = colors;

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
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView label = new TextView(context);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        label.setText(colors.get(position).getValue());

        return label;
    }

}
