package com.childcareapp.pivak.fyplogin.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.Models.CommentUsersList;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.LikeUsersList;
import com.childcareapp.pivak.fyplogin.Models.NotificationsList;
import com.childcareapp.pivak.fyplogin.R;
import com.childcareapp.pivak.fyplogin.RecyclerviewAdapters.CommentRecyclerView;
import com.github.abdularis.civ.CircleImageView;
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
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CommentPostDialog extends AppCompatDialogFragment {

    View view;
    Button mCommentBtn;
    EditText mCommentText;
    CircleImageView circleImageView;
    String postId,uName,uploaderId;
    List<CommentUsersList> mUsersList;
    CommentUsersList commentUsersList;
    RecyclerView recyclerView;
    List<CommentUsersList> commentUsersLists;
    CommentRecyclerView commentRecyclerView;
    int check = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.comment_post_dialog, null);

        postId = getArguments().getString("postId");
        uName = getArguments().getString("uName");
        uploaderId = getArguments().getString("uploaderId");
        mCommentBtn = view.findViewById(R.id.cmntBtn);
        mCommentText = view.findViewById(R.id.cmntText);
        circleImageView = view.findViewById(R.id.imgUsrCmnt);

        recyclerView = view.findViewById(R.id.comment_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        commentUsersLists = new ArrayList<CommentUsersList>();
        commentRecyclerView = new CommentRecyclerView(commentUsersLists,getActivity(),uName);
        recyclerView.setAdapter(commentRecyclerView);

        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mCommentText.getText().toString().equals(""))
                {
                    setComments();
                    if(!uName.equals(uploaderId))
                    {
                        setNotifications();
                    }
                    dismiss();
                }
                else
                {
                    Toast.makeText(getActivity(), "Write a comment to post.", Toast.LENGTH_SHORT).show();
                }
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
        builder.setView(view).setTitle("Comments");
        final AlertDialog dialog = builder.create();
        dialog.show();
        setListviewComments();
        return dialog;
    }
    private void setListviewComments()
    {
        FirebaseFirestore post = FirebaseFirestore.getInstance();
        CollectionReference docRef =   post.collection("NewsfeedComment");
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
                            //newsFeedList.clear();
                            //Toast.makeText(getContext(), "juzzz checking", Toast.LENGTH_SHORT).show();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                CommentUsersList commentUsersList=doc.toObject(CommentUsersList.class);
                                if(commentUsersList.getPostId().equals(postId))
                                {
                                    CommentUsersList addtorecycler = new CommentUsersList(commentUsersList.getId(),commentUsersList.getComment(),commentUsersList.getTimeStamp(),commentUsersList.getTimeInMillis(),commentUsersList.getPostId());
                                    commentUsersLists.add(addtorecycler);
                                }
                            }
                        }
                        else
                        {

                        }
                        int newMsgPosition = commentUsersLists.size() - 1;
                        commentRecyclerView.notifyItemInserted(newMsgPosition);
                    }
                });

//        FirebaseFirestore post = FirebaseFirestore.getInstance();
//        CollectionReference docRef =   post.collection("NewsfeedComment");
//        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task)
//            {
//                mUsersList = new ArrayList<>();
//                if (task.isSuccessful())
//                {
//                    List<LikeUsersList> mUsersList = new ArrayList<>();
//                    LikeUsersList like;
//                    for (QueryDocumentSnapshot document : task.getResult())
//                    {
//                        CommentUsersList commentUsersList=document.toObject(CommentUsersList.class);
//                        if(commentUsersList.getPostId().equals(postId))
//                        {
//                            CommentUsersList addtorecycler = new CommentUsersList(commentUsersList.getId(),commentUsersList.getComment(),commentUsersList.getTimeStamp(),commentUsersList.getTimeInMillis(),commentUsersList.getPostId());
//                            commentUsersLists.add(addtorecycler);
//                        }
//                    }
//
//                }
//                else
//                {
//
//                    Log.w(TAG, "Error getting documents.", task.getException());
//                }
//                int newMsgPosition = commentUsersLists.size() - 1;
//                commentRecyclerView.notifyItemInserted(newMsgPosition);
//                //Toast.makeText(getContext(), Integer.toString(commentRecyclerView.getItemCount()), Toast.LENGTH_SHORT).show();
//            }
//        });
    }
    private void setComments()
    {
        CommentUsersList commentUsersList = new CommentUsersList(uName,mCommentText.getText().toString(),null,System.currentTimeMillis(),postId);
        db.collection("NewsfeedComment").add(commentUsersList).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }});
        Toast.makeText(getActivity(), "Comment Added!", Toast.LENGTH_SHORT).show();
    }
    private void setNotifications()
    {

        NotificationsList notificationsList = new NotificationsList(uName,"commented on your post.","",System.currentTimeMillis(),postId,"notRead",uploaderId);

        db.collection("NewsfeedNotify").add(notificationsList).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }});
    }
}