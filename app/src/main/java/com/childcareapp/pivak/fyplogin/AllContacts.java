package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewChat;
import com.childcareapp.pivak.fyplogin.Models.ChatDataModel;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class AllContacts extends Fragment {
    View view;
    ListView listView;
    ArrayList<ChatDataModel> dataModels;
    String uName,userType,status;
    List<String> sortedUserIDs=new ArrayList<>();
    List<String> userIDs=new ArrayList<>();
    List<String> usernames=new ArrayList<>();
    List<String> lastMessage=new ArrayList<>();
    List<String> readStatus=new ArrayList<>();
    List<String> images=new ArrayList<>();
    List<Date> timestamps=new ArrayList<>();
    List<String> imagesURL=new ArrayList<>();
    int totalUsers=0;
    Bundle bundle=new Bundle();
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.all_contacts, container, false);

        uName=getArguments().getString("user");
        userType=getArguments().getString("userType");
        status=getArguments().getString("status");
        progressBar=view.findViewById(R.id.contactsProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance().collection("Users").document(userType).collection(uName)
                .document("Messages").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        String[] users=document.getData().get("users").toString().split(",");
                        totalUsers=(users.length-1);
                        sortedUserIDs.clear();
                        userIDs.clear();
                        usernames.clear();
                        timestamps.clear();
                        lastMessage.clear();
                        readStatus.clear();
                        images.clear();
                        for (int a = 1; a < users.length; a++) {
                            getData(users[a],a);
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

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                sortAndDisplayChatRooms();
//            }
//        }, 4000);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Messages");
    }

    public void setListView(final String userID, String image, String name, String message, String readStatus, Date timeStamp)
    {
        ListViewChat adapter;
        listView = view.findViewById(R.id.addUsersInChat);
        dataModels.add(new ChatDataModel(name, message, getTime(timeStamp),readStatus,image));
        adapter= new ListViewChat(view.getContext(),R.layout.listview_chat, dataModels);
        listView.setAdapter(adapter);
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        { }
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
            sortedUserIDs.add(userID);
            imagesURL.add(image);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                View cView=listView.getChildAt(position);
                TextView name=cView.findViewById(R.id.chatNameUser);
                bundle.putString("image", imagesURL.get(position));
                bundle.putString("uName",uName);
                bundle.putString("status",status);
                bundle.putString("name",name.getText().toString());
                bundle.putString("secondUser", sortedUserIDs.get(position));
                getSecondUserType(sortedUserIDs.get(position));
            }
        });
    }

    public void getData(final String user, final int index)
    {
        FirebaseFirestore.getInstance().collection("Users").document(userType)
                .collection(uName).document("Messages").collection(user)
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                        @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null){

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                ChatDataModel data=doc.toObject(ChatDataModel.class);
                                userIDs.add(user);
                                timestamps.add(data.getTimestamp());
                                usernames.add( data.getName());
                                lastMessage.add(data.getMessage());
                                readStatus.add(data.getReadStatus());
                                images.add(data.getImage());
                                if (totalUsers==index)
                                {
                                   loadListView();
                                }
                            }
                        }
                    }
                });

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
    }

    public void loadChatFragment()
    {

        bundle.putString("senderName", getArguments().getString("senderName"));
        bundle.putString("senderPhoto", getArguments().getString("senderPhoto"));
        ChatFragment chatFragment = new ChatFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        if(status.equals("Student")) {
            bundle.putString("userType","Student");
            chatFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_student_home, chatFragment);
        }
        else if(status.equals("Alumni"))
        {
            bundle.putString("userType","Student");
            chatFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_alumni_home, chatFragment);
        }
        else if(status.equals("Faculty"))
        {
            bundle.putString("userType","Student");
            chatFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_faculty_home, chatFragment);
        }
        else if(status.equals("Company"))
        {
            bundle.putString("userType","Company");
            chatFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_company_home, chatFragment);
        }
        fragmentTransaction.commit();
    }

    public void sortAndDisplayChatRooms()
    {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // Any UI task

        for(int i=0;i<=timestamps.size();i++)
        {
            for(int a=0; a<timestamps.size(); a++) {
                if ((a + 1) != timestamps.size()) {
                    if (timestamps.get(a).before(timestamps.get(a + 1))) {
                        Date temp = timestamps.get(a);
                        timestamps.set(a, timestamps.get(a + 1));
                        timestamps.set(a + 1, temp);

                        String temp1 = userIDs.get(a);
                        userIDs.set(a, userIDs.get(a + 1));
                        userIDs.set(a + 1, temp1);

                        temp1 = usernames.get(a);
                        usernames.set(a, usernames.get(a + 1));
                        usernames.set(a + 1, temp1);

                        temp1 = lastMessage.get(a);
                        lastMessage.set(a, lastMessage.get(a + 1));
                        lastMessage.set(a + 1, temp1);

                        temp1 = readStatus.get(a);
                        readStatus.set(a, readStatus.get(a + 1));
                        readStatus.set(a + 1, temp1);

                        String temp2 = images.get(a);
                        images.set(a, images.get(a + 1));
                        images.set(a + 1, temp2);
                    }
                }
            }
        }
        dataModels=new ArrayList<>();
        for(int a=0;a<userIDs.size();a++)
        {
            setListView(userIDs.get(a),images.get(a), usernames.get(a), lastMessage.get(a), readStatus.get(a), timestamps.get(a));
        }

            }
        };
        handler.sendEmptyMessage(1);
    }

    public void loadListView()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (timestamps.size()!= totalUsers || userIDs.size()!=totalUsers || usernames.size()!=totalUsers ||
                        lastMessage.size()!=totalUsers || readStatus.size()!=totalUsers || images.size()!=totalUsers) { // your conditions
                }
                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        // Any UI task
                        progressBar.setVisibility(View.GONE);
                        sortAndDisplayChatRooms();
                    }
                };
                handler.sendEmptyMessage(1);

                 // your task to execute
            }
        };
        new Thread(runnable).start();
    }

    public String getTime(Date timeStamp)
    {
        TimeAgo timeAgo = new TimeAgo();
        long time=timeStamp.getTime();
        String timee="";
        if(timeAgo.getTimeAgo(time).equals("time"))
        {
            timee=new SimpleDateFormat("K:mm a").format(timeStamp);
            return timee;
        }
        else
        {
            timee=timeAgo.getTimeAgo(time);
            return timee;
        }
    }

    public class TimeAgo
    {
        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

        public TimeAgo()
        {}
        public String getTimeAgo(long time) {
            if (time < 1000000000000L) {
                time *= 1000;
            }

            long now = System.currentTimeMillis();
            if (time > now || time <= 0) {
                return null;
            }


            final long diff = now - time;
//            if (diff < MINUTE_MILLIS) {
//                return "just now";
//            } else if (diff < 2 * MINUTE_MILLIS) {
//                return "a minute ago";
//            } else if (diff < 50 * MINUTE_MILLIS) {
//                return diff / MINUTE_MILLIS + " minutes ago";
//            } else if (diff < 90 * MINUTE_MILLIS) {
//                return "an hour ago";
//            }

            if (diff < 24 * HOUR_MILLIS)
            {
                //return diff / HOUR_MILLIS + " hours ago";
                return "time";
            }
            else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            }
            else {
                return diff / DAY_MILLIS + " days ago";
            }
        }
    }

}
