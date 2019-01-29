package com.childcareapp.pivak.fyplogin.ListviewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.childcareapp.pivak.fyplogin.Models.UserProjects;
import com.childcareapp.pivak.fyplogin.R;

import java.util.List;

public class ListViewProjects extends ArrayAdapter<UserProjects> {
    private int layoutResource;
    public ListViewProjects(@NonNull Context context, int resource, @NonNull List<UserProjects> objects) {
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
        UserProjects data = getItem(position);
        //thadapter.notifyDataSetChanged();
        if (data != null) {
           // Toast.makeText(getContext(), "Title: "+ data.getTitle(), Toast.LENGTH_SHORT).show();
            TextView title = (TextView) view.findViewById(R.id.projectTitleUser);
            TextView link = (TextView) view.findViewById(R.id.projectLinkUser);
            if (title != null) {
                title.setText(data.getTitle());
            }
            if (link != null) {
                link.setText(data.getDescription());
            }
        }
        return view;
    }
}
