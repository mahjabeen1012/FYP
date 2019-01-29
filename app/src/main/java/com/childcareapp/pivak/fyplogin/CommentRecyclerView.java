package com.childcareapp.pivak.fyplogin.RecyclerviewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.childcareapp.pivak.fyplogin.Models.CommentUsersList;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.childcareapp.pivak.fyplogin.NewsFeedList;
import com.childcareapp.pivak.fyplogin.NewsFeedRecyclerView;
import com.childcareapp.pivak.fyplogin.NewsFeedViewHolder;
import com.childcareapp.pivak.fyplogin.R;
import com.github.abdularis.civ.CircleImageView;
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
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class CommentRecyclerView  extends RecyclerView.Adapter<CommentRecyclerView.CommentViewHolder> {

    private Context context;
    Integer likes;
    String uName;
    private List<CommentUsersList> commentUsersList = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CommentRecyclerView(List<CommentUsersList> commentUsersList, Context context, String uName) {
        this.commentUsersList = commentUsersList;
        this.context = context;
        this.uName = uName;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.listview_comments, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {

        final CommentUsersList comments = this.commentUsersList.get(position);

        holder.mComment.setText(comments.getComment());
        String timeAgo = NewsFeedRecyclerView.TimeAgo.getTimeAgo(comments.getTimeInMillis());
        holder.mTime.setText(timeAgo);

        db.collection("Users").document("Student").collection(comments.getId()).document("Profile").collection("Image").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                            DocumentReference imgRef = db.collection("Users").document("Student").collection(comments.getId()).document("Profile").collection("Image").document(idd);
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
                                            Picasso.get().load(url.toString()).into(holder.circleImageView);
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
        DocumentReference docRef = db.collection("Users").document("Student").collection(comments.getId()).document("Profile");
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
                        String userName = userModel.getfName() + " " + userModel.getlName();
                        holder.mName.setText(userName);
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

    @Override
    public int getItemCount() {
        if(commentUsersList==null)
        {
            commentUsersList = new ArrayList<CommentUsersList>();
        }
        return commentUsersList.size();
    }
    public class CommentViewHolder extends RecyclerView.ViewHolder
    {

        TextView mName,mComment,mTime;
        CircleImageView circleImageView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name_comment);
            mComment = itemView.findViewById(R.id.text_comment);
            mTime = itemView.findViewById(R.id.time_comment);
            circleImageView = itemView.findViewById(R.id.image_comment);
        }
    }
}