package com.childcareapp.pivak.fyplogin.ListviewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.childcareapp.pivak.fyplogin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListViewUsers  extends ArrayAdapter<UserModel> {
    private int layoutResource;
    public ListViewUsers(@NonNull Context context, int resource, @NonNull List<UserModel> objects) {
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
            TextView name = (TextView) view.findViewById(R.id.searchUserName);
            TextView uId = (TextView) view.findViewById(R.id.searchUserID);
            ImageView image = (ImageView) view.findViewById(R.id.searchUserImage);
            if (name != null) {
                name.setText(data.getfName()+" "+data.getlName());
            }
            if (uId != null) {
                uId.setText(data.getUserId());
            }
            if (image != null)
            {
                /////////////////////////// I COMMENTED THIS LIKE 52!
                Picasso.get().load(data.getImage().toString()).into(image);
                //  image.setBackground(data.getImage());
            }
        }
        return view;
    }
}
