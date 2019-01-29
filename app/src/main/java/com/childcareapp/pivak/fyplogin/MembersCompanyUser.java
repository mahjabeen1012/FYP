package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewUsers;
import com.childcareapp.pivak.fyplogin.Models.CompanyModel;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class MembersCompanyUser extends Fragment implements View.OnClickListener{

    SearchView companySearchview;
    List<UserModel> userList=new ArrayList<>();
    List<String> images=new ArrayList<>();
    ListView listView;
    int totalCompanies=0;
    Bundle bundle=new Bundle();
    ProgressBar progressBar;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.member_company_user, container, false);
        progressBar=view.findViewById(R.id.companySearchProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        companySearchview=view.findViewById(R.id.search_view_company);
        listView = view.findViewById(R.id.searchComapnyListview);
        displayCompanies();
        companySearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUser();
                //displayUsers();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Search a Company");
    }


    public void searchUser()
    {
        FirebaseFirestore.getInstance().collection("Users")
                .document("Company").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        userList.clear();
                        images.clear();
                        userList=new ArrayList<>();
                        listView.setAdapter(null);
                        totalCompanies=0;
                        String[] users=document.getData().get("users").toString().split(",");
                        for (int a = 1; a < users.length; a++) {
                            checkUser(users[a]);
                        }
                    }
                    else
                    {
                        Log.i(TAG, "onComplete: Image doesn't exist");
                        return;
                    }
                }
                else
                {
                    Log.i(TAG, "onComplete: Failed to get Image");
                }
            }
        });
    }

    public void displayCompanies()
    {
        FirebaseFirestore.getInstance().collection("Users")
                .document("Company").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        userList.clear();
                        images.clear();
                        listView.setAdapter(null);

                        final String[] users=document.getData().get("users").toString().split(",");
                        totalCompanies= (users.length-1);
                        for (int a = 1; a < users.length; a++) {
                            //checkUser(users[a]);
                            final int finalA = a;
                            FirebaseFirestore.getInstance().collection("Users").document("Company")
                                    .collection(users[a]).document("Profile").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document2 = task.getResult();
                                        if (document2.exists())
                                        {
                                            CompanyModel data=document2.toObject(CompanyModel.class);
                                            loadImage(users[finalA], data.getName(),"");
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
                        }
                    }
                    else
                    {
                        Log.i(TAG, "onComplete: Image doesn't exist");
                        return;
                    }
                }
                else
                {
                    Log.i(TAG, "onComplete: Failed to get Image");
                }
            }
        });
    }

    public void checkUser(final String user)
    {
        DocumentReference Messages = FirebaseFirestore.getInstance().collection("Users").document("Company")
                .collection(user).document("Profile");
        Messages.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        CompanyModel data=document.toObject(CompanyModel.class);
                        if(data.getName().equalsIgnoreCase(companySearchview.getQuery().toString()))
                        {
                            totalCompanies++;
                            loadImage(user, data.getName(),"search");
                        }
//                        else
//                        {
//                            Toast.makeText(getActivity(), "User doesn't exist", Toast.LENGTH_SHORT).show();
//                        }
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


    }

    public void loadImage(final String user, final String name, final String type)
    {
        FirebaseFirestore.getInstance().collection("Users").document("Company").collection(user)
                .document("Profile").collection("Image")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                        @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                Images imag = doc.toObject(Images.class);
                                URL url = null;
                                try {
                                    url = new URL(imag.getUrl());
                                } catch (MalformedURLException e1) {
                                    e1.printStackTrace();
                                }
                                setListView(user,name, url, type);
                                return;
                            }
                            String urlString="https://firebasestorage.googleapis.com/v0/b/fastquadconnectify.appspot.com/o/Images%2FprofileImage.jpg?alt=media&token=6b9d666b-c591-4b74-a6ac-ea7961b07158";
                            URL url= null;
                            try {
                                url = new URL(urlString);
                            } catch (MalformedURLException e1) {
                                e1.printStackTrace();
                            }
                            setListView(user,name, url, type);
                            return;
                        }
                        else
                        {
                            String urlString="https://firebasestorage.googleapis.com/v0/b/fastquadconnectify.appspot.com/o/Images%2FprofileImage.jpg?alt=media&token=6b9d666b-c591-4b74-a6ac-ea7961b07158";
                            URL url= null;
                            try {
                                url = new URL(urlString);
                            } catch (MalformedURLException e1) {
                                e1.printStackTrace();
                            }
                            setListView(user,name, url,type);
                            return;
                        }
                    }});
    }

    public void setListView(final String user, String name, URL url, final String type)
    {
        listView.setAdapter(null);
        UserModel data = new UserModel(url, name, "",user);
        userList.add(data);
        ListViewUsers list = new ListViewUsers(getActivity(), R.layout.listview_users, userList);
        listView.setAdapter(list);
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            return;
        }
        else
        {
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++)
            {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
            images.add(url.toString());
        }
        if(listView.getCount()==totalCompanies) {
            displayUsers(type);
        }
    }
    public void listviewListener(final String type)
    {
        if(listView.getCount()>0) {
            for (int i = 0; i <= listView.getLastVisiblePosition() - listView.getFirstVisiblePosition(); i++) {
                final View view1 = listView.getChildAt(i);
                final TextView namee=view1.findViewById(R.id.searchUserName);
                final TextView idd=(TextView) view1.findViewById(R.id.searchUserID);
                final ImageView profileImage=(ImageView) view1.findViewById(R.id.searchUserImage);
                final  Button messageBtn=(Button) view1.findViewById(R.id.searchMessageBtn);


                final int finalI1 = i;
                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        // Any UI task
                        if(type.equals("search"))
                        {
                            if(finalI1==0) {
                                namee.setVisibility(view1.VISIBLE);
                                messageBtn.setVisibility(view1.VISIBLE);
                                profileImage.setVisibility(view1.VISIBLE);
                                idd.setVisibility(view1.VISIBLE);
                            }
                            else
                            {
                                namee.setVisibility(view1.INVISIBLE);
                                messageBtn.setVisibility(view1.INVISIBLE);
                                profileImage.setVisibility(view1.INVISIBLE);
                                idd.setVisibility(view1.INVISIBLE);
                            }
                        }
                        else
                        {
                            if (getArguments().getString("user").equals(idd.getText().toString())) {
                                messageBtn.setVisibility(view1.INVISIBLE);
                            } else if (type.equals("search") && finalI1 == 1) {
                                messageBtn.setVisibility(view1.INVISIBLE);
                            } else {
                                messageBtn.setVisibility(view1.VISIBLE);
                            }
                        }
                    }
                };
                handler.sendEmptyMessage(1);

//
//                if(type.equals("search")) {
//                    Handler handler1 = new Handler(Looper.getMainLooper()) {
//                        @Override
//                        public void handleMessage(Message msg) {
//                            // Any UI task
//
//                            if(finalI1==0) {
//                                namee.setVisibility(view1.VISIBLE);
//                                messageBtn.setVisibility(view1.VISIBLE);
//                                profileImage.setVisibility(view1.VISIBLE);
//                                idd.setVisibility(view1.VISIBLE);
//                            }
//                            else
//                            {
//                                namee.setVisibility(view1.INVISIBLE);
//                                messageBtn.setVisibility(view1.INVISIBLE);
//                                profileImage.setVisibility(view1.INVISIBLE);
//                                idd.setVisibility(view1.INVISIBLE);
//                            }
//                        }
//                    };
//                    handler1.sendEmptyMessage(1);
//                }



                profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle1=new Bundle();
                        bundle1.putString("user",idd.getText().toString());
                        bundle1.putString("status",getArguments().getString("status"));
                        bundle1.putString("profileType", "search");
                        if(getArguments().getString("user").equals(idd.getText().toString())) {
                            bundle1.putString("profileType","myProfile");
                        }
                        else {
                            bundle1.putString("profileType", "search");
                        }

                        MyProfileCompany myProfileUser = new MyProfileCompany();
                        myProfileUser.setArguments(bundle1);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.addToBackStack(null);
                        if(getArguments().getString("status").equals("Student")) {
                            fragmentTransaction.replace(R.id.content_student_home, myProfileUser);
                        }
                        else if(getArguments().getString("status").equals("Alumni"))
                        {
                            fragmentTransaction.replace(R.id.content_alumni_home, myProfileUser);
                        }
                        else if(getArguments().getString("status").equals("Faculty"))
                        {
                            fragmentTransaction.replace(R.id.content_faculty_home, myProfileUser);
                        }
                        else if(getArguments().getString("status").equals("Company"))
                        {
                            fragmentTransaction.replace(R.id.content_company_home, myProfileUser);
                        }
                        fragmentTransaction.commit();
                    }
                });

                final int finalI = i;
                messageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bundle.putString("image", images.get(finalI));
                        bundle.putString("uName",getArguments().getString("user"));
                        bundle.putString("status",getArguments().getString("status"));
                        bundle.putString("name",namee.getText().toString());
                        bundle.putString("secondUser", idd.getText().toString());
                        bundle.putString("senderName", getArguments().getString("senderName"));
                        bundle.putString("senderPhoto", getArguments().getString("senderPhoto"));
                        getSecondUserType(idd.getText().toString());
                    }
                });
            }
        }
    }
    public void loadChatFragment()
    {
        ChatFragment chatFragment = new ChatFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        if(getArguments().getString("status").equals("Student")) {
            bundle.putString("userType","Student");
            chatFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_student_home, chatFragment);
        }
        else if(getArguments().getString("status").equals("Alumni"))
        {
            bundle.putString("userType","Student");
            chatFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_alumni_home, chatFragment);
        }
        else if(getArguments().getString("status").equals("Faculty"))
        {
            bundle.putString("userType","Student");
            chatFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_faculty_home, chatFragment);
        }
        else if(getArguments().getString("status").equals("Company"))
        {
            bundle.putString("userType","Company");
            chatFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_company_home, chatFragment);
        }
        fragmentTransaction.commit();
    }
    public void getSecondUserType(final String userName)
    {
        /// get 2nd user type
        FirebaseFirestore.getInstance().collection("Users").document("Company").collection(userName)
                .document("Profile").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        bundle.putString("secondUserType","Company");
                        loadChatFragment();
                    }
                    else
                    {
                        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(userName)
                                .document("Profile").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists())
                                    {
                                        bundle.putString("secondUserType","Student");
                                        loadChatFragment();
                                    }
                                    else
                                    {

                                    }
                                }
                                else
                                {
                                    Log.i(TAG, "onComplete: Failed to get Image");
                                }
                            }
                        });
                    }
                }
                else
                {
                    Log.i(TAG, "onComplete: Failed to get Image");
                }
            }
        });
        // end of nested query



    }

    public void displayUsers(final String type)
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (listView.getChildAt(totalCompanies-1) == null ||
                        listView.getChildAt(totalCompanies-1) .equals("") ) { // your conditions
                }
                progressBar.setVisibility(View.INVISIBLE);
                listviewListener(type); // your task to execute
            }
        };
        new Thread(runnable).start();
    }
    @Override
    public void onClick(View v) {

    }
}
