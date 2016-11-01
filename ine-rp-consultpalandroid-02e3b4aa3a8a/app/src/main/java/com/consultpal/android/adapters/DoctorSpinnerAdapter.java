package com.consultpal.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.consultpal.android.R;
import com.consultpal.android.model.Doctor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshay on 28-09-2016.
 */
public class DoctorSpinnerAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<Doctor> doctors;

    public DoctorSpinnerAdapter(Context mContext, ArrayList<Doctor> doctors) {
        this.mContext = mContext;
        this.doctors = doctors;
    }

    @Override
    public int getCount() {
        return doctors.size();
    }

    @Override
    public Object getItem(int position) {
        return doctors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder viewHolder = null;
        if (row == null) {

            LayoutInflater theLayoutInflater = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            row = theLayoutInflater.inflate(R.layout.doctor_spinner_child_element, null);
            viewHolder = new ViewHolder();
            viewHolder.spinnerChild = (TextView) row.findViewById(R.id.txtSpinnerChild);
            row.setTag(viewHolder);

        } else
            viewHolder = (ViewHolder) convertView.getTag();
        Doctor doctor = doctors.get(position);
        viewHolder.spinnerChild.setText(doctor.getName());

        return row;
    }

    private class ViewHolder {
        TextView spinnerChild;
    }
   /* public void addItem(final TypeDataDTO item) {
        mCategories.add(item);
        notifyDataSetChanged();
    }

    public void clear() {
        mCategories.clear();
    }*/
}