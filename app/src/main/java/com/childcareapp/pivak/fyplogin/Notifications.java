package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewNotifications;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.NotificationsList;
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
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Notifications extends Fragment {

    String uName,userType;
    ProgressBar progressBar;
    List<NotificationsList> mUsersList;
    ListView mLikeUsersListView;
    int totalNotifications=0;
    String postID, uID;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.notifications, container, false);
        uName = getArguments().getString("user");
        userType = getArguments().getString("userType");
        progressBar=view.findViewById(R.id.notificationProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        final ListView listView = view.findViewById(R.id.list_view_notifications);
        mLikeUsersListView = view.findViewById(R.id.list_view_notifications);

        FirebaseFirestore post = FirebaseFirestore.getInstance();
        post.collection("NewsfeedNotify").orderBy("timeStamp", Query.Direction.DESCENDING)
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
                            totalNotifications=queryDocumentSnapshots.size();
                            mUsersList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                NotificationsList notifyList = document.toObject(NotificationsList.class);
                                if (notifyList.getUploaderId().equals(uName)) {
                                    //mUsersList.add(notifyList);
                                    postID=notifyList.getPostId();
                                    uID=notifyList.getUploaderId();
                                    getData(notifyList.getId(), notifyList.getNotification(),notifyList.getTimeInMillis());
                                    //setListview();
                                }
                            }

                            //progressBar.setVisibility(View.INVISIBLE);
                        }
                        else
                        {

                        }
                    }
                });


        return view;
    }
    public void displayUsers()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                while (mLikeUsersListView.getChildAt(totalNotifications-1) == null ||
//                        mLikeUsersListView.getChildAt(totalNotifications-1) .equals("") ) { // your conditions
//                }
                progressBar.setVisibility(View.INVISIBLE);
                listner(); // your task to execute
            }
        };
        new Thread(runnable).start();
    }

    public void listner()
    {
        mLikeUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                View cView=mLikeUsersListView.getChildAt(position);
                final TextView posId = cView.findViewById(R.id.postid);
                TextView uploaderid = cView.findViewById(R.id.uploaderid);
                String status="";
                if(status.equals("job"))
                {
                    Bundle bundle2=new Bundle();
                    bundle2.putString("user", uName);
                    bundle2.putString("userStatus", "Student");
                    JobRecommendation jobRecommendation = new JobRecommendation();
                    jobRecommendation.setArguments(bundle2);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    if (userType.equals("Student")) {
                        fragmentTransaction.replace(R.id.content_student_home, jobRecommendation);
                    }
                    if (userType.equals("Alumni")) {
                        fragmentTransaction.replace(R.id.content_alumni_home, jobRecommendation);
                    }
                    if (userType.equals("Company")) {
                        fragmentTransaction.replace(R.id.content_company_home, jobRecommendation);
                    }
                    if (userType.equals("Faculty")) {
                        fragmentTransaction.replace(R.id.content_faculty_home, jobRecommendation);
                    }
                    fragmentTransaction.commit();
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("uploaderId", uploaderid.getText().toString());
                    bundle.putString("postId", posId.getText().toString());
                    bundle.putString("uName", uName);
                    ViewNotification viewNotification = new ViewNotification();
                    viewNotification.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    if (userType.equals("Student")) {
                        fragmentTransaction.replace(R.id.content_student_home, viewNotification);
                    }
                    if (userType.equals("Alumni")) {
                        fragmentTransaction.replace(R.id.content_alumni_home, viewNotification);
                    }
                    if (userType.equals("Company")) {
                        fragmentTransaction.replace(R.id.content_company_home, viewNotification);
                    }
                    if (userType.equals("Faculty")) {
                        fragmentTransaction.replace(R.id.content_faculty_home, viewNotification);
                    }
                    fragmentTransaction.commit();
                }

            }
        });
    }
    public void getData(final String idd, final String text, final long time)
    {
        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(idd)
                .document("Profile").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                        loadImage(idd, userName, text, time);
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
    public void loadImage(final String iddd, final String name, final String text, final long time)
    {
        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(iddd)
                .document("Profile").collection("Image").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                            final String idd=documentSnapshot.getId();
                            ///////////// Nested query to get Image
                            DocumentReference imgRef = FirebaseFirestore.getInstance().collection("Users")
                                    .document("Student").collection(iddd).document("Profile").collection("Image").document(idd);
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
                                                setListview(iddd, text,name, time, url.toString());
                                            }
                                            catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }

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
    public void setListview(final String idd, final String text, final String name, final long time, final String image)
    {
        NotificationsList notificationsList=new NotificationsList(idd, text, name, time,image, postID, uID);
        mUsersList.add(notificationsList);

        ListViewNotifications mUsersAdapter = new ListViewNotifications(getActivity(), mUsersList);
        mLikeUsersListView.setAdapter(mUsersAdapter);
        ListAdapter listAdapterrr = mLikeUsersListView.getAdapter();

        //Toast.makeText(getContext(), Integer.toString(listAdapterrr.getCount()), Toast.LENGTH_SHORT).show();
        if (listAdapterrr == null) {
            return;
        } else
        {
            int totalHeight = 0;
            for (int i = 0; i < listAdapterrr.getCount(); i++) {
                View listItem = listAdapterrr.getView(i, null, mLikeUsersListView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mLikeUsersListView.getLayoutParams();
            params.height = totalHeight + (mLikeUsersListView.getDividerHeight() * (listAdapterrr.getCount() - 1));
            mLikeUsersListView.setLayoutParams(params);
            mLikeUsersListView.requestLayout();
        }
        //if(mLikeUsersListView.getCount()==totalNotifications) {
            displayUsers();
        //}
    }
}