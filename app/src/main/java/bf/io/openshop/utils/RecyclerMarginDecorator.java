package bf.io.openshop.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import bf.io.openshop.R;

public class RecyclerMarginDecorator extends RecyclerView.ItemDecoration {

    private int marginHorizontal = 0;
    private int marginVertical = 0;
    public RecyclerMarginDecorator(int margin) {
        this.marginVertical = margin;
        this.marginHorizontal = margin;
    }

    public RecyclerMarginDecorator(Context context, ORIENTATION orientation) {
        switch (orientation) {
            case VERTICAL:
                this.marginVertical = context.getResources().getDimensionPixelSize(R.dimen.base_recycler_margin);
                break;
            case HORIZONTAL:
                this.marginHorizontal = context.getResources().getDimensionPixelSize(R.dimen.base_recycler_margin);
                break;
            default:
                this.marginVertical = context.getResources().getDimensionPixelSize(R.dimen.base_recycler_margin);
                this.marginHorizontal = marginVertical;
                break;
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.set(marginHorizontal, marginVertical, marginHorizontal, marginVertical);
    }

    public enum ORIENTATION {
        HORIZONTAL, VERTICAL, BOTH
    }

}
