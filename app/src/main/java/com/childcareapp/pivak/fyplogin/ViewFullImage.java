package com.childcareapp.pivak.fyplogin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ViewFullImage extends AppCompatDialogFragment
{
    String uri,displayName;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.view_full_image, null);
        ImageView imageView = view.findViewById(R.id.fullimage);


        uri = getArguments().getString("uri");
        displayName = getArguments().getString("fileName");
        //Toast.makeText(getContext(), uri, Toast.LENGTH_SHORT).show();
        Picasso.get().load(uri).into(imageView);
        builder.setView(view).setTitle(displayName);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.darkGrey));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkGrey));
            }
        });
        dialog.show();
        return dialog;
    }
}