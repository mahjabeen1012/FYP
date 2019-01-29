package com.childcareapp.pivak.fyplogin.ListviewAdapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.Models.ChatDataModel;
import com.childcareapp.pivak.fyplogin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListViewChat extends ArrayAdapter<ChatDataModel> implements AdapterView.OnItemClickListener{
    private int layoutResource;
    public ListViewChat(@NonNull Context context, int resource, @NonNull List<ChatDataModel> objects) {
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
        ChatDataModel data = getItem(position);
        //thadapter.notifyDataSetChanged();
        if (data != null) {
            TextView nameUser = (TextView) view.findViewById(R.id.chatNameUser);
            ImageView image = (ImageView) view.findViewById(R.id.chatImageUser);
            TextView content= (TextView) view.findViewById(R.id.chatLastText);
            TextView time= (TextView) view.findViewById(R.id.chatTime);
            if (nameUser != null) {
                nameUser.setText(data.getName());
            }
            if (image != null)
            {
                Picasso.get().load(data.getImage()).into(image);
                //image.setImageBitmap(data.getBitmapImage());
            }
            if(time != null)
            {
                time.setText(data.getStatus());
            }
            if (content != null)
            {
                if(data.getReadStatus().equals("unread")) {
                    content.setTypeface(content.getTypeface(), Typeface.BOLD);
                    content.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    time.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryDark));
                    nameUser.setTypeface(content.getTypeface(), Typeface.BOLD);
                }
                else
                {
                    content.setTypeface(content.getTypeface(), Typeface.NORMAL);
                    content.setTextColor(ContextCompat.getColor(getContext(), R.color.darkGrey));
                    time.setTextColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
                    nameUser.setTypeface(content.getTypeface(), Typeface.NORMAL);
                }
                content.setText(data.getMessage());
            }

        }
        return view;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getContext(), "THISSSS", Toast.LENGTH_SHORT).show();
    }
}