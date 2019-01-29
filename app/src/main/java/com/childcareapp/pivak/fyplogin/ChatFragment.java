package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.Models.ChatDataModel;
import com.childcareapp.pivak.fyplogin.Models.CompanyModel;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.childcareapp.pivak.fyplogin.RecyclerviewAdapters.ChatAppMsgAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class ChatFragment extends Fragment {
    View view;
    EditText msgInputText;
    RecyclerView msgRecyclerView;
    ChatAppMsgAdapter chatAppMsgAdapter;
    List<ChatDataModel> msgDtoList;
    String uName,user,userType,secondUserType;
    String photo;
    FirebaseFirestore myStore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.chat_fragment, container, false);

        String name = "name";
        uName=getArguments().getString("uName");
        user=getArguments().getString("secondUser");
        userType=getArguments().getString("userType");
        secondUserType= getArguments().getString("secondUserType");
        photo= getArguments().getString("image");
        myStore=FirebaseFirestore.getInstance();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(name);


        loadPreviousChat();

        msgInputText = view.findViewById(R.id.chat_input_msg);
        Button msgSendButton = view.findViewById(R.id.chat_send_msg);
        msgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msgContent = msgInputText.getText().toString();
                if(!TextUtils.isEmpty(msgContent))
                {
                    sendMessage(msgContent);
                    updateDatabase(msgContent);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getArguments().getString("name"));

    }

    public void loadPreviousChat()
    {
        //Get RecyclerView object.
        msgRecyclerView = view.findViewById(R.id.chat_recycler_view);

        // Set RecyclerView layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        // Create the initial data list.
        msgDtoList = new ArrayList<ChatDataModel>();

        // Create the data adapter with above data list.
        chatAppMsgAdapter = new ChatAppMsgAdapter(msgDtoList);

        // Set data adapter to RecyclerView.
        msgRecyclerView.setAdapter(chatAppMsgAdapter);
        myStore.collection("Users").document(userType)
                .collection(uName).document("Messages").collection(user)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                        @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null){
                            msgDtoList.clear();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                ChatDataModel data=doc.toObject(ChatDataModel.class);
                                if(data.getStatus().equals("received") && data.getReadStatus().equals("unread"))
                                {
                                    updateReadStatus(doc.getId());
                                }
                                // Add a new sent message to the list.
                                addMessagetoList(data.getMessage().toString(),data.getStatus().toString());
                            }
                        }
                    }

                });

    }
    public void addMessagetoList(String message, String status)
    {
        if(status.equals("sent")) {
            ChatDataModel msgDto = new ChatDataModel("Sent", message,null);
            msgDtoList.add(msgDto);
        }
        else if(status.equals("received"))
        {
            ChatDataModel msgDto = new ChatDataModel("Received", message, photo);
            msgDtoList.add(msgDto);
        }

        int newMsgPosition = msgDtoList.size() - 1;

        // Notify recycler view insert one new data.
        //chatAppMsgAdapter.notifyItemInserted(newMsgPosition);

        // Scroll RecyclerView to the last message.
        msgRecyclerView.scrollToPosition(newMsgPosition);
        chatAppMsgAdapter.notifyDataSetChanged();
    }
    public void sendMessage(String msgContent)
    {
        // Add a new sent message to the list.
        ChatDataModel msgDto = new ChatDataModel("Sent", msgContent,null);
        msgDtoList.add(msgDto);
        int newMsgPosition = msgDtoList.size() - 1;
        // Scroll RecyclerView to the last message.
        msgRecyclerView.scrollToPosition(newMsgPosition);
        chatAppMsgAdapter.notifyDataSetChanged();
        // Empty the input edit text box.
        msgInputText.setText("");
    }
    public void updateDatabase(String msgContent)
    {
        //updateSenderChatRoom(msgContent);
        updateReceiverChatRoom(msgContent);
    }
    public void updateSenderChatRoom(final String msgContent)
    {
                        //// check whether chatroom of this user exist or not
                        final String finalName = getArguments().getString("name");
                        //// check whether chatroom of this user exist or not
                        myStore.collection("Users").document(userType).collection(uName)
                                .document("Messages").collection(user)
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                if (documentSnapshots.isEmpty())
                                {
                                    ///// Store new user's username in messages's field
                                    updateUsersList(uName, user,userType);
                                    //// Store data to FireStore
                                    //loadPhoto(uName, userType, secondUserType,finalName,msgContent,"sent","", user);
                                    final ChatDataModel data = new ChatDataModel(finalName, msgContent,
                                            "sent","", photo);
                                    storeSenderDataTOFirestore(data, uName, user,userType);
                                    return;
                                }
                                else
                                {
                                    //// Store data to FireStore
                                    // loadPhoto(uName, userType, secondUserType,finalName,msgContent,"sent","", user);
                                    final ChatDataModel data = new ChatDataModel(finalName, msgContent,
                                            "sent","", photo);
                                    storeSenderDataTOFirestore(data, uName, user,userType);
                                    return;
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "onFailure: Failed to get Image");

                            }
                        });
        /////////////
    }
//    public void updateReceiverChatRoom(final String msgContent)
//    {
//        //// get sender's name
//        myStore.collection("Users").document(userType).collection(uName)
//                .document("Profile").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful())
//                {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists())
//                    {
//                        UserModel userData=null;
//                        CompanyModel companyData=null;
//                        String name=null;
//                        if(userType.equals("Company"))
//                        {
//                            companyData=document.toObject(CompanyModel.class);
//                            name=companyData.getName();
//                        }
//                        else if(userType.equals("Student"))
//                        {
//                            userData=document.toObject(UserModel.class);
//                            name=userData.getfName()+" "+userData.getlName();
//                        }
//                        //// check whether chatroom of this user exist or not
//                        final String finalName = name;
//                        myStore.collection("Users").document(secondUserType).collection(user)
//                                .document("Messages").collection(uName)
//                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                            @Override
//                            public void onSuccess(QuerySnapshot documentSnapshots) {
//                                if (documentSnapshots.isEmpty())
//                                {
//                                    ///// Store new user's username in messages's field
//                                    updateUsersList(user, uName,secondUserType);
//                                    //// Store data to FireStore
//                                    loadPhoto(user, secondUserType,userType, finalName,msgContent, "received", "unread", uName);
////                                    final ChatDataModel data = new ChatDataModel(finalName, msgContent,
////                                            "received","unread", photo);
////                                    storeDataTOFirestore(data, user, uName,secondUserType);
//                                    return;
//                                }
//                                else
//                                {
//                                    //// Store data to FireStore
//                                    loadPhoto(user, secondUserType,userType, finalName,msgContent, "received", "unread", uName);
////                                    final ChatDataModel data = new ChatDataModel(finalName, msgContent,
////                                            "received","unread", photo);
////                                    storeDataTOFirestore(data, user, uName,secondUserType);
//                                    return;
//                                }
//
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.i(TAG, "onFailure: Failed to get Image");
//
//                            }
//                        });
//                        //// end chatroom
//                    }
//                    else
//                    {
//                        Log.i(TAG, "onComplete: Image doesn't exist");
//                    }
//                }
//                else
//                {
//                    Log.i(TAG, "onComplete: Failed to get Image");
//                }
//            }
//        });
//        /////////////
//
//
//
//    }

    public void updateReceiverChatRoom(final String msgContent)
    {
                        final String finalName = getArguments().getString("senderName");
                        myStore.collection("Users").document(secondUserType).collection(user)
                                .document("Messages").collection(uName)
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                if (documentSnapshots.isEmpty())
                                {
                                    ///// Store new user's username in messages's field
                                    updateUsersList(user, uName,secondUserType);
                                    //// Store data to FireStore
                                    //loadPhoto(user, secondUserType,userType, finalName,msgContent, "received", "unread", uName);
                                    final ChatDataModel data = new ChatDataModel(finalName, msgContent,
                                            "received","unread", getArguments().getString("senderPhoto"));
                                    storeReceiverDataTOFirestore(data, user, uName,secondUserType);
                                    return;
                                }
                                else
                                {
                                    //// Store data to FireStore
                                    //loadPhoto(user, secondUserType,userType, finalName,msgContent, "received", "unread", uName);
                                    final ChatDataModel data = new ChatDataModel(finalName, msgContent,
                                            "received","unread", getArguments().getString("senderPhoto"));
                                    storeReceiverDataTOFirestore(data, user, uName,secondUserType);
                                    return;
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "onFailure: Failed to get Image");

                            }
                        });
                        //// end chatroom

    }
    public void updateReadStatus(String id)
    {
        Map<String, Object> data1 = new HashMap<>();
        data1.put("readStatus","read");
        myStore.collection("Users").document(userType)
                .collection(uName).document("Messages").collection(user).document(id).update(data1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    public void updateUsersList(final String userName, final String secondUser,final String TypeUser)
    {
        // getPrevious Users

        myStore.collection("Users").document(TypeUser)
                .collection(userName).document("Messages").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        String finalUserList="";
                        ///////////////
                        String users=document.getData().get("users").toString();
                        if(!users.equals("")) {
                            finalUserList = users + "," + secondUser;
                        }

                        else
                        {
                            finalUserList=","+ secondUser;
                        }
                        Map<String, Object> data = new HashMap<>();
                        data.put("users",finalUserList);
                        myStore.collection("Users").document(TypeUser)
                                .collection(userName).document("Messages").update(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                        //////////
                    }
                    else
                    {
                        Map<String, Object> data = new HashMap<>();
                        data.put("users",","+secondUser);
                        myStore.collection("Users").document(TypeUser)
                                .collection(userName).document("Messages").set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                }
                else
                {
                    Log.i(TAG, "Failed to get data");
                }
            }
        });

    }

    public void storeReceiverDataTOFirestore(final ChatDataModel data, String firstUser, String secondUser, String typeUser)
    {
        //Toast.makeText(getActivity(), "message: "+ data.getMessage(), Toast.LENGTH_SHORT).show();
        myStore.collection("Users").document(typeUser).collection(firstUser)
                .document("Messages").collection(secondUser).add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference aVoid) {
                updateSenderChatRoom(data.getMessage());
                Log.i(TAG, "onSuccess: Message Sent");
                return;
            }

        });
    }

    public void storeSenderDataTOFirestore(ChatDataModel data, String firstUser, String secondUser,String typeUser)
    {
        //Toast.makeText(getActivity(), "message: "+ data.getMessage(), Toast.LENGTH_SHORT).show();
        myStore.collection("Users").document(typeUser).collection(firstUser)
                .document("Messages").collection(secondUser).add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override

            public void onSuccess(DocumentReference aVoid) {
                Log.i(TAG, "onSuccess: Message Sent");
                return;
            }
        });
    }

//    public void loadPhoto(final String userName, final String firstTypeUser, String secondTypeUser, final String name, final String msg,
//                          final String msgType, final String status, final String secondUser)
//    {
//
//        myStore.collection("Users").document(secondTypeUser).collection(secondUser)
//                .document("Profile").collection("Image")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
//                                        @javax.annotation.Nullable FirebaseFirestoreException e) {
//                        if (e != null) {
//                            Log.e(TAG, "onEvent: Listen failed.", e);
//                            return;
//                        }
//
//                        if (queryDocumentSnapshots != null) {
//
//                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                                Images imag = doc.toObject(Images.class);
//                                URL url = null;
//                                try {
//                                    url = new URL(imag.getUrl());
//                                } catch (MalformedURLException e1) {
//                                    e1.printStackTrace();
//                                }
//                                //Picasso.get().load(url.toString()).into(img);
//                                final ChatDataModel chatData = new ChatDataModel(name, msg, msgType,status,url.toString());
//                                storeReceiverDataTOFirestore(chatData, userName, secondUser,firstTypeUser);
//                                return;
//                            }
//                        }
//                        else
//                        {
//                            Log.i(TAG, "onEvent: Empty List");
//                        }
//                    }});
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        msgRecyclerView.onFinishTemporaryDetach();
        msgRecyclerView.onChildDetachedFromWindow(view);
        msgRecyclerView.setAdapter(null);
    }

}
