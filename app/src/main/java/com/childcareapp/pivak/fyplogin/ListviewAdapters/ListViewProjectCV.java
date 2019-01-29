package com.childcareapp.pivak.fyplogin.ListviewAdapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.childcareapp.pivak.fyplogin.Models.CVData;
import com.childcareapp.pivak.fyplogin.R;

import java.util.List;

public class ListViewProjectCV extends ArrayAdapter<CVData> {
    private int layoutResource;
    public ListViewProjectCV(@NonNull Context context, int resource, @NonNull List<CVData> objects) {
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
        CVData data = getItem(position);
        //thadapter.notifyDataSetChanged();
        if (data != null) {
            TextView title = (TextView) view.findViewById(R.id.projectTitleCV);
            TextView description = (TextView) view.findViewById(R.id.projectDescriptionCV);
            if (title != null) {
                title.setText(data.getTitle());
            }
            if (description != null) {
                description.setText(data.getDescription());
            }
        }
        return view;
    }
}
