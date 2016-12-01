package com.consultpal.android.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.consultpal.android.R;
import com.consultpal.android.listeners.OnStartDragListener;
import com.consultpal.android.model.Symptom;
import com.consultpal.android.utils.SharedPrefManager;
import com.consultpal.android.views.SessionActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ines on 5/15/16.
 */
public class SymptomsRVAdapter extends RecyclerView.Adapter<SymptomsRVAdapter.SimpleViewHolder>
        implements ItemTouchHelperAdapter {

    private final Context mContext;
    private List<Symptom> mData;
    private final OnStartDragListener mDragStartListener;
    private boolean onBind;
    private int maxNoOfBox;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final RelativeLayout background_layout;
        public final ImageView dragView;
        public final TextView description;

        public SimpleViewHolder(View view) {
            super(view);

            background_layout = (RelativeLayout) view.findViewById(R.id.symptom_list_item_bg);
            description = (TextView) view.findViewById(R.id.symptom_list_item_description);
            dragView = (ImageView) view.findViewById(R.id.symptom_list_item_drag_layout);
        }
    }

    public SymptomsRVAdapter(Context context, List<Symptom> data, OnStartDragListener dragStartListener, int maxNoOfBox) {
        mContext = context;
        if (data != null) {
            mData = new ArrayList<>(data);
        } else {
            mData = new ArrayList<>();
        }
        mDragStartListener = dragStartListener;
        this.maxNoOfBox = maxNoOfBox;
    }

    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.symptom_list_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        onBind = true;
        holder.background_layout.setBackgroundResource(getBackgroundDrawableColor(position));
        if (position > 0) {
            holder.description.setHint(mContext.getString(R.string.session_symptom_list_others_hint));
        } else {
            holder.description.setHint(mContext.getString(R.string.session_symptom_list_first_hint));
        }
        holder.description.setText(mData.get(position).getDescription());
        // Set click listener for course items only, not titles
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionActivity activity = (SessionActivity) mContext;
                activity.openSymptomDetail(mData.get(position), position);

            }
        });

        // Start a drag whenever the handle view it touched
        holder.dragView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        onBind = false;
    }

    public int getBackgroundDrawableColor(int position) {
        switch (position) {
            case 0:
                return R.drawable.symptom_item_list_border_1;
            case 1:
                return R.drawable.symptom_item_list_border_2;
            case 2:
                return R.drawable.symptom_item_list_border_3;
            case 3:
                return R.drawable.symptom_item_list_border_4;
            case 4:
                return R.drawable.symptom_item_list_border_5;
            case 5:
                return R.drawable.symptom_item_list_border_6;
            case 6:
                return R.drawable.symptom_item_list_border_7;
            case 7:
                return R.drawable.symptom_item_list_border_8;
            default:
                return R.drawable.symptom_item_list_border_8;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateItemsList(ArrayList<Symptom> symptomsList) {
        mData.clear();
        mData = new ArrayList<>(symptomsList);
        notifyDataSetChanged();
    }

    public List<Symptom> getItemsList() {
        return mData;
    }

    public void updateItem(Symptom symptom, int position) {
        mData.set(position, symptom);
        notifyItemChanged(position);
    }

    public boolean addNewBox(int position) {
        if (mData.size() < this.maxNoOfBox && position == mData.size() - 1) {
            mData.add(new Symptom(null, position + 1));
            notifyItemChanged(position + 1);
            return true;
        }
        return false;
    }

    public void deleteItem(int position) {
        // Can't delete all items from list
        if (mData.size() > 1) {
            // Add to symptomsToDelete before removing from List
            SessionActivity activity = (SessionActivity) mContext;
            activity.addSymptomToDelete(mData.get(position));
            mData.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        String strFromText = mData.get(fromPosition).getDescription();
        String strToText = mData.get(toPosition).getDescription();
        if ((TextUtils.isEmpty(strFromText) || strFromText == null)
                || (TextUtils.isEmpty(strToText) || strToText == null)) {
            return false;
        }
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mData, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mData, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemFinishedMoving() {
        // Update priorities after swap
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setPriority(i + 1);
        }

        // A Handler is used to call notifyDataSetChanged in order to fix IllegalStateException
        // when swiping items too fast
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                // Used to always keep colors in the same order
                notifyDataSetChanged();
            }
        };
        handler.post(r);

    }


}