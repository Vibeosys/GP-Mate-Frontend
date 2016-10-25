package com.consultpal.android.listeners;

import android.support.v7.widget.RecyclerView;

/**
 * Created by ines on 5/15/16.
 */
public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);

}
