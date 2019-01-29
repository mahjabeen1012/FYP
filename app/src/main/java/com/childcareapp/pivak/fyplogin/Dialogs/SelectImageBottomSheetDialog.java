package com.childcareapp.pivak.fyplogin.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.childcareapp.pivak.fyplogin.Dialogs.EditIntroCompanyDialog;
import com.childcareapp.pivak.fyplogin.Dialogs.EditIntroDialog;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class SelectImageBottomSheetDialog extends BottomSheetDialogFragment {
    Button capture,upload,remove;
    String uName,imageLink,statuss="";
    ImageView img;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_image_select, container, false);
        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.edit_intro_company_dialog, container,false);
        img=view.findViewById(R.id.editDialogImageviewCompany);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pivak);
        img.setImageBitmap(bitmap);

        uName=getArguments().getString("user");
        imageLink=getArguments().getString("imageLink");
        statuss=getArguments().getString("status");

        capture=v.findViewById(R.id.takeImageProfile);
        upload=v.findViewById(R.id.uploadImageProfile);
        remove=v.findViewById(R.id.deleteImageProfile);

        //////// FROM CAMERA
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
        });

        //////FROM GALLERY
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });

        ///////REMOVE PHOTO
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profileimage);
                //img.setImageBitmap(bitmap);
                uploadImage(bitmap,"Removed");
            }
        });
        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bitmap bitmap=null;
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK)
                {
                    ////////////// from camera
                    bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    //img.setImageBitmap(bitmap);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK)
                {
                    /////////// from gallery
                    if (imageReturnedIntent != null) {
                        Uri contentURI = imageReturnedIntent.getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                            //img.setImageBitmap(bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i(TAG, "onActivityResult: Failed");
                        }
                    }
                }
                break;
        }

        if(bitmap!=null)
        {
            uploadImage(bitmap,"Updated");
        }
    }
    public void loadPreviousFragment()
    {
        Bundle bundle2 = new Bundle();
        bundle2.putString("user", uName);
        bundle2.putString("status", statuss);
        bundle2.putString("profileType",getArguments().getString("profileType"));
        if(statuss.equals("Company"))
        {
            EditIntroCompanyDialog editIntroDialog = new EditIntroCompanyDialog();
            editIntroDialog.setArguments(bundle2);
            editIntroDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Edit Introduction");
        }
        else {
            EditIntroDialog editIntroDialog = new EditIntroDialog();
            editIntroDialog.setArguments(bundle2);
            editIntroDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Edit Introduction");
        }
    }
    public void uploadImage(Bitmap bitmap, final String status)
    {
        final FirebaseFirestore mStore=FirebaseFirestore.getInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        final String[] str1 = uName.split("@");
        final StorageReference sReff = mStorageRef.child("Images/" + str1[0] + ".jpg");
        UploadTask uploadTask = sReff.putBytes(data);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return sReff.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful())
                {
                    Uri downloadUri = task.getResult();
                    Images imgg = new Images(str1[0], downloadUri.toString());
                    if (!imageLink.equals("")) {
                        mStore.collection("Users").document(statuss).collection(uName).document("Profile")
                                .collection("Image").document(imageLink).set(imgg)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG, "onSuccess: photo updated");
                            }
                        });
                    }
                    else
                    {
                        mStore.collection("Users").document(statuss).collection(uName).document("Profile").collection("Image").add(imgg).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference aVoid) {
                                Log.i(TAG, "onSuccess: photo updated");
                            }
                        });
                    }
                    //Toast.makeText(getContext(), "Photo "+status+" Successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i(TAG, "upload failed: "+task.getException().getMessage());
                }
            }
        });

        //loadPreviousFragment();
        dismiss();
    }
}
