package bf.io.openshop.ux.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import bf.io.openshop.R;
import bf.io.openshop.ux.adapters.ProductImagesPagerAdapter;
import timber.log.Timber;

public class ProductImagesDialogFragment extends DialogFragment {

    private ArrayList<String> images;
    private int defaultPosition = 0;

    private ViewPager imagesPager;

    public static ProductImagesDialogFragment newInstance(ArrayList<String> images, int defaultPosition) {
        if (images == null || images.isEmpty()) return null;
        ProductImagesDialogFragment frag = new ProductImagesDialogFragment();
        frag.images = images;
        frag.defaultPosition = defaultPosition;
        return frag;
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
            window.setWindowAnimations(R.style.dialogFragmentAnimation);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.dialog_product_detail_images, container, false);

        imagesPager = (ViewPager) view.findViewById(R.id.dialog_product_detail_images_pager);

        // Prepare endless image adapter
        PagerAdapter mPagerAdapter = new ProductImagesPagerAdapter(getActivity(), images);
        imagesPager.setAdapter(mPagerAdapter);

        if (defaultPosition > 0 && defaultPosition < images.size())
            imagesPager.setCurrentItem(defaultPosition);
        else
            imagesPager.setCurrentItem(0);

        ImageView goLeft = (ImageView) view.findViewById(R.id.dialog_product_detail_images_left);
        goLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = imagesPager.getCurrentItem();
                imagesPager.setCurrentItem(position - 1, true);
            }
        });

        ImageView goRight = (ImageView) view.findViewById(R.id.dialog_product_detail_images_right);
        goRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = imagesPager.getCurrentItem();
                imagesPager.setCurrentItem(position + 1, true);
            }
        });

        Button cancel = (Button) view.findViewById(R.id.pager_close);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
