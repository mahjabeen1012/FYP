package com.childcareapp.pivak.fyplogin.ListviewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.childcareapp.pivak.fyplogin.Models.UserEducation;
import com.childcareapp.pivak.fyplogin.R;

import java.util.List;

/**
 * Created by IDEAL on 11/19/2018.
 */

public class ListViewEducation extends ArrayAdapter<UserEducation> {
    private int layoutResource;
    public ListViewEducation(@NonNull Context context, int resource, @NonNull List<UserEducation> objects) {
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
        UserEducation data = getItem(position);
        //thadapter.notifyDataSetChanged();
        if (data != null) {
            TextView degree = (TextView) view.findViewById(R.id.educationDegree);
            TextView institution = (TextView) view.findViewById(R.id.educationInstitution);
            TextView durationn = (TextView) view.findViewById(R.id.educationDuration);
            TextView locationn = (TextView) view.findViewById(R.id.educationLocation);
            if (degree != null) {
                degree.setText(data.getDegree());
            }
            if (institution != null) {
                institution.setText(data.getInstitution());
            }
            if (durationn != null) {
                durationn.setText(data.getsDate()+ " to "+ data.geteDate());
            }
            if (locationn != null) {
                locationn.setText(data.getCity()+", "+data.getCountry());
            }
        }
        return view;
    }
}
