package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.childcareapp.pivak.fyplogin.Dialogs.AddExperienceDialog;
import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewExperience;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.UserExperience;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ViewExperienceUser extends Fragment implements View.OnClickListener {

    Button addExperience,back ;
    String uName,profileType;
    List<UserExperience> experienceList;
    List<String> experienceObjects = new ArrayList<>();
    List<String> experienceDescriptionObjects = new ArrayList<>();
    ListView listExperience;
    int totalExperienceObjects=0;
    ProgressBar progressBar;
    View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.view_experience_user, container, false);
        addExperience = view.findViewById(R.id.addExperience);
        back = view.findViewById(R.id.backExperienceUser);
        back.setOnClickListener(this);
        addExperience.setOnClickListener(this);

        uName= getArguments().getString("user");
        profileType=getArguments().getString("profileType");
        progressBar=view.findViewById(R.id.experienceProgressBar);
        showProgressbar();

        if(profileType.equals("search"))
        {
            addExperience.setVisibility(View.INVISIBLE);
        }
        else if(profileType.equals("myProfile"))
        {
            addExperience.setVisibility(View.VISIBLE);
        }
        setListviewExperience();
        loadListView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setListExperience(String objID, String designation, String organization,String sDate, String eDate, String city
            , String country, String description)
    {
        UserExperience data = new UserExperience(designation, organization,country,city,sDate,eDate);
        experienceList.add(data);
        ListViewExperience list = new ListViewExperience(getActivity(), R.layout.listview_experience, experienceList);
        listExperience.setAdapter(list);
        ListAdapter listAdapter = listExperience.getAdapter();
        if (listAdapter == null)
        {
            return;
        }
        else
        {
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++)
            {
                View listItem = listAdapter.getView(i, null, listExperience);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listExperience.getLayoutParams();
            params.height = totalHeight + (listExperience.getDividerHeight() * (listAdapter.getCount() - 1));
            listExperience.setLayoutParams(params);
            listExperience.requestLayout();
            experienceObjects.add(objID);
            experienceDescriptionObjects.add(description);
        }
    }
    public void setListviewExperience()
    {
        experienceDescriptionObjects.clear();
        experienceObjects.clear();

        listExperience = view.findViewById(R.id.listViewExperience);
        listExperience.setAdapter(null);
        final FirebaseFirestore mStore=FirebaseFirestore.getInstance();
        mStore.collection("Users").document("Student").collection(uName).document("Profile")
                .collection("Experience").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                {
                    Log.i(TAG, "Experience: Empty List");
                    return;
                }
                else
                {
                    experienceList = new ArrayList<>();
                    totalExperienceObjects=documentSnapshots.size();
                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
                    {
                        if (documentSnapshot.exists())
                        {
                            final String idd=documentSnapshot.getId();
                            DocumentReference docRef = mStore.collection("Users").document("Student").collection(uName)
                                    .document("Profile").collection("Experience").document(idd);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists())
                                        {
                                            UserExperience studentExperience=document.toObject(UserExperience.class);
                                            setListExperience(idd, studentExperience.getDesignation(), studentExperience.getOrganization(),
                                                    studentExperience.getsDate(), studentExperience.geteDate(),
                                                    studentExperience.getCity(), studentExperience.getCountry(),
                                                    studentExperience.getDescription());
                                        }
                                        else {
                                            Log.i(TAG, "Experience: Document doesn't exist");
                                        }
                                    }
                                    else {
                                        Log.i(TAG, "Experience: Failed to get data");
                                    }
                                }
                            });
                        }
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "Experience: Failed to get data");
            }
        });
    }

    public void displayExperience()
    {
        experienceDescriptionObjects.clear();
        experienceObjects.clear()
        ;
        listExperience = view.findViewById(R.id.listViewExperience);
        listExperience.setAdapter(null);
        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName)
                .document("Profile").collection("Experience").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    experienceList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        UserExperience studentExperience=doc.toObject(UserExperience.class);
                        setListExperience(doc.getId(), studentExperience.getDesignation(), studentExperience.getOrganization(),
                                studentExperience.getsDate(), studentExperience.geteDate(),
                                studentExperience.getCity(), studentExperience.getCountry(),
                                studentExperience.getDescription());
                    }
                }
                else
                {
                    Log.i(TAG, "onEvent: Empty List");
                }
            }});
    }
    public void openAddExperienceDialog()
    {
        Bundle bundle2 = new Bundle();
        bundle2.putString("user", uName);
        bundle2.putString("status", "add");
        bundle2.putString("userStatus", getArguments().getString("userStatus"));
        AddExperienceDialog addExperienceDialog = new AddExperienceDialog();
        addExperienceDialog.setArguments(bundle2);
        addExperienceDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Add Experience");
    }
    public void  updateExperience()
    {
        if(listExperience.getCount()>0) {
            for (int i = 0; i <= listExperience.getLastVisiblePosition() - listExperience.getFirstVisiblePosition(); i++) {
                String s="";
                final View view1 = listExperience.getChildAt(i);
                final String expId=experienceObjects.get(i);
                final String description=experienceDescriptionObjects.get(i);
                final TextView expPosition=view1.findViewById(R.id.experiencePost);
                final TextView expCompany=(TextView) view1.findViewById(R.id.experienceCompany);
                final TextView expDuration=(TextView) view1.findViewById(R.id.experienceDuration);
                final TextView expLocation=(TextView) view1.findViewById(R.id.experienceLocation);

                final Button removeExperience= (Button) view1.findViewById(R.id.deleteExperience);
                final Button expEdit = (Button) view1.findViewById(R.id.editExperience);


                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        // Any UI task
                        if(profileType.equals("search"))
                        {
                            expEdit.setVisibility(view1.INVISIBLE);
                            removeExperience.setVisibility(view1.INVISIBLE);
                        }
                        else
                        {
                            expEdit.setVisibility(view1.VISIBLE);
                            removeExperience.setVisibility(view1.VISIBLE);
                        }
                    }
                };
                handler.sendEmptyMessage(1);
                expEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("user", uName);
                        bundle2.putString("status", "update");
                        bundle2.putString("post",expPosition.getText().toString());
                        bundle2.putString("company",expCompany.getText().toString());
                        bundle2.putString("duration",expDuration.getText().toString());
                        bundle2.putString("location",expLocation.getText().toString());
                        bundle2.putString("experienceId", expId);
                        bundle2.putString("description", description);
                        bundle2.putString("userStatus", getArguments().getString("userStatus"));
                        AddExperienceDialog addExperienceDialog = new AddExperienceDialog();
                        addExperienceDialog.setArguments(bundle2);
                        addExperienceDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Add Experience");
                    }
                });

                removeExperience.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialog(expId);
                    }
                });
            }
        }
    }
    public void removeExperience(final String expID)
    {
        FirebaseFirestore.getInstance().collection("Users").document("Student")
                .collection(uName).document("Profile").collection("Experience")
                .document(expID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setListviewExperience();
                loadListView();

            }
        });
    }
    public void loadListView()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (listExperience.getChildAt(totalExperienceObjects-1) == null ||
                        listExperience.getChildAt(totalExperienceObjects-1) .equals("") ) { // your conditions
                }
                progressBar.setVisibility(View.INVISIBLE);
                updateExperience(); // your task to execute
            }
        };
        new Thread(runnable).start();
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.addExperience)
        {
            openAddExperienceDialog();
        }
        else if(v.getId()==R.id.backExperienceUser)
        {
            FragmentManager fm = getFragmentManager();
            fm.popBackStack ("profileUser", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void showAlertDialog(final String expID)
    {
        new AwesomeInfoDialog(getContext())
                .setTitle(Html.fromHtml("<b>"+"Delete Experience"+"</b>", Html.FROM_HTML_MODE_LEGACY))
                .setMessage("Are you sure you want to delete this item?")
                .setColoredCircle(R.color.primaryDark)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                .setCancelable(true)
                .setPositiveButtonText("DELETE")
                .setPositiveButtonbackgroundColor(R.color.primaryDark)
                .setPositiveButtonTextColor(R.color.white)
                .setNegativeButtonText("CANCEL")
                .setNegativeButtonbackgroundColor(R.color.primaryDark)
                .setNegativeButtonTextColor(R.color.white)
                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        removeExperience(expID);
                    }
                })
                .setNegativeButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        //cancel
                    }
                })
                .show();
    }
    public void showProgressbar()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

}
