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
import bf.io.openshop.entities.filtr.FilterValueSelect;
import timber.log.Timber;

/**
 * Simple filter arrayAdapter for select type selection.
 */
public class FilterSelectSpinnerAdapter extends ArrayAdapter<FilterValueSelect> {
    private static final int layoutID = R.layout.spinner_item_product_size;
    private final LayoutInflater layoutInflater;

    private List<FilterValueSelect> selectValuesList;

    /**
     * Creates an filter adapter for select type selection.
     *
     * @param context activity context.
     */
    public FilterSelectSpinnerAdapter(Context context) {
        super(context, layoutID);
        this.selectValuesList = new ArrayList<>();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return selectValuesList.size();
    }

    public FilterValueSelect getItem(int position) {
        return selectValuesList.get(position);
    }

    public long getItemId(int position) {
        return selectValuesList.get(position).getId();
    }

    public void setSelectValuesList(List<FilterValueSelect> selectValues) {
        if (selectValues != null) {
            this.selectValuesList.clear();
            this.selectValuesList.addAll(selectValues);
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

        if (selectValuesList.get(position) != null) {
            holder.sizeText.setText(selectValuesList.get(position).getValue());
        } else {
            Timber.e("Received null productSize in %s", this.getClass().getSimpleName());
        }
        return v;
    }

    static class ListItemHolder {
        TextView sizeText;
    }
}