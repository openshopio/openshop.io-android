package bf.io.openshop.utils;

import android.content.Context;
import android.graphics.Rect;
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
        if (orientation == ORIENTATION.VERTICAL) {
            this.marginVertical = context.getResources().getDimensionPixelSize(R.dimen.base_recycler_margin);
        } else if (orientation == ORIENTATION.HORIZONTAL) {
            this.marginHorizontal = context.getResources().getDimensionPixelSize(R.dimen.base_recycler_margin);
        } else {
            this.marginVertical = context.getResources().getDimensionPixelSize(R.dimen.base_recycler_margin);
            this.marginHorizontal = marginVertical;
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(marginHorizontal, marginVertical, marginHorizontal, marginVertical);
    }

    public enum ORIENTATION {
        HORIZONTAL, VERTICAL, BOTH
    }

}
