package bf.io.openshop.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import bf.io.openshop.R;

public class RecyclerDividerDecorator extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private final int marginHorizontal;


    private Drawable mDivider;

    /**
     * Default divider will be used
     */
    public RecyclerDividerDecorator(Context context) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        mDivider = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
        this.marginHorizontal = context.getResources().getDimensionPixelSize(R.dimen.base_recycler_margin);
    }

    /**
     * Custom divider will be used
     */
    public RecyclerDividerDecorator(Context context, int resId) {
        mDivider = ContextCompat.getDrawable(context, resId);
        this.marginHorizontal = context.getResources().getDimensionPixelSize(R.dimen.base_recycler_margin);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(marginHorizontal, 0, marginHorizontal, 0);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
