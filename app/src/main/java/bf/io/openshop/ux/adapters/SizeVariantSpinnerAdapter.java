package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.product.ProductVariant;
import timber.log.Timber;

/**
 * Adapter handling list of all available product sizes.
 */
public class SizeVariantSpinnerAdapter extends ArrayAdapter<ProductVariant> {
    private static final int layoutID = R.layout.spinner_item_product_size;
    private final LayoutInflater layoutInflater;

    private List<ProductVariant> productSizeList;

    /**
     * Creates an adapter for available product sizes selection.
     * Creates an empty sizes list.
     *
     * @param context activity context.
     */
    public SizeVariantSpinnerAdapter(Context context) {
        super(context, layoutID);
        this.productSizeList = new ArrayList<>();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Creates an adapter for available product sizes selection.
     *
     * @param context activity context.
     * @param sizes   list of items.
     */
    public SizeVariantSpinnerAdapter(Context context, List<ProductVariant> sizes) {
        super(context, layoutID, sizes);
        this.productSizeList = sizes;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return productSizeList.size();
    }

    public ProductVariant getItem(int position) {
        return productSizeList.get(position);
    }

    public long getItemId(int position) {
        return productSizeList.get(position).getId();
    }

    public void setProductSizeList(List<ProductVariant> sizes) {
        if (sizes != null) {
            this.productSizeList.clear();
            this.productSizeList.addAll(sizes);
            notifyDataSetChanged();
        } else {
            Timber.e("Trying set null size list in %s", this.getClass().getSimpleName());
        }
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
//        Timber.d("getView Position: " + position + ". ConvertView: " + convertView);
        View v = convertView;
        ListItemHolder holder;

        if (v == null) {
            v = layoutInflater.inflate(layoutID, parent, false);
            holder = new ListItemHolder();
            holder.sizeText = (TextView) v.findViewById(R.id.size_spinner_text);
            v.setTag(holder);
        } else {
            holder = (ListItemHolder) v.getTag();
        }

        if (productSizeList.get(position) != null && productSizeList.get(position).getSize() != null) {
            holder.sizeText.setText(productSizeList.get(position).getSize().getValue());
        } else {
            Timber.e("Received null productSize in %s", this.getClass().getSimpleName());
        }
        return v;
    }

    static class ListItemHolder {
        TextView sizeText;
    }
}