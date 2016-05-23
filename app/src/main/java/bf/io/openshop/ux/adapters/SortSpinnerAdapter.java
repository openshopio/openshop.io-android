package bf.io.openshop.ux.adapters;


import android.app.Activity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.SortItem;

/**
 * Simple arrayAdapter for sort type selection.
 */
public class SortSpinnerAdapter extends ArrayAdapter<SortItem> {

    private List<SortItem> sortItemList = new ArrayList<>();

    /**
     * Creates an adapter for sort type selection.
     *
     * @param activity activity context.
     */
    public SortSpinnerAdapter(Activity activity) {
        super(activity, R.layout.spinner_item_sort);
        this.setDropDownViewResource(R.layout.spinner_item_sort_dropdown);

        //        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SortItem newest = new SortItem("newest", activity.getString(R.string.Whats_new));
        sortItemList.add(newest);
        SortItem popularity = new SortItem("popularity", activity.getString(R.string.Recommended));
        sortItemList.add(popularity);
        SortItem priceDesc = new SortItem("price_DESC", activity.getString(R.string.Highest_price));
        sortItemList.add(priceDesc);
        SortItem priceAsc = new SortItem("price_ASC", activity.getString(R.string.Lowest_price));
        sortItemList.add(priceAsc);

    }

    public int getCount() {
        return sortItemList.size();
    }

    public SortItem getItem(int position) {
        return sortItemList.get(position);
    }


//    public View getCustomView(int position, View convertView, ViewGroup parent) {
//
//        TextView label = new TextView(context);
//        label.setSingleLine(true);
//        label.setEllipsize(TextUtils.TruncateAt.END);
//        label.setText(getItemDescription(position));
//
//        return label;
//    }
}