package com.childcareapp.pivak.fyplogin.ListviewAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.LikeUsersList;
import com.childcareapp.pivak.fyplogin.Models.NotificationsList;
import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.childcareapp.pivak.fyplogin.NewsFeedRecyclerView;
import com.childcareapp.pivak.fyplogin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ListViewNotifications extends ArrayAdapter<NotificationsList> {


    TextView nameUser, content, time, postId, uploaderId;
    ImageView image;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public ListViewNotifications(@NonNull Context context, List<NotificationsList> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.listview_notifications,parent,false);
        }
        nameUser = convertView.findViewById(R.id.chatNameUser);
        image = convertView.findViewById(R.id.chatImageUser);
        content = convertView.findViewById(R.id.chatLastText);
        time = convertView.findViewById(R.id.chatTime);
        postId = convertView.findViewById(R.id.postid);
        uploaderId = convertView.findViewById(R.id.uploaderid);

        final NotificationsList data = getItem(position);
        if(data.getStatus().equals("notRead"))
        {
            convertView.setBackgroundColor(Color.LTGRAY);
        }
        else
        {
            convertView.setBackgroundColor(Color.WHITE);
        }
        Picasso.get().load(data.getUrl().toString()).into(image);


//        db.collection("Users").document("Student").collection(data.getId()).document("Profile").collection("Image").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot documentSnapshots) {
//                if (documentSnapshots.isEmpty())
//                {
//                    //Toast.makeText(getActivity(), "Empty List", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                else
//                {
//                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
//                    {
//                        if (documentSnapshot.exists())
//                        {
//                            String idd=documentSnapshot.getId();
//                            ///////////// Nested query to get Image
//                            DocumentReference imgRef = db.collection("Users").document("Student").collection(data.getId()).document("Profile").collection("Image").document(idd);
//                            imgRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                    if (task.isSuccessful())
//                                    {
//                                        DocumentSnapshot document = task.getResult();
//                                        if (document.exists())
//                                        {
//                                            ///////////////
//                                            Images imag=document.toObject(Images.class);
//                                            URL url = null;
//                                            try
//                                            {
//                                                url =new URL(imag.getUrl());
//                                                //modelNewsFeed.setImg(url);
//                                            }
//                                            catch (MalformedURLException e) {
//                                                e.printStackTrace();
//                                            }
//                                            Picasso.get().load(url.toString()).into(image);
//                                        }
//                                        else
//                                        {
//                                            Log.i(TAG, "onComplete: Image doesn't exist");
//                                        }
//                                    }
//                                    else
//                                    {
//                                        Log.i(TAG, "onComplete: Failed to get Image");
//                                    }
//                                }
//                            });
//                            //// end of nested query
//
//                        }
//                    }
//                }
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.i(TAG, "onFailure: Failed to get Image");
//
//            }
//        });
//        DocumentReference docRef = db.collection("Users").document("Student").collection(data.getId()).document("Profile");
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task)
//            {
//                if (task.isSuccessful())
//                {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists())
//                    {
//                        UserModel userModel = document.toObject(UserModel.class);
//                        String userName = userModel.getfName() + " " + userModel.getlName();
//                        nameUser.setText(userName);
//                    }
//                    else {
//                        Log.i(TAG, "Profile: Document doesn't exist");
//                    }
//                }
//                else {
//                    Log.i(TAG, "Profile: Failed to get data");
//                }
//            }
//        });
        nameUser.setText(data.getStatus());
        if(time != null)
        {
            String timeAgo = NewsFeedRecyclerView.TimeAgo.getTimeAgo(data.getTimeInMillis());
            time.setText(timeAgo);
        }
        if (content != null)
        {
            content.setText(data.getNotification());
        }
        postId.setText(data.getPostId());
        uploaderId.setText(data.getUploaderId());

        return convertView;
    }
}
