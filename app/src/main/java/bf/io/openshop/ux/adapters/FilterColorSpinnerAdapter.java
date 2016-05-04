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
import bf.io.openshop.entities.filtr.FilterValueColor;
import timber.log.Timber;

/**
 * Simple filter arrayAdapter for color type selection.
 */
public class FilterColorSpinnerAdapter extends ArrayAdapter<FilterValueColor> {
    private static final int layoutID = R.layout.spinner_item_product_size;
    private final LayoutInflater layoutInflater;

    private List<FilterValueColor> colorValuesList;

    /**
     * Creates an filter adapter for color selection. Color is represented only by text.
     *
     * @param context activity context.
     */
    public FilterColorSpinnerAdapter(Context context) {
        super(context, layoutID);
        this.colorValuesList = new ArrayList<>();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return colorValuesList.size();
    }

    public FilterValueColor getItem(int position) {
        return colorValuesList.get(position);
    }

    public long getItemId(int position) {
        return colorValuesList.get(position).getId();
    }

    public void setColorValuesList(List<FilterValueColor> selectValues) {
        if (selectValues != null) {
            this.colorValuesList.clear();
            this.colorValuesList.addAll(selectValues);
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
//        Timber.e("getView Position: " + position + ". ConvertView: " + convertView);
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

        if (colorValuesList.get(position) != null) {
            holder.sizeText.setText(colorValuesList.get(position).getValue());
        } else {
            Timber.e("Received null productSize in %s", this.getClass().getSimpleName());
        }
        return v;
    }

    static class ListItemHolder {
        TextView sizeText;
    }
}