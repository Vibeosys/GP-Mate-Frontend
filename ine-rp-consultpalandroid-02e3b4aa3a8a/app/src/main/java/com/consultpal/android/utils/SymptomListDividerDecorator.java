package com.consultpal.android.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ines on 5/15/16.
 */
public class SymptomListDividerDecorator extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;

    public SymptomListDividerDecorator(int mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        // Dont add divider after the last item
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = mVerticalSpaceHeight;
        }
    }
}
