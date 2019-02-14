package com.childcareapp.pivak.fyplogin;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.ModelNewsFeed;
import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;

@IgnoreExtraProperties
public class HomeActivity extends Fragment {

    TextView name,fileName;
    EditText content;
    Button attach, upload,update, removeBtn;
    ImageView img;
    Uri uri=null,storageUrl = null;
    String displayName="", uName,extension,fileType="",UploaderName,downloadUrl,postContent,postId;
    private static final int READ_REQUEST_CODE = 42;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    StorageReference ref;
    RecyclerView recyclerView;
    List<NewsFeedList> newsFeedList;
    NewsFeedRecyclerView newsFeedRecyclerView;
    View view;
    Bitmap bitmap;
    int check = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_activity, container, false);

        uName= getArguments().getString("user");
        byte[] byteArray= getArguments().getByteArray("image");
        bitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

        attach = view.findViewById(R.id.attachButtonProfileUser);
        upload = view.findViewById(R.id.uploadButton);
        fileName = view.findViewById(R.id.displayname);
        content = view.findViewById(R.id.content);
        name = view.findViewById(R.id.nameShareContent);
        update = view.findViewById(R.id.refreshBtn);
        removeBtn = view.findViewById(R.id.remove);
        fileName.setVisibility(View.INVISIBLE);
        removeBtn.setVisibility(View.INVISIBLE);

        loadImage();
        loadStudentName();


        recyclerView = view.findViewById(R.id.news_feed_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        newsFeedList = new ArrayList<NewsFeedList>();
        newsFeedRecyclerView= new NewsFeedRecyclerView(newsFeedList,getActivity(),uName);
        recyclerView.setAdapter(newsFeedRecyclerView);
        //loadNewsfeed();
        //checkfeed();
        //check1();
        checkfeed();

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName.setHint("File name");
                performFileSearch();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(uri != null)
                {
                    putIntoSrorage();
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
                        content.setText("");
                    }
                }

            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayName!="")
                {
                    fileName.setText("");
                    fileType = "";
                    fileName.setHint("File name");
                    fileName.setVisibility(View.INVISIBLE);
                    removeBtn.setVisibility(View.INVISIBLE);
                    uri=null;

                }
                else
                {
                    //false condition
                }

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Home");
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
                    removeBtn.setVisibility(View.VISIBLE);
                    fileName.setVisibility(View.VISIBLE);
                    fileName.setText(displayName);
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

    public void putIntoSrorage()
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
                            putIntoFirestore();
                            //updateRecyclerView();
                            content.setText("");
                            fileName.setText("");
                            uri = null;
                            fileType = "";
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
        postContent = content.getText().toString();
        postId = UUID.randomUUID().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        long time = System.currentTimeMillis();
        ModelNewsFeed modelNewsFeed = new ModelNewsFeed();
        modelNewsFeed.setContent(postContent);
        modelNewsFeed.setUploaderId(uName);
        modelNewsFeed.setDownloadUrl(downloadUrl);
        modelNewsFeed.setUploaderName(UploaderName);
        modelNewsFeed.setFiletype(fileType);
        modelNewsFeed.setFileName(displayName);
        modelNewsFeed.setPostId(postId);
        modelNewsFeed.setLikes(0);
        modelNewsFeed.setTimeInMillis(time);

        db.collection("feed").document("Posts").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        String uploaders="";
                        ///////////////
                        String users=document.getData().get("users").toString();
                        if(!users.equals(""))
                        {
                            String[] tokens = users.split(",");
                            for (String t : tokens)
                            {
                                if(t.equals(uName))
                                {
                                    check = 1;
                                }
                            }
                            if(users.equals(uName))
                            {
                                check = 1;
                            }
                            if(check == 0)
                            {
                                uploaders = users + "," + uName;
                            }
                        }

                        else
                        {
                            uploaders = uName;
                        }
                        if(check == 0)
                        {
                            Map<String, Object> data = new HashMap<>();
                            data.put("users",uploaders);
                            FirebaseFirestore.getInstance().collection("feed").document("Posts").update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                        }
                        ////////
                    }
                    else
                    {
                        Map<String, Object> data = new HashMap<>();
                        data.put("users",uName);
                        FirebaseFirestore.getInstance().collection("feed").document("Posts").set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Failed to get data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        db.collection("feed").document("Posts").collection(uName).add(modelNewsFeed).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

            }
        });
        db.collection("NewsFeed").add(modelNewsFeed).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

            }
        });

        int position = 0;
        NewsFeedList newslist = new NewsFeedList(postContent,uName,downloadUrl,UploaderName,fileType,null,displayName,postId,0,time);
        if(uName.equals(newslist.getUploaderId()))
        {
            newsFeedList.add(position,newslist);
            newsFeedRecyclerView.notifyItemInserted(position);
            //newsFeedRecyclerView.refresh(newsFeedList);
            recyclerView.scrollToPosition(position);
        }


    }

    public void loadImage()
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document("Student").collection(uName).document("Profile").collection("Image").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                            img=getActivity().findViewById(R.id.imgShareContent);
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
                                            Picasso.get().load(url.toString()).into(img);
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
    }

    public void loadStudentName()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document("Student").collection(uName).document("Profile");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        UserModel userModel = document.toObject(UserModel.class);
                        UploaderName = userModel.getfName() + " " + userModel.getlName();
                        name.setText(UploaderName);
                    }
                    else {
                        Log.i(TAG, "Profile: Document doesn't exist");
                    }
                }
                else {
                    Log.i(TAG, "Profile: Failed to get data");
                }
            }
        });


    }

    public void loadNewsfeed()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("feed").document("Posts").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        String users=document.getData().get("users").toString();
                        if(!users.equals(""))
                        {
                            String[] tokens = users.split(",");
                            for (String t : tokens)
                            {
                                FirebaseFirestore post = FirebaseFirestore.getInstance();
                                CollectionReference docRef =   post.collection("feed").document("Posts").collection(t);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                                    {
                                        if (task.isSuccessful())
                                        {

                                            for (QueryDocumentSnapshot document : task.getResult())
                                            {
                                                ModelNewsFeed newsFeed=document.toObject(ModelNewsFeed.class);
                                                NewsFeedList newslist = new NewsFeedList(newsFeed.getContent(), newsFeed.getUploaderId(),newsFeed.getDownloadUrl(),newsFeed.getUploaderName(),newsFeed.getFiletype(),newsFeed.getTimeStamp(),newsFeed.getFileName(),newsFeed.getPostId(),newsFeed.getLikes(),newsFeed.getTimeInMillis());
                                                newsFeedList.add(newslist);


                                            }
                                        }
                                        else
                                        {

                                            Log.w(TAG, "Error getting documents.", task.getException());
                                        }
                                        int newMsgPosition = newsFeedList.size() - 1;
                                        newsFeedRecyclerView.notifyItemInserted(newMsgPosition);

                                    }
                                });
                            }

                        }
                        else
                        {
                            //users=null
                        }
                    }
                }
                else
                {
                    //no database connection
                }

            }});
    }

    private void updateRecyclerView()
    {
        final int size = newsFeedList.size();
        newsFeedList.clear();
        newsFeedRecyclerView.notifyItemRangeRemoved(0, size);
        loadNewsfeed();
        Toast.makeText(getContext(), "New Posts", Toast.LENGTH_SHORT).show();
    }

    public void checkfeed()
    {

        FirebaseFirestore post = FirebaseFirestore.getInstance();
        CollectionReference docRef =   post.collection("NewsFeed");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {

                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        ModelNewsFeed newsFeed=document.toObject(ModelNewsFeed.class);
                        NewsFeedList newslist = new NewsFeedList(newsFeed.getContent(), newsFeed.getUploaderId(),newsFeed.getDownloadUrl(),newsFeed.getUploaderName(),newsFeed.getFiletype(),newsFeed.getTimeStamp(),newsFeed.getFileName(),newsFeed.getPostId(),newsFeed.getLikes(),newsFeed.getTimeInMillis());
                        newsFeedList.add(newslist);


                    }
                    //Toast.makeText(getContext(), Integer.toString(newsFeedList.size()), Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Log.w(TAG, "Error getting documents.", task.getException());
                }
                int newMsgPosition = newsFeedList.size() - 1;
                newsFeedRecyclerView.notifyItemInserted(newMsgPosition);

            }
        });



    }

    private void check1()
    {
        FirebaseFirestore post = FirebaseFirestore.getInstance();
        CollectionReference docRef =   post.collection("NewsFeed");
        docRef.orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                        @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null)
                        {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                ModelNewsFeed newsFeed=doc.toObject(ModelNewsFeed.class);
                                NewsFeedList newslist = new NewsFeedList(newsFeed.getContent().toString(), newsFeed.getUploaderId().toString(),newsFeed.getDownloadUrl().toString(),newsFeed.getUploaderName().toString(),newsFeed.getFiletype().toString(),newsFeed.getTimeStamp(),newsFeed.getFileName().toString(),newsFeed.getPostId(),newsFeed.getLikes(),newsFeed.getTimeInMillis());
                                newsFeedList.add(newslist);
                            }
                            Toast.makeText(getContext(), Integer.toString(newsFeedList.size()), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                        }
                        int newMsgPosition = newsFeedList.size() - 1;
                        newsFeedRecyclerView.notifyItemInserted(newMsgPosition);
                    }
                });
    }

    public void refresh()
    {
        final int size = newsFeedList.size();
        newsFeedList.clear();
        newsFeedRecyclerView.notifyItemRangeRemoved(0, size);
        //check1();
        checkfeed();
        //Toast.makeText(getContext(), "New Posts", Toast.LENGTH_SHORT).show();
    }


}
