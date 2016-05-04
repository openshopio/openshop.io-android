package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.product.ProductColor;
import bf.io.openshop.views.RoundedImageView;
import timber.log.Timber;

/**
 * Simple arrayAdapter for color selection.
 */
public class ColorSpinnerAdapter extends ArrayAdapter<ProductColor> {
    private static final int layoutID = R.layout.spinner_item_product_color;
    private final LayoutInflater layoutInflater;

    private List<ProductColor> productColorList;

    /**
     * Creates an adapter for color selection.
     *
     * @param context activity context.
     */
    public ColorSpinnerAdapter(Context context) {
        super(context, layoutID);
        this.productColorList = new ArrayList<>();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return productColorList.size();
    }

    public ProductColor getItem(int position) {
        return productColorList.get(position);
    }

    public long getItemId(int position) {
        return productColorList.get(position).getId();
    }

    public void setProductColorList(List<ProductColor> productColors) {
        if (productColors != null) {
            this.productColorList.addAll(productColors);
            notifyDataSetChanged();
        } else {
            Timber.e("Trying set null color list in %s", this.getClass().getSimpleName());
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
            holder.colorImage = (RoundedImageView) v.findViewById(R.id.color_picker_image_view);
            holder.colorText = (TextView) v.findViewById(R.id.color_picker_text);
            holder.colorStroke = (LinearLayout) v.findViewById(R.id.color_picker_image_stroke);
            v.setTag(holder);
        } else {
            holder = (ListItemHolder) v.getTag();
        }

        ProductColor color = productColorList.get(position);
        Picasso.with(getContext()).cancelRequest(holder.colorImage);
        if (color != null) {
            holder.colorText.setText(color.getValue());

            if (color.getId() == -5) {
                holder.colorStroke.setVisibility(View.INVISIBLE);
            } else {
                if (color.getCode() != null && (!color.getCode().isEmpty())) {
                    final String hexColor = color.getCode();
                    GradientDrawable gradDrawable = (GradientDrawable) holder.colorImage.getBackground();
                    int resultColor = 0xffffffff;
                    try {
                        resultColor = Color.parseColor(hexColor);
                    } catch (Exception e) {
                        Timber.e(e, "CustomSpinnerColors parse color exception");
                    }
                    gradDrawable.setColor(resultColor);
                } else {
                    Picasso.with(getContext()).load(color.getImg()).fit().into(holder.colorImage);
                    GradientDrawable gradDrawable = (GradientDrawable) holder.colorImage.getBackground();
                    gradDrawable.setColor(Color.TRANSPARENT);
                }
            }
        } else {
            Timber.e("Received null productColor in %s", this.getClass().getSimpleName());
        }
        return v;
    }

    static class ListItemHolder {
        TextView colorText;
        RoundedImageView colorImage;
        LinearLayout colorStroke;
    }
}