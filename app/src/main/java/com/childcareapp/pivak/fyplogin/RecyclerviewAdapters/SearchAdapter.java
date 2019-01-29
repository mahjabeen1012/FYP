package com.childcareapp.pivak.fyplogin.RecyclerviewAdapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.childcareapp.pivak.fyplogin.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ImageViewHolder> {

    private Bitmap images[];
    private String names[];
    SearchAdapter(Bitmap images[], String names[])
    {
        this.images = images;
        this.names = names;
    }

    @Override

    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_adapter, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Bitmap img_id = images[position];
        String name_id = names[position];
        holder.picture.setImageBitmap(img_id);
        holder.name.setText(name_id);


    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder
    {
        ImageView picture;
        TextView name;
        public ImageViewHolder(View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.searchAdapterImage);
            name = itemView.findViewById(R.id.searchAdapterName);
        }
    }
}
