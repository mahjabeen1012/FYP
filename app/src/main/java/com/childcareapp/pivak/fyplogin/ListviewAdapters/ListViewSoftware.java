package com.childcareapp.pivak.fyplogin.ListviewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.childcareapp.pivak.fyplogin.R;

import java.util.List;

/**
 * Created by IDEAL on 11/19/2018.
 */

public class ListViewSoftware extends ArrayAdapter<UserModel> {
    private int layoutResource;
    public ListViewSoftware(@NonNull Context context, int resource, @NonNull List<UserModel> objects) {
        super(context, resource, objects);
        this.layoutResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }
        //thadapter.notifyDataSetChanged();
        UserModel data = getItem(position);
        //thadapter.notifyDataSetChanged();
        if (data != null) {
            TextView titleSoftware = (TextView) view.findViewById(R.id.softwareTitle);
            if (titleSoftware != null) {
                titleSoftware.setText(data.getSoftwares());
            }
        }
        return view;
    }
}
