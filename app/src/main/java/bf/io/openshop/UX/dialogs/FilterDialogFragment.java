package bf.io.openshop.UX.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import bf.io.openshop.R;
import bf.io.openshop.entities.filtr.DeserializerFilters;
import bf.io.openshop.entities.filtr.FilterType;
import bf.io.openshop.entities.filtr.FilterTypeColor;
import bf.io.openshop.entities.filtr.FilterTypeRange;
import bf.io.openshop.entities.filtr.FilterTypeSelect;
import bf.io.openshop.entities.filtr.Filters;
import bf.io.openshop.interfaces.FilterDialogInterface;
import bf.io.openshop.utils.RecyclerMarginDecorator;
import bf.io.openshop.UX.adapters.FilterRecyclerAdapter;
import timber.log.Timber;

public class FilterDialogFragment extends DialogFragment {

    private Filters filterData;
    private FilterDialogInterface filterDialogInterface;

    public static FilterDialogFragment newInstance(Filters filter, FilterDialogInterface filterDialogInterface) {
        FilterDialogFragment filterDialogFragment = new FilterDialogFragment();

        if (filter == null || filterDialogInterface == null) {
            Timber.e(new RuntimeException(), "Created filterDialog with null parameters.");
            return null;
        }
        filterDialogFragment.filterData = filter;
        filterDialogFragment.filterDialogInterface = filterDialogInterface;
        return filterDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogFullscreen);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = d.getWindow();
            window.setLayout(width, height);
            window.setWindowAnimations(R.style.alertDialogAnimation);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.dialog_filters, container, false);

        prepareFilterRecycler(view);

        Button btnApply = view.findViewById(R.id.filter_btn_apply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterUrl = buildFilterUrl();
                filterDialogInterface.onFilterSelected(filterUrl);
                dismiss();
            }
        });

        Button btnCancel = view.findViewById(R.id.filter_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear all selected values
                if (filterData != null) {
                    for (FilterType filterType : filterData.getFilters()) {
                        switch (filterType.getType()) {
                            case DeserializerFilters.FILTER_TYPE_RANGE:
                                ((FilterTypeRange) filterType).setSelectedMin(-1);
                                ((FilterTypeRange) filterType).setSelectedMax(-1);
                                break;
                            case DeserializerFilters.FILTER_TYPE_COLOR:
                                ((FilterTypeColor) filterType).setSelectedValue(null);
                                break;
                            case DeserializerFilters.FILTER_TYPE_SELECT:
                                ((FilterTypeSelect) filterType).setSelectedValue(null);
                                break;
                        }
                    }
                }
                filterDialogInterface.onFilterCancelled();
                dismiss();
            }
        });
        return view;
    }

    private void prepareFilterRecycler(View view) {
        RecyclerView filterRecycler = view.findViewById(R.id.filter_recycler);
        filterRecycler.addItemDecoration(new RecyclerMarginDecorator(getActivity(), RecyclerMarginDecorator.ORIENTATION.VERTICAL));
        filterRecycler.setItemAnimator(new DefaultItemAnimator());
        filterRecycler.setHasFixedSize(true);
        filterRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        FilterRecyclerAdapter filterRecyclerAdapter = new FilterRecyclerAdapter(getActivity(), filterData);
        filterRecycler.setAdapter(filterRecyclerAdapter);
    }

    private String buildFilterUrl() {
        StringBuilder filterUrl = new StringBuilder();

        for (FilterType filterType : filterData.getFilters()) {
            switch (filterType.getType()) {
                case DeserializerFilters.FILTER_TYPE_COLOR:
                    FilterTypeColor filterTypeColor = (FilterTypeColor) filterType;
                    if (filterTypeColor.getSelectedValue() != null) {
                        filterUrl.append("&").append(filterType.getLabel()).append("=").append(filterTypeColor.getSelectedValue().getId());
                    }
                    break;
                case DeserializerFilters.FILTER_TYPE_SELECT:
                    FilterTypeSelect filterTypeSelect = (FilterTypeSelect) filterType;
                    if (filterTypeSelect.getSelectedValue() != null) {
                        filterUrl.append("&").append(filterType.getLabel()).append("=").append(filterTypeSelect.getSelectedValue().getId());
                    }
                    break;
                case DeserializerFilters.FILTER_TYPE_RANGE:
                    FilterTypeRange filterTypeRange = (FilterTypeRange) filterType;
                    if (filterTypeRange.getSelectedMin() >= 0 && filterTypeRange.getSelectedMax() > 0) {
                        filterUrl.append("&").append(filterType.getLabel()).append("=").append(filterTypeRange.getSelectedMin()).append("|").append(filterTypeRange.getSelectedMax());
                    }
                    break;
                default:
                    Timber.e("Unknown filter type.");
                    break;
            }
        }

        Timber.d("BuildFilterUrl - %s", filterUrl.toString());
        return filterUrl.toString();
    }
}
