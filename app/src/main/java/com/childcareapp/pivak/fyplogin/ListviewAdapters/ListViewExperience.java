package com.childcareapp.pivak.fyplogin.ListviewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.childcareapp.pivak.fyplogin.Models.UserExperience;
import com.childcareapp.pivak.fyplogin.R;

import java.util.List;

/**
 * Created by IDEAL on 11/19/2018.
 */

public class ListViewExperience extends ArrayAdapter<UserExperience> {
    private int layoutResource;
    public ListViewExperience(@NonNull Context context, int resource, @NonNull List<UserExperience> objects) {
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
        UserExperience data = getItem(position);
        //thadapter.notifyDataSetChanged();
        if (data != null) {
            TextView post = (TextView) view.findViewById(R.id.experiencePost);
            TextView company = (TextView) view.findViewById(R.id.experienceCompany);
            TextView duration = (TextView) view.findViewById(R.id.experienceDuration);
            TextView location = (TextView) view.findViewById(R.id.experienceLocation);
            if (post != null) {
                post.setText(data.getDesignation());
            }
            if (company != null) {
                company.setText(data.getOrganization());
            }
            if (duration != null) {
                duration.setText(data.getsDate()+" to "+data.geteDate());
            }
            if (location != null) {
                location.setText(data.getCity()+", "+data.getCountry());
            }
        }
        return view;
    }
}
