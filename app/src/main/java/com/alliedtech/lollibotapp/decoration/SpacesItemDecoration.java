package com.alliedtech.lollibotapp.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        if (parent.getChildLayoutPosition(view) % 2 == 0) {
            outRect.right = space;
            outRect.left = 0;
        }
        else {
            outRect.left = space;
            outRect.right = 0;
        }

        outRect.bottom = space;

        // Add top margin only for the first two items to avoid double space between items
        if (parent.getChildLayoutPosition(view) / 2 == 0) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }
    }
}