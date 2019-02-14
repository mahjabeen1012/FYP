package com.childcareapp.pivak.fyplogin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewLike;
import com.childcareapp.pivak.fyplogin.Models.LikeUsersList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewLikedUsers extends AppCompatDialogFragment {

    String postId,uName;
    View view;
    ProgressBar progressBar;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.activity_view_liked_users, null);
        postId = getArguments().getString("postId");
        uName = getArguments().getString("uName");
        progressBar = view.findViewById(R.id.show_like_users);
        progressBar.setVisibility(View.VISIBLE);
        builder.setView(view).setTitle("All Likes");
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.darkGrey));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkGrey));
            }
        });
        dialog.show();
        setListviewLikes();

        return dialog;
    }
    public void setListviewLikes()
    {

        FirebaseFirestore post = FirebaseFirestore.getInstance();
        CollectionReference doc =   post.collection("Newsfeedlike").document("posts").collection(postId);
        doc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                List<LikeUsersList> mUsersList = new ArrayList<>();
                LikeUsersList like;
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        if(document.exists())
                        {
                            like = document.toObject(LikeUsersList.class);
                            mUsersList.add(like);

                        }
                        else
                        {
                            //error getting documents
                        }
                    }
                    ListView mLikeUsersListView = view.findViewById(R.id.list_view_like);
                    ListViewLike mUsersAdapter = new ListViewLike(getActivity(), mUsersList);
                    mLikeUsersListView.setAdapter(mUsersAdapter);
                    ListAdapter listAdapterrr = mLikeUsersListView.getAdapter();

                    if (listAdapterrr == null)
                    {
                        return;
                    }
                    else
                    {
                        int totalHeight = 0;
                        for (int i = 0; i < listAdapterrr.getCount(); i++)
                        {
                            View listItem = listAdapterrr.getView(i, null, mLikeUsersListView);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                        }
                        ViewGroup.LayoutParams params = mLikeUsersListView.getLayoutParams();
                        params.height = totalHeight + (mLikeUsersListView.getDividerHeight() * (listAdapterrr.getCount() - 1));
                        mLikeUsersListView.setLayoutParams(params);
                        mLikeUsersListView.requestLayout();
                    }

                    progressBar.setVisibility(View.GONE);
                }
                else
                {

                }
            } });
    }

}