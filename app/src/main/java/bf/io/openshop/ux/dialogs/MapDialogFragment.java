package bf.io.openshop.ux.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import bf.io.openshop.R;
import bf.io.openshop.entities.delivery.Branch;
import bf.io.openshop.entities.delivery.OpeningHours;
import bf.io.openshop.entities.delivery.Shipping;
import bf.io.openshop.listeners.OnSingleClickListener;
import timber.log.Timber;

/**
 * Dialog offers a branch detail with an interactive map.
 */
public class MapDialogFragment extends DialogFragment implements OnMapReadyCallback {

    private ShippingDialogFragment shippingDialogFragment;
    private SupportMapFragment supportMapFragment;
    private Branch branch;
    private Shipping shipping;

    public MapDialogFragment() {
        super();
        supportMapFragment = new SupportMapFragment();
        supportMapFragment.getMapAsync(this);
    }

    /**
     * Creates dialog which handles the branch detail with an interactive map.
     *
     * @param fragment fragment which handles branch selection.
     * @param shipping shipping object for selection response.
     * @param branch   branch to display.
     * @return new instance of dialog.
     */
    public static MapDialogFragment newInstance(ShippingDialogFragment fragment, Shipping shipping, Branch branch) {
        MapDialogFragment frag = new MapDialogFragment();
        frag.branch = branch;
        frag.shipping = shipping;
        frag.shippingDialogFragment = fragment;
        return frag;
    }

    /**
     * Creates dialog which handles the branch detail with an interactive map.
     *
     * @param branch branch to display.
     * @return new instance of dialog.
     */
    public static MapDialogFragment newInstance(Branch branch) {
        return newInstance(null, null, branch);
    }


    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.dialog_map, container, false);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.map_frame, supportMapFragment).commit();

        View closeBtn = view.findViewById(R.id.map_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button selectionButton = (Button) view.findViewById(R.id.map_select);
        // If selection isn't supported, hide button.
        if (shippingDialogFragment == null || shipping == null) {
            selectionButton.setVisibility(View.GONE);
        } else {
            selectionButton.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    if (shippingDialogFragment != null && shipping != null) {
                        shippingDialogFragment.onShippingSelected(shipping);
                        dismiss();
                    } else {
                        Timber.e("Something is null");
                    }
                }
            });
        }

        TextView title = (TextView) view.findViewById(R.id.map_title);
        TextView location = (TextView) view.findViewById(R.id.map_location);
        TextView openingTime = (TextView) view.findViewById(R.id.map_shop_info_time);
        TextView note = (TextView) view.findViewById(R.id.map_note);
        LinearLayout locationPaths = (LinearLayout) view.findViewById(R.id.map_shop_info);

        if (branch != null) {
            title.setText(branch.getName());
            location.setText(branch.getAddress());
            if (branch.getOpeningHoursList() != null && branch.getOpeningHoursList().size() > 0) {
                openingTime.setVisibility(View.VISIBLE);
                String timeText = "";
                for (int i = 0; i < branch.getOpeningHoursList().size(); i++) {
                    OpeningHours openingHours = branch.getOpeningHoursList().get(i);
                    timeText += openingHours.getDay() + " " + openingHours.getOpening();
                    if (i != branch.getOpeningHoursList().size() - 1) {
                        timeText += "\n";
                    }
                }
                openingTime.setText(timeText);
            } else {
                openingTime.setVisibility(View.GONE);
            }
            if (branch.getNote() != null && !branch.getNote().isEmpty()) {
                note.setVisibility(View.VISIBLE);
                note.setText(branch.getNote());
            } else {
                note.setVisibility(View.GONE);
            }

            // TODO add "How to get here" options
            // how would normal data looks like? Probably not strings like tram/train/sub
////            List<Transport> transports = branch.getTransports();
////            transports = new ArrayList<>();
////            transports.add(new Transport("tram", "15 minut pesky od tramvaje"));
////            transports.add(new Transport("train", "12 sekund pesky od vlaku"));
////            transports.add(new Transport("sub", "4 piko schody z metra a pak pesky do stanice miru"));
//
//
//            if (transports != null) {
//                for (int i = 0; i < transports.size(); i++) {
//                    View transportView = inflater.inflate(R.layout.dialog_map_transport_item, locationPaths, false);
//                    ImageView locationIcon = (ImageView) transportView.findViewById(R.id.dialog_map_transport_item_image);
//                    TextView locationText = (TextView) transportView.findViewById(R.id.dialog_map_transport_item_text);
//                    locationText.setText(transports.get(i).getText());
//
//                    locationPaths.addView(transportView);
//                }
//            }

            final ScrollView parentScrollView = (ScrollView) view.findViewById(R.id.map_fragment_scroll_view);
            ImageView transparentImageView = (ImageView) view.findViewById(R.id.map_frame_overlay);
            transparentImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            parentScrollView.requestDisallowInterceptTouchEvent(true);
                            // Disable touch on transparent view
                            return false;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            parentScrollView.requestDisallowInterceptTouchEvent(false);
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            parentScrollView.requestDisallowInterceptTouchEvent(true);
                            return false;

                        default:
                            return true;
                    }
                }
            });
        } else {
            Timber.e(new RuntimeException(), "Null branch in MapDialogFragment");
        }
        return view;
    }


    public SupportMapFragment getFragment() {
        return supportMapFragment;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (branch != null && branch.getCoordinates() != null) {
            LatLng position = new LatLng(branch.getCoordinates().getLat(), branch.getCoordinates().getLon());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
            googleMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shop_info_map_mrker))
                    .title(branch.getName())
                    .snippet(branch.getAddress())
                    .position(position)
                    .draggable(false)
                    .anchor((float) 0.917, (float) 0.903));
        }
    }
}
