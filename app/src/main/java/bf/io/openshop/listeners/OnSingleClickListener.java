package bf.io.openshop.listeners;

import android.os.SystemClock;
import android.view.View;

/**
 * Custom {@link android.view.View.OnClickListener} preventing unwanted double click.
 */
public abstract class OnSingleClickListener implements View.OnClickListener {

    /**
     * The time between two consecutive click events, in milliseconds.
     */
    private static final long MIN_CLICK_INTERVAL = 800;

    private long mLastClickTime = 0;

    /**
     * Custom {@link android.view.View.OnClickListener} preventing unwanted double click.
     *
     * @param view View where double click is undesirable
     */
    public abstract void onSingleClick(View view);

    @Override
    public final void onClick(View v) {
        if (SystemClock.uptimeMillis() - mLastClickTime < MIN_CLICK_INTERVAL)
            return;
        mLastClickTime = SystemClock.uptimeMillis();

        onSingleClick(v);
    }

}
