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

public class ListViewExperienceCV extends ArrayAdapter<CVData> {
    private int layoutResource;
    public ListViewExperienceCV(@NonNull Context context, int resource, @NonNull List<CVData> objects) {
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
            TextView duration = (TextView) view.findViewById(R.id.experienceDurationCV);
            TextView title = (TextView) view.findViewById(R.id.experienceTitleCV);
            TextView workplace = (TextView) view.findViewById(R.id.experienceWorkplaceCV);
            TextView description = (TextView) view.findViewById(R.id.experienceDescriptionCV);
            if (duration != null) {
                duration.setText(data.getDuration());
            }
            if (title != null) {
                title.setText(data.getTitle());
            }
            if (workplace != null) {
                workplace.setText(data.getWorkplace());
            }
            if (description != null) {
                description.setText(data.getDescription());
            }
        }
        return view;
    }
}
