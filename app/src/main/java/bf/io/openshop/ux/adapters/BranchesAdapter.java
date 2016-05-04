package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.delivery.Branch;

/**
 * BaseAdapter for branches list.
 */
public class BranchesAdapter extends BaseAdapter {

    private List<Branch> branches = new ArrayList<>();
    private LayoutInflater layoutInflater;

    /**
     * Creates an adapter for branches list.
     *
     * @param context  activity context
     * @param branches list of items.
     */
    public BranchesAdapter(Context context, List<Branch> branches) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.branches = branches;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        return branches.size();
    }

    @Override
    public Branch getItem(int position) {
        return branches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_item_shipping, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.shipping_title);
            holder.description = (TextView) convertView.findViewById(R.id.shipping_description);
            holder.price = (TextView) convertView.findViewById(R.id.shipping_price);
            holder.shopInfo = (ImageView) convertView.findViewById(R.id.shipping_shop_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Branch item = branches.get(position);
        holder.price.setVisibility(View.GONE);
        holder.title.setText(item.getName());
        holder.description.setText(item.getAddress());
        holder.shopInfo.setVisibility(View.VISIBLE);
        return convertView;
    }

    public static class ViewHolder {
        public TextView title;
        public TextView description;
        public TextView price;
        public ImageView shopInfo;
    }
}
