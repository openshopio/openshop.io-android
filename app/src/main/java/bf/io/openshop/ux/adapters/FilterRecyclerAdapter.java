package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.filtr.DeserializerFilters;
import bf.io.openshop.entities.filtr.FilterType;
import bf.io.openshop.entities.filtr.FilterTypeColor;
import bf.io.openshop.entities.filtr.FilterTypeRange;
import bf.io.openshop.entities.filtr.FilterTypeSelect;
import bf.io.openshop.entities.filtr.FilterValueColor;
import bf.io.openshop.entities.filtr.FilterValueSelect;
import bf.io.openshop.entities.filtr.Filters;
import bf.io.openshop.views.RangeSeekBar;
import timber.log.Timber;

/**
 * Adapter handling list of filter items.
 */
public class FilterRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * ID used for simulate default value
     */
    private static final long DEFAULT_ID = -131;

    private static final int TYPE_ITEM_COLOR = 0;
    private static final int TYPE_ITEM_SELECT = 1;
    private static final int TYPE_ITEM_RANGE = 2;

    private final List<FilterType> filterTypeList = new ArrayList<>();

    private final Context context;
    private LayoutInflater layoutInflater;

    /**
     * Creates an adapter that handles a list of filter items.
     *
     * @param context    activity context.
     * @param filterData corresponding filter object.
     */
    public FilterRecyclerAdapter(Context context, Filters filterData) {
        this.context = context;

        // Add default values
        for (FilterType filterType : filterData.getFilters()) {
            if (DeserializerFilters.FILTER_TYPE_COLOR.equals(filterType.getType())) {
                try {
                    List<FilterValueColor> colorValues = ((FilterTypeColor) filterType).getValues();
                    if (colorValues.get(0).getId() != DEFAULT_ID || !colorValues.get(0).getValue().equals(context.getString(R.string.All))) {
                        colorValues.add(0, new FilterValueColor(DEFAULT_ID, context.getString(R.string.All)));
                    }
                } catch (Exception e) {
                    Timber.e(e, "Setting default value for color filter failed");
                }
            } else if (DeserializerFilters.FILTER_TYPE_SELECT.equals(filterType.getType())) {
                try {
                    List<FilterValueSelect> selectValues = ((FilterTypeSelect) filterType).getValues();
                    if (selectValues.get(0).getId() != DEFAULT_ID || !selectValues.get(0).getValue().equals(context.getString(R.string.All))) {
                        selectValues.add(0, new FilterValueSelect(DEFAULT_ID, context.getString(R.string.All)));
                    }
                } catch (Exception e) {
                    Timber.e(e, "Setting default value for select filter failed");
                }
            }
        }

        filterTypeList.addAll(filterData.getFilters());

        // TODO if count of filters is more than 4-5, then is needed to persist selected values.
        // The values are already saved, but views have to be restored properly.
    }

    @Override
    public int getItemCount() {
        return filterTypeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String type = filterTypeList.get(position).getType();
        if (DeserializerFilters.FILTER_TYPE_RANGE.equals(type)) {
            return TYPE_ITEM_RANGE;
        } else if (DeserializerFilters.FILTER_TYPE_COLOR.equals(type)) {
            return TYPE_ITEM_COLOR;
        } else {
            return TYPE_ITEM_SELECT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_ITEM_COLOR) {
            View view = layoutInflater.inflate(R.layout.list_item_filter_select, parent, false);
            return new ViewHolderColor(view, context);
        } else if (viewType == TYPE_ITEM_SELECT) {
            View view = layoutInflater.inflate(R.layout.list_item_filter_select, parent, false);
            return new ViewHolderSelect(view, context);
        } else {
            View view = layoutInflater.inflate(R.layout.list_item_filter_range, parent, false);
            return new ViewHolderRange(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderColor) {
            ViewHolderColor viewHolderColor = (ViewHolderColor) holder;

            FilterTypeColor filterTypeColor = (FilterTypeColor) filterTypeList.get(position);
            viewHolderColor.bindContent(filterTypeColor);
            viewHolderColor.colorName.setText(filterTypeColor.getName());
            viewHolderColor.colorSpinnerAdapter.setColorValuesList(filterTypeColor.getValues());
        } else if (holder instanceof ViewHolderSelect) {
            ViewHolderSelect viewHolderSelect = (ViewHolderSelect) holder;

            FilterTypeSelect filterTypeSelect = (FilterTypeSelect) filterTypeList.get(position);
            viewHolderSelect.bindContent(filterTypeSelect);
            viewHolderSelect.selectName.setText(filterTypeSelect.getName());
            viewHolderSelect.filterSelectSpinnerAdapter.setSelectValuesList(filterTypeSelect.getValues());
        } else if (holder instanceof ViewHolderRange) {
            final ViewHolderRange viewHolderRange = (ViewHolderRange) holder;

            final FilterTypeRange filterTypeRange = (FilterTypeRange) filterTypeList.get(position);
            viewHolderRange.rangeName.setText(filterTypeRange.getName());

            RangeSeekBar<Integer> seekBar = new RangeSeekBar<>(context);
            seekBar.setRangeValues(filterTypeRange.getMin(), filterTypeRange.getMax());

            seekBar.setNotifyWhileDragging(true);
            viewHolderRange.seekBarLayout.removeAllViews();
            viewHolderRange.seekBarLayout.addView(seekBar);

            if (filterTypeRange.getSelectedMin() < 0 && filterTypeRange.getSelectedMax() <= 0) {
                filterTypeRange.setSelectedMin(filterTypeRange.getMin());
                filterTypeRange.setSelectedMax(filterTypeRange.getMax());
            }
            seekBar.setSelectedMinValue(filterTypeRange.getSelectedMin());
            seekBar.setSelectedMaxValue(filterTypeRange.getSelectedMax());
            viewHolderRange.rangeResult.setText(context.getString(R.string.format_price_range,
                    filterTypeRange.getSelectedMin(), filterTypeRange.getRangeTitle(),
                    filterTypeRange.getSelectedMax(), filterTypeRange.getRangeTitle()));

            seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
                @Override
                public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                    viewHolderRange.rangeResult.setText(context.getString(R.string.format_price_range, minValue, filterTypeRange.getRangeTitle(), maxValue, filterTypeRange.getRangeTitle()));
                    filterTypeRange.setSelectedMin(minValue);
                    filterTypeRange.setSelectedMax(maxValue);
                }
            });

        } else {
            Timber.e(new RuntimeException(), "Unknown ViewHolder in class: %s", this.getClass().getSimpleName());
        }
    }


    // Provide a reference to the views for each data item
    public static class ViewHolderColor extends RecyclerView.ViewHolder {

        public TextView colorName;
        public Spinner colorSpinner;
        public FilterColorSpinnerAdapter colorSpinnerAdapter;

        private FilterTypeColor filterTypeColor;

        public ViewHolderColor(View itemView, Context context) {
            super(itemView);
            colorName = (TextView) itemView.findViewById(R.id.list_item_filter_select_title);
            colorSpinner = (Spinner) itemView.findViewById(R.id.list_item_filter_select_spinner);

            colorSpinnerAdapter = new FilterColorSpinnerAdapter(context);
            colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            colorSpinner.setAdapter(colorSpinnerAdapter);
            colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    Timber.d("Color- type: " + filterTypeColor.toString());
//                    Timber.d("Color- selected: " + filterTypeColor.getValues().get(position));
                    if (filterTypeColor.getValues().get(position).getId() != DEFAULT_ID) {
                        filterTypeColor.setSelectedValue(filterTypeColor.getValues().get(position));
                    } else {
                        filterTypeColor.setSelectedValue(null);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        public void bindContent(FilterTypeColor filterTypeColor) {
            this.filterTypeColor = filterTypeColor;
        }
    }

    // Provide a reference to the views for each data item
    public static class ViewHolderSelect extends RecyclerView.ViewHolder {

        public TextView selectName;
        public Spinner selectSpinner;
        public FilterSelectSpinnerAdapter filterSelectSpinnerAdapter;

        private FilterTypeSelect filterTypeSelect;

        public ViewHolderSelect(View itemView, Context context) {
            super(itemView);
            selectName = (TextView) itemView.findViewById(R.id.list_item_filter_select_title);
            selectSpinner = (Spinner) itemView.findViewById(R.id.list_item_filter_select_spinner);

            filterSelectSpinnerAdapter = new FilterSelectSpinnerAdapter(context);
            filterSelectSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectSpinner.setAdapter(filterSelectSpinnerAdapter);
            selectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    Timber.d("Selec- type: " + filterTypeSelect.toString());
//                    Timber.d("Selec- selected: " + filterTypeSelect.getValues().get(position));
                    if (filterTypeSelect.getValues().get(position).getId() != DEFAULT_ID) {
                        filterTypeSelect.setSelectedValue(filterTypeSelect.getValues().get(position));
                    } else {
                        filterTypeSelect.setSelectedValue(null);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        public void bindContent(FilterTypeSelect filterTypeSelect) {
            this.filterTypeSelect = filterTypeSelect;
        }
    }

    // Provide a reference to the views for each data item
    public static class ViewHolderRange extends RecyclerView.ViewHolder {

        public TextView rangeName;
        public TextView rangeResult;
        public LinearLayout seekBarLayout;

        public ViewHolderRange(View itemView) {
            super(itemView);
            rangeName = (TextView) itemView.findViewById(R.id.list_item_filter_range_title);
            rangeResult = (TextView) itemView.findViewById(R.id.list_item_filter_range_result);
            seekBarLayout = (LinearLayout) itemView.findViewById(R.id.list_item_filter_range_bar_layout);
        }
    }

}