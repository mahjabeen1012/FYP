package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SearchActivity extends Fragment implements View.OnClickListener {
    String loggedInUserID, userName, batch, degree, descipline, campus;
    List<UserModel> userList=new ArrayList<>();
    List<String> images=new ArrayList<>();
    ListView listView;
    Bundle bundle2=new Bundle();
    int totalUsers=0;
    ProgressBar progressBar;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_search, container, false);

        loggedInUserID=getArguments().getString("loggedInUserID");
        userName=getArguments().getString("name");

        batch=getArguments().getString("batch");
        degree=getArguments().getString("degree");
        descipline=getArguments().getString("discipline");
        campus=getArguments().getString("campus");
        listView = view.findViewById(R.id.listviewUsers);
        progressBar=view.findViewById(R.id.searchUsersProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        searchUser();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Members");
    }


    public void searchUser()
    {
        FirebaseFirestore.getInstance().collection("Users")
                .document("Student").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        String[] users=document.getData().get("users").toString().split(",");
                        listView.setAdapter(null);
                        userList.clear();
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
    public void setListView(final String user, String fName, String lName, URL url)
    {
        UserModel data = new UserModel(url, fName, lName,user);
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
            totalUsers++;
        }
        if(listView.getCount()==totalUsers) {
            displayUsers();
        }
    }

    public void checkUser(final String user)
    {
        DocumentReference Messages = FirebaseFirestore.getInstance().collection("Users").document("Student")
                .collection(user).document("Profile");
        Messages.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        UserModel data=document.toObject(UserModel.class);
                        if(searchCriteria(user, data).equals(false))
                        {
                            Toast.makeText(getActivity(), "User doesn't exist", Toast.LENGTH_SHORT).show();
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


    }

    public void loadImage(final String user, final String fName, final String lName)
    {
        FirebaseFirestore.getInstance().collection("Users").document("Student")
                .collection(user).document("Profile").collection("Image")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                {
                    String urlString="https://firebasestorage.googleapis.com/v0/b/fastquadconnectify.appspot.com/o/Images%2FprofileImage.jpg?alt=media&token=6b9d666b-c591-4b74-a6ac-ea7961b07158";
                    URL url= null;
                    try {
                        url = new URL(urlString);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    setListView(user,fName, lName, url);
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
                            DocumentReference imgRef = FirebaseFirestore.getInstance().collection("Users")
                                    .document("Student").collection(user).document("Profile")
                                    .collection("Image").document(idd);
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
                                                url=new URL(imag.getUrl());
                                            }
                                            catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            setListView(user ,fName, lName, url);
                                            return;
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

    public Boolean searchCriteria(String user, UserModel data)
    {
        String dataName=data.getfName()+" "+data.getlName();
        String dataDegree=data.getDegree();
        String dataBatch=data.getBatch();
        String dataCampus=data.getCampus();
        String dataDiscipline=data.getDiscipline();

        if(!userName.equals("") && !batch.equals("") && !degree.equals("") && !descipline.equals("") && !campus.equals(""))
        {
            if((userName.equalsIgnoreCase(dataName) || userName.equalsIgnoreCase(data.getfName()) || userName.equalsIgnoreCase(data.getlName())
            ) && batch.equalsIgnoreCase(dataBatch) && degree.equalsIgnoreCase(dataDegree) && campus.equalsIgnoreCase(dataCampus) &&
                    descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "all";
        }

        else if(!userName.equals("") && batch.equals("") && degree.equals("") && descipline.equals("") && campus.equals(""))
        {
            if(userName.equalsIgnoreCase(dataName) || userName.equalsIgnoreCase(data.getfName()) || userName.equalsIgnoreCase(data.getlName()))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "name";
        }
        else if(!userName.equals("") && !batch.equals("") && degree.equals("") && descipline.equals("") && campus.equals(""))
        {
            if((userName.equalsIgnoreCase(dataName) || userName.equalsIgnoreCase(data.getfName()) || userName.equalsIgnoreCase(data.getlName())
            ) && batch.equalsIgnoreCase(dataBatch))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "name,batch";
        }
        else if(!userName.equals("") && !batch.equals("") && !degree.equals("") && descipline.equals("") && campus.equals(""))
        {
            if((userName.equalsIgnoreCase(dataName) || userName.equalsIgnoreCase(data.getfName()) || userName.equalsIgnoreCase(data.getlName())
            ) && batch.equalsIgnoreCase(dataBatch) && degree.equalsIgnoreCase(dataDegree))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "name,batch,degree";
        }
        else if(!userName.equals("") && !batch.equals("") && !degree.equals("") && !descipline.equals("") && campus.equals(""))
        {
            if((userName.equalsIgnoreCase(dataName) || userName.equalsIgnoreCase(data.getfName()) || userName.equalsIgnoreCase(data.getlName())
            ) && batch.equalsIgnoreCase(dataBatch) && degree.equalsIgnoreCase(dataDegree) && descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "name,batch,degree,discipline";
        }
        else if(!userName.equals("") && batch.equals("") && !degree.equals("") && descipline.equals("") && campus.equals(""))
        {
            if((userName.equalsIgnoreCase(dataName) || userName.equalsIgnoreCase(data.getfName()) || userName.equalsIgnoreCase(data.getlName())
            ) && degree.equalsIgnoreCase(dataDegree))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "name,degree";
        }
        else if(!userName.equals("") && batch.equals("") && !degree.equals("") && !descipline.equals("") && campus.equals(""))
        {
            if((userName.equalsIgnoreCase(dataName) || userName.equalsIgnoreCase(data.getfName()) || userName.equalsIgnoreCase(data.getlName())
            ) && degree.equalsIgnoreCase(dataDegree) && descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "name,degree,discipline";
        }
        else if(!userName.equals("") && batch.equals("") && !degree.equals("") && !descipline.equals("") && !campus.equals(""))
        {
            if((userName.equalsIgnoreCase(dataName) || userName.equalsIgnoreCase(data.getfName()) || userName.equalsIgnoreCase(data.getlName())
            ) && degree.equalsIgnoreCase(dataDegree) && campus.equalsIgnoreCase(dataCampus) &&
                    descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "name,degree,discipline,campus";
        }
        else if(!userName.equals("") && batch.equals("") && degree.equals("") && !descipline.equals("") && campus.equals(""))
        {
            if((userName.equalsIgnoreCase(dataName) || userName.equalsIgnoreCase(data.getfName()) || userName.equalsIgnoreCase(data.getlName())
            ) && descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "name,discipline";
        }
        else if(!userName.equals("") && batch.equals("") && degree.equals("") && !descipline.equals("") && !campus.equals(""))
        {
            if((userName.equalsIgnoreCase(dataName) || userName.equalsIgnoreCase(data.getfName()) || userName.equalsIgnoreCase(data.getlName())
            ) && campus.equalsIgnoreCase(dataCampus) && descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "name,discipline,campus";
        }
        else if(!userName.equals("") && batch.equals("") && degree.equals("") && descipline.equals("") && !campus.equals(""))
        {
            if((userName.equalsIgnoreCase(dataName) || userName.equalsIgnoreCase(data.getfName()) || userName.equalsIgnoreCase(data.getlName())
            ) && campus.equalsIgnoreCase(dataCampus))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "name,campus";
        }
        ///////////////////

        else if(userName.equals("") && !batch.equals("") && degree.equals("") && descipline.equals("") && campus.equals(""))
        {
            if(batch.equalsIgnoreCase(dataBatch))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "batch";
        }
        else if(userName.equals("") && !batch.equals("") && !degree.equals("") && descipline.equals("") && campus.equals(""))
        {
            if(batch.equalsIgnoreCase(dataBatch) && degree.equalsIgnoreCase(dataDegree))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "batch,degree";
        }
        else if(userName.equals("") && !batch.equals("") && !degree.equals("") && !descipline.equals("") && campus.equals(""))
        {
            if(batch.equalsIgnoreCase(dataBatch) && degree.equalsIgnoreCase(dataDegree) && descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "batch,degree,discipline";
        }
        else if(userName.equals("") && !batch.equals("") && !degree.equals("") && !descipline.equals("") && !campus.equals(""))
        {
            if(batch.equalsIgnoreCase(dataBatch) && degree.equalsIgnoreCase(dataDegree) && campus.equalsIgnoreCase(dataCampus) &&
                    descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "batch,degree,discipline,campus";
        }
        else if(userName.equals("") && !batch.equals("") && degree.equals("") && !descipline.equals("") && campus.equals(""))
        {
            if(batch.equalsIgnoreCase(dataBatch) && descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "batch,discipline";
        }
        else if(userName.equals("") && !batch.equals("") && degree.equals("") && !descipline.equals("") && !campus.equals(""))
        {
            if(batch.equalsIgnoreCase(dataBatch) && campus.equalsIgnoreCase(dataCampus) && descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "batch,discipline,campus";
        }
        else if(userName.equals("") && !batch.equals("") && degree.equals("") && descipline.equals("") && !campus.equals(""))
        {
            if(batch.equalsIgnoreCase(dataBatch) && campus.equalsIgnoreCase(dataCampus))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "batch,campus";
        }
        ////////////
        else if(userName.equals("") && batch.equals("") && !degree.equals("") && descipline.equals("") && campus.equals(""))
        {
            if(degree.equalsIgnoreCase(dataDegree))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "degree";
        }
        else if(userName.equals("") && batch.equals("") && !degree.equals("") && !descipline.equals("") && campus.equals(""))
        {
            if(degree.equalsIgnoreCase(dataDegree) && descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "degree,discipline";
        }
        else if(userName.equals("") && batch.equals("") && !degree.equals("") && !descipline.equals("") && !campus.equals(""))
        {
            if(degree.equalsIgnoreCase(dataDegree) && campus.equalsIgnoreCase(dataCampus) &&
                    descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "degree,discipline,campus";
        }
        else if(userName.equals("") && batch.equals("") && !degree.equals("") && !descipline.equals("") && !campus.equals(""))
        {
            if(degree.equalsIgnoreCase(dataDegree) && campus.equalsIgnoreCase(dataCampus))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "degree,campus";
        }
        ///////////////

        else if(userName.equals("") && batch.equals("") && degree.equals("") && !descipline.equals("") && campus.equals(""))
        {
            if(descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "discipline";
        }
        else if(userName.equals("") && batch.equals("") && degree.equals("") && !descipline.equals("") && !campus.equals(""))
        {
            if(campus.equalsIgnoreCase(dataCampus) && descipline.equalsIgnoreCase(dataDiscipline))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "discipline,campus";
        }
        else if(userName.equals("") && batch.equals("") && degree.equals("") && descipline.equals("") && !campus.equals(""))
        {
            if(campus.equalsIgnoreCase(dataCampus))
            {
                loadImage(user, data.getfName(), data.getlName());
                return true;
            }
            //return "campus";
        }
        else {
            return false;
        }
        return true;
    }

    public void listviewListener()
    {
        if(listView.getCount()>0) {
            for (int i = 0; i <= listView.getLastVisiblePosition() - listView.getFirstVisiblePosition(); i++) {
                View view1 = listView.getChildAt(i);
                final TextView namee=view1.findViewById(R.id.searchUserName);
                final TextView idd=(TextView) view1.findViewById(R.id.searchUserID);
                final ImageView profileImage=(ImageView) view1.findViewById(R.id.searchUserImage);
                Button messageBtn=(Button) view1.findViewById(R.id.searchMessageBtn);


                if(loggedInUserID.equals(idd.getText().toString())) {
                    messageBtn.setVisibility(view1.INVISIBLE);
                }
                else{
                    messageBtn.setVisibility(view1.VISIBLE);
                }

                profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle=new Bundle();
                        bundle.putString("user",idd.getText().toString());
                        bundle.putString("status",getArguments().getString("status"));
                        bundle.putString("profileType", "search");
                        if(loggedInUserID.equals(idd.getText().toString())) {
                            bundle.putString("profileType","myProfile");
                        }
                        else {
                            bundle.putString("profileType", "search");
                        }
                        MyProfileUser myProfileUser = new MyProfileUser();
                        myProfileUser.setArguments(bundle);
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

//                        profileImage.buildDrawingCache();
//                        Bitmap bmap = profileImage.getDrawingCache();
//                        ByteArrayOutputStream stream=new ByteArrayOutputStream();
//                        bmap.compress(Bitmap.CompressFormat.PNG,100,stream);
//                        byte[] byteArray=stream.toByteArray();
                        bundle2.putString("uName",loggedInUserID);
                        bundle2.putString("status",getArguments().getString("status"));
                        bundle2.putString("name",namee.getText().toString());
                        bundle2.putString("secondUser", idd.getText().toString());
                        bundle2.putString("image", images.get(finalI));
                        bundle2.putString("senderName", getArguments().getString("senderName"));
                        bundle2.putString("senderPhoto", getArguments().getString("senderPhoto"));
                        getSecondUserType(idd.getText().toString());
                    }
                });
            }
        }
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
                        bundle2.putString("secondUserType","Company");
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
                                        bundle2.putString("secondUserType","Student");
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
                    Log.i(TAG, "onComplete: Failed to get document");
                }
            }
        });
        // end of nested query
    }


    public void loadChatFragment()
    {
        ChatFragment chatFragment = new ChatFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        if(getArguments().getString("status").equals("Student")) {
            bundle2.putString("userType","Student");
            chatFragment.setArguments(bundle2);
            fragmentTransaction.replace(R.id.content_student_home, chatFragment);
        }
        else if(getArguments().getString("status").equals("Alumni"))
        {
            bundle2.putString("userType","Student");
            chatFragment.setArguments(bundle2);
            fragmentTransaction.replace(R.id.content_alumni_home, chatFragment);
        }
        else if(getArguments().getString("status").equals("Faculty"))
        {
            bundle2.putString("userType","Student");
            chatFragment.setArguments(bundle2);
            fragmentTransaction.replace(R.id.content_faculty_home, chatFragment);
        }
        else if(getArguments().getString("status").equals("Company"))
        {
            bundle2.putString("userType","Company");
            chatFragment.setArguments(bundle2);
            fragmentTransaction.replace(R.id.content_company_home, chatFragment);
        }
        fragmentTransaction.commit();
    }

    public void displayUsers()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (listView.getChildAt(totalUsers-1) == null ||
                        listView.getChildAt(totalUsers-1) .equals("") ) { // your conditions
                }
                progressBar.setVisibility(View.INVISIBLE);
                listviewListener(); // your task to execute
            }
        };
        new Thread(runnable).start();
    }
    @Override
    public void onClick(View v) {

    }
}