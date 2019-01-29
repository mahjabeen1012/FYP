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

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.childcareapp.pivak.fyplogin.Dialogs.AddEducationDialog;
import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewEducation;
import com.childcareapp.pivak.fyplogin.Models.UserEducation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ViewEducationUser extends Fragment implements View.OnClickListener{
    public Button back, addEducation;
    String uName,profileType;
    List<UserEducation> educationList;
    List<String> educationObjects = new ArrayList<>();
    ListView listEducation;
    int totalEducationObjects=0;
    ProgressBar progressBar;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.view_education_user, container, false);
        addEducation = view.findViewById(R.id.addEducation);
        back = view.findViewById(R.id.backEducationUser);
        back.setOnClickListener(this);
        addEducation.setOnClickListener(this);
        progressBar=view.findViewById(R.id.educationProgressBar);


        uName= getArguments().getString("user");
        profileType=getArguments().getString("profileType");

        showProgressbar();
        if(profileType.equals("search"))
        {
            addEducation.setVisibility(View.INVISIBLE);
        }
        else if(profileType.equals("myProfile"))
        {
            addEducation.setVisibility(View.VISIBLE);
        }

        setListviewEducation();
        loadListView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void openAddEducationDialog()
    {
        Bundle bundle2 = new Bundle();
        bundle2.putString("user", uName);
        bundle2.putString("status","add");
        bundle2.putString("userStatus", getArguments().getString("userStatus"));
        AddEducationDialog addEducationDialog = new AddEducationDialog();
        addEducationDialog.setArguments(bundle2);
        addEducationDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(),"Add Education");
    }

    public void setListEducation(String objID, String degree, String inst,String sDate, String eDate ,String city, String country)
    {
        UserEducation dataa;
        dataa = new UserEducation(degree, inst, country, city , sDate, eDate);
        educationList.add(dataa);
        ListViewEducation listt = new ListViewEducation(getActivity(), R.layout.listview_education, educationList);
        listEducation.setAdapter(listt);
        ListAdapter listAdapterr = listEducation.getAdapter();
        if (listAdapterr == null)
        {
            return;
        }
        else
        {
            int totalHeight = 0;
            for (int i = 0; i < listAdapterr.getCount(); i++)
            {
                View listItem = listAdapterr.getView(i, null, listEducation);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listEducation.getLayoutParams();
            params.height = totalHeight + (listEducation.getDividerHeight() * (listAdapterr.getCount() - 1));
            listEducation.setLayoutParams(params);
            listEducation.requestLayout();
            educationObjects.add(objID);
        }

    }
    public void setListviewEducation()
    {
        listEducation = view.findViewById(R.id.ListViewEducation);
        listEducation.setAdapter(null);
        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName).document("Profile").collection("Education").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                {
                    Log.i(TAG, "Education: Empty list");
                    return;
                }
                else
                {
                    educationList = new ArrayList<>();
                    totalEducationObjects=documentSnapshots.size();
                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
                    {
                        if (documentSnapshot.exists())
                        {
                            final String idd=documentSnapshot.getId();

                            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users")
                                    .document("Student").collection(uName).document("Profile")
                                    .collection("Education").document(idd);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {

                                            UserEducation userEducation=document.toObject(UserEducation.class);
                                            //// Set listview
                                            setListEducation(idd,userEducation.getDegree(), userEducation.getInstitution(),
                                                    userEducation.getsDate(),userEducation.geteDate(),
                                                    userEducation.getCity(),userEducation.getCountry());
                                        }
                                        else {
                                            Log.i(TAG, "Education: document doesn't exist");
                                        }
                                    }
                                    else {
                                        Log.i(TAG, "Education: Failed to get data");
                                    }
                                }
                            });
                        }
                        //progressBar();
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "Education: Failed to get data");

            }
        });
    }

    public void updateEducation()
    {
        if(listEducation.getCount()>0) {
            for (int i = 0; i <= listEducation.getLastVisiblePosition() - listEducation.getFirstVisiblePosition(); i++) {
                String s = "";
                final View view1 = listEducation.getChildAt(i);
                if(view1!=null)
                {
                    final String eduId = educationObjects.get(i);
                    final TextView eduDegree = view1.findViewById(R.id.educationDegree);
                    final TextView eduInstituion = (TextView) view1.findViewById(R.id.educationInstitution);
                    final TextView eduDuration = (TextView) view1.findViewById(R.id.educationDuration);
                    final TextView eduLocation = (TextView) view1.findViewById(R.id.educationLocation);

                    final Button educationEdit = (Button) view1.findViewById(R.id.editEducation);
                    final Button removeEducation = (Button) view1.findViewById(R.id.deleteEducation);

                    Handler handler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            // Any UI task
                            if(profileType.equals("search"))
                            {
                                educationEdit.setVisibility(view1.INVISIBLE);
                                removeEducation.setVisibility(view1.INVISIBLE);
                            }
                            else
                            {
                                educationEdit.setVisibility(view1.VISIBLE);
                                removeEducation.setVisibility(view1.VISIBLE);
                            }
                        }
                    };
                    handler.sendEmptyMessage(1);

                    educationEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("user", uName);
                            bundle2.putString("status", "update");
                            bundle2.putString("degree", eduDegree.getText().toString());
                            bundle2.putString("institution", eduInstituion.getText().toString());
                            bundle2.putString("duration", eduDuration.getText().toString());
                            bundle2.putString("location", eduLocation.getText().toString());
                            bundle2.putString("educationID", eduId);
                            bundle2.putString("userStatus", getArguments().getString("userStatus"));
                            AddEducationDialog addEducationDialog = new AddEducationDialog();
                            addEducationDialog.setArguments(bundle2);
                            addEducationDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Add Education");
                        }
                    });

                    removeEducation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAlertDialog(eduId);
                        }
                    });
                }
            }
        }
    }

    public void removeEducation(final String eduID)
    {
        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName)
                .document("Profile").collection("Education").document(eduID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setListviewEducation();
                loadListView();
                return;
            }
        });
    }

    public void loadListView()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (listEducation.getChildAt(totalEducationObjects-1) == null ||
                        listEducation.getChildAt(totalEducationObjects-1) .equals("") ) { // your conditions
                }
                progressBar.setVisibility(View.INVISIBLE);
                updateEducation(); // your task to execute
            }
        };
        new Thread(runnable).start();
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.addEducation)
        {
            openAddEducationDialog();
        }
        else if(v.getId()==R.id.backEducationUser)
        {
            FragmentManager fm = getActivity().getFragmentManager();
            fm.popBackStack ("profileUser", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void showAlertDialog(final String eduID)
    {
        new AwesomeInfoDialog(getContext())
                .setTitle(Html.fromHtml("<b>"+"Delete Education"+"</b>", Html.FROM_HTML_MODE_LEGACY))
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
                        removeEducation(eduID);
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
