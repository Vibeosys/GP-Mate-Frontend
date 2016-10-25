package com.consultpal.android.adapters;

/**
 * Created by ines on 5/15/16.
 */
public interface ItemTouchHelperAdapter {

    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and <strong>not</strong> at the end of a "drop" event.<br/>
     * <br/>
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then resolved position of the moved item.
     * @return True if the item was moved to the new adapter position.

     */
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemFinishedMoving();


}