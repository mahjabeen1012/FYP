package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ViewProfileUser extends Fragment {
    RecyclerView recyclerView;
    List<NewsFeedList> newsFeedList;
    NewsFeedRecyclerView newsFeedRecyclerView;
    String uName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.view_profile_user, container, false);

        uName = getArguments().getString("user");
        recyclerView = view.findViewById(R.id.myPosts);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        newsFeedList = new ArrayList<NewsFeedList>();
        newsFeedRecyclerView= new NewsFeedRecyclerView(newsFeedList,getActivity(),uName);
        recyclerView.setAdapter(newsFeedRecyclerView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button back = getActivity().findViewById(R.id.backProfileUser);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getFragmentManager();
                fm.popBackStack (null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });

        Button refreshData = getActivity().findViewById(R.id.myPostsRefresh);
        refreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPosts();
            }
        });
        FirebaseFirestore  post = FirebaseFirestore.getInstance();
        CollectionReference docRef =   post.collection("NewsFeed");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {

                    for (QueryDocumentSnapshot document : task.getResult())
                    {

                        Toast.makeText(getContext(), uName, Toast.LENGTH_SHORT).show();
                        ModelNewsFeed newsFeed=document.toObject(ModelNewsFeed.class);
                        if(newsFeed.getUploaderId().equals(uName))
                        {
                            //Toast.makeText(getContext(), newsFeed.getUploaderId(), Toast.LENGTH_SHORT).show();
                            NewsFeedList newslist = new NewsFeedList(newsFeed.getContent(), newsFeed.getUploaderId(),newsFeed.getDownloadUrl(),newsFeed.getUploaderName(),newsFeed.getFiletype(),newsFeed.getTimeStamp(),newsFeed.getFileName(),newsFeed.getPostId(),newsFeed.getLikes(),newsFeed.getTimeInMillis());
                            newsFeedList.add(newslist);
                        }
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
    public void refreshPosts()
    {
        final int size = newsFeedList.size();
        newsFeedList.clear();
        newsFeedRecyclerView.notifyItemRangeRemoved(0, size);
        //check1();
        checkfeed();
        //Toast.makeText(getContext(), "New Posts", Toast.LENGTH_SHORT).show();
    }
}
