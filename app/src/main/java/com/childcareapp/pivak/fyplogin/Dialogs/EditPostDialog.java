package com.childcareapp.pivak.fyplogin.Dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.Models.ModelNewsFeed;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.R;
import com.github.abdularis.civ.CircleImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class EditPostDialog extends AppCompatDialogFragment{

    CircleImageView circleImageView;
    TextView name,filename;
    EditText content;
    Button updateBtn, cancelBtn,attachBtn;
    String uploaderName,uName,mContent,mfileName,mfileType,mName,postId,downloadUrl;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int READ_REQUEST_CODE = 42;
    String displayName="",extension,fileType="";
    Uri uri=null,storageUrl = null;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    StorageReference ref;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_post_dialog, null);

        uploaderName = getArguments().getString("name");
        uName = getArguments().getString("id");
        mfileName = getArguments().getString("filename");
        mfileType = getArguments().getString("filetype");
        mContent = getArguments().getString("content");
        postId = getArguments().getString("postId");

        circleImageView = view.findViewById(R.id.imgShareContent);
        name = view.findViewById(R.id.nameShareContent);
        content = view.findViewById(R.id.content);
        filename = view.findViewById(R.id.displayname);
        updateBtn = view.findViewById(R.id.updateButton);
        cancelBtn = view.findViewById(R.id.cancelButton);
        attachBtn = view.findViewById(R.id.attachfile);

        name.setText(uploaderName);
        filename.setText(mfileName);
        content.setText(mContent);

        Toast.makeText(getContext(), mfileName, Toast.LENGTH_SHORT).show();
        filename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filename.setText("");
                fileType = "";
                filename.setHint("File name");
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri != null)
                {
                    putIntoStorage();
                }
                else
                {
                    //Toast.makeText(getContext(), "in else of upload", Toast.LENGTH_SHORT).show();
                    storageUrl = null; downloadUrl="";
                    if(content.getText().toString().equals(""))
                    {
                        Toast.makeText(getContext(), "no content found to post", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        putIntoFirestore();
                        //updateRecyclerView();
                        Toast.makeText(getContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        //content.setText("");
                    }
                }
                dismiss();

            }
        });
        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(filename.getText().toString().equals(""))
                {
                    filename.setHint("File name");
                    performFileSearch();
                }
                else
                {
                    Toast.makeText(getActivity(), "A file is already selected.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleImageView.setImageBitmap(null);
                name.setText("");
                content.setText("");
                filename.setText("");
                dismiss();
            }
        });
        db.collection("Users").document("Student").collection(uName).document("Profile").collection("Image").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                {
                    //Toast.makeText(getActivity(), "Empty List", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
                    {
                        if (documentSnapshot.exists())
                        {
                            String idd=documentSnapshot.getId();
                            ///////////// Nested query to get Image
                            //img=getActivity().findViewById(R.id.imgShareContent);
                            DocumentReference imgRef = db.collection("Users").document("Student").collection(uName).document("Profile").collection("Image").document(idd);
                            imgRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists())
                                        {
                                            ///////////////
                                            Images imag=document.toObject(Images.class);
                                            URL url = null;
                                            try
                                            {
                                                url =new URL(imag.getUrl());
                                                //modelNewsFeed.setImg(url);
                                            }
                                            catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            Picasso.get().load(url.toString()).into(circleImageView);
                                        }
                                        else
                                        {
                                            Log.i(TAG, "onComplete: Image doesn't exist");
                                        }
                                    }
                                    else
                                    {
                                        Log.i(TAG, "onComplete: Failed to get Image");
                                    }
                                }
                            });
                            //// end of nested query

                        }
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: Failed to get Image");

            }
        });


        builder.setView(view).setTitle("Edit Post");
        final AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }
    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            if (resultData != null) {
                uri = resultData.getData();
                dumpImageMetaData(uri);
                String path = uri.getPath().toString(); // "/mnt/sdcard/FileName.mp3
                if(path.lastIndexOf(".") != -1 && path.lastIndexOf(".") != 0)
                {
                    extension = path.substring(path.lastIndexOf(".")+1);
                }
                if(extension.equals("png") || extension.equals("jpeg") || extension.equals("jpg") || extension.equals("mp4" ) || extension.equals("mov" ) || extension.equals("pdf" )|| extension.equals("docx") || extension.equals("doc") || extension.equals("PNG") || extension.equals("JPEG") || extension.equals("JPG") || extension.equals("MP4" ) || extension.equals("MOV" ) || extension.equals("PDF" )|| extension.equals("DOCX") || extension.equals("DOC") )
                {

                    filename.setText(displayName);
                    Toast.makeText(getContext(), "File attached", Toast.LENGTH_SHORT).show();
                    if(extension.equals("png") || extension.equals("jpeg") || extension.equals("PNG") || extension.equals("JPEJ") || extension.equals("jpg") || extension.equals("JPG"))
                    {
                        fileType = "img";
                    }
                    if( extension.equals("mp4" ) || extension.equals("MP4" ) || extension.equals("MOV" ) || extension.equals("mov" ))
                    {
                        fileType = "video";
                    }
                    if(extension.equals("pdf" )|| extension.equals("docx") || extension.equals("doc") || extension.equals("PDF" )|| extension.equals("DOCX") || extension.equals("DOC"))
                    {
                        fileType = "doc";
                    }
                }
                else
                {
                    uri = null;
                    displayName = "";
                    fileType = "";
                    Toast.makeText(getContext(), "File not supported", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public void dumpImageMetaData(Uri uri)
    {
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor cursor = getContext().getContentResolver()
                .query(uri, projection, null, null, null, null);

        try
        {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }

            }
        } finally
        {
            cursor.close();
        }
    }
    public void putIntoStorage()
    {
        if(uri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            ref = storageReference.child("FILES/" + displayName);
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            //Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            storageUrl = urlTask.getResult();
                            downloadUrl = storageUrl.toString();
                            Toast.makeText(getContext(), fileType, Toast.LENGTH_SHORT).show();
                            putIntoFirestore();
                            //updateRecyclerView();
//                            content.setText("");
//                            filename.setText("");
//                            uri = null;
//                            fileType = "";
                            Toast.makeText(getContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
        else
        {

        }
    }

    public void putIntoFirestore()
    {
                                FirebaseFirestore post = FirebaseFirestore.getInstance();
                                CollectionReference docRef =   post.collection("NewsFeed");
                                docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                                    {
                                        if (task.isSuccessful())
                                        {

                                            for (final QueryDocumentSnapshot document : task.getResult())
                                            {
                                                final ModelNewsFeed model = document.toObject(ModelNewsFeed.class);
                                                if(model.getPostId().equals(postId))
                                                {
                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("content",content.getText().toString());
                                                    data.put("uploaderId",uName);
                                                    data.put("uploaderName",uploaderName);
                                                    data.put("downloadUrl",downloadUrl);
                                                    data.put("filetype",fileType);
                                                    data.put("fileName",displayName);
                                                    db.collection("NewsFeed").document(document.getId()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            //updateRecyclerView();

                                                        }
                                                    });

                                                }


                                            }
                                        }
                                        else
                                        {
                                            Log.w(TAG, "Error getting documents.", task.getException());
                                        }
                                    }
                                });

    }
}
