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
import com.childcareapp.pivak.fyplogin.Dialogs.AddAwardDialog;
import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewAward;
import com.childcareapp.pivak.fyplogin.Models.UserAwards;
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

public class ViewAwardsUser extends Fragment implements View.OnClickListener{
    String uName,userStatus,profileType;
    Button addAward,back;
    ListView listAward;
    List<UserAwards> awardsList;
    List<String> awardObjects = new ArrayList<>();
    List<String> descriptionObject = new ArrayList<>();
    int totalAwards=0;
    ProgressBar progressBar;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.view_awards_user, container, false);
        addAward = view.findViewById(R.id.addAwards);
        back = view.findViewById(R.id.backAwardsUser);
        back.setOnClickListener(this);
        addAward.setOnClickListener(this);
        uName= getArguments().getString("user");
        userStatus=getArguments().getString("userStatus");
        profileType=getArguments().getString("profileType");

        progressBar=view.findViewById(R.id.awardProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        if(profileType.equals("search"))
        {
            addAward.setVisibility(View.INVISIBLE);
        }
        else if(profileType.equals("myProfile"))
        {
            addAward.setVisibility(View.VISIBLE);
        }

        setListviewAwards();
        loadListView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void openAddAwardDialog()
    {
        Bundle bundle2 = new Bundle();
        bundle2.putString("user", uName);
        bundle2.putString("status","add");
        bundle2.putString("userStatus",userStatus);
        AddAwardDialog addAwardDialog = new AddAwardDialog();
        addAwardDialog.setArguments(bundle2);
        addAwardDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Add Award");
    }

    public void setListAwards(String objID, String title, String year, String description)
    {
        UserAwards dataaa;
        dataaa = new UserAwards(title, year);
        awardsList.add(dataaa);
        ListViewAward listtt = new ListViewAward(getActivity(), R.layout.listview_awards, awardsList);
        listAward.setAdapter(listtt);

        ListAdapter listAdapterrr = listAward.getAdapter();
        if (listAdapterrr == null)
        {
            return;
        }
        else
        {
            int totalHeight = 0;
            for (int i = 0; i < listAdapterrr.getCount(); i++)
            {
                View listItem = listAdapterrr.getView(i, null, listAward);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listAward.getLayoutParams();
            params.height = totalHeight + (listAward.getDividerHeight() * (listAdapterrr.getCount() - 1));
            listAward.setLayoutParams(params);
            listAward.requestLayout();
            awardObjects.add(objID);
            descriptionObject.add(description);
        }

    }
    public void setListviewAwards()
    {
        descriptionObject.clear();
        awardObjects.clear();
        listAward = view.findViewById(R.id.listViewAwardss);
        listAward.setAdapter(null);
        final FirebaseFirestore mStore=FirebaseFirestore.getInstance();
        mStore.collection("Users").document("Student").collection(uName).document("Profile").collection("Awards").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                {
                    Log.i(TAG, "Awards: empty list");
                    return;
                }
                else
                {
                    awardsList = new ArrayList<>();
                    totalAwards=documentSnapshots.size();
                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
                    {
                        if (documentSnapshot.exists())
                        {
                            final String idd=documentSnapshot.getId();
                            DocumentReference docRef = mStore.collection("Users").document("Student").collection(uName)
                                    .document("Profile").collection("Awards").document(idd);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            ///////////////
                                            UserAwards awards=document.toObject(UserAwards.class);
                                            setListAwards(idd, awards.getTitle(), awards.getYear(),awards.getDescription()); /// fill awards listview
                                        }
                                        else {
                                            Log.i(TAG, "Awards: Document Doesn't Exist");
                                        }
                                    }
                                    else {
                                        Log.i(TAG, "Awards: Failed to get data");
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
                Log.i(TAG, "Awards: Failed to get data");
            }
        });
    }

    public void updateAward()
    {
        if(listAward.getCount()>0) {
            for (int i = 0; i <= listAward.getLastVisiblePosition() - listAward.getFirstVisiblePosition(); i++) {
                String s="";
                final View view1 = listAward.getChildAt(i);
                final String awardId=awardObjects.get(i);
                final String description=descriptionObject.get(i);
                final TextView awardTitle=view1.findViewById(R.id.awardTitle);
                final TextView awardYear=(TextView) view1.findViewById(R.id.awardYear);
                final Button awardEdit = (Button) view1.findViewById(R.id.editAward);
                final Button removeAward= (Button) view1.findViewById(R.id.deleteAwards);


                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        // Any UI task
                        if(profileType.equals("search"))
                        {
                            awardEdit.setVisibility(view1.INVISIBLE);
                            removeAward.setVisibility(view1.INVISIBLE);
                        }
                        else
                        {
                            awardEdit.setVisibility(view1.VISIBLE);
                            removeAward.setVisibility(view1.VISIBLE);
                        }
                    }
                };
                handler.sendEmptyMessage(1);

                awardEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("user", uName);
                        bundle2.putString("status", "update");
                        bundle2.putString("title",awardTitle.getText().toString());
                        bundle2.putString("year",awardYear.getText().toString());
                        bundle2.putString("awardID", awardId);
                        bundle2.putString("userStatus",userStatus);
                        bundle2.putString("description",description);
                        AddAwardDialog addAwardDialog = new AddAwardDialog();
                        addAwardDialog.setArguments(bundle2);
                        addAwardDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Update Award");
                    }
                });

                removeAward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialog(awardId);
                    }
                });
            }
        }
    }
    public void deleteAward(final String awardID)
    {
        FirebaseFirestore.getInstance().collection("Users").document("Student")
                .collection(uName).document("Profile").collection("Awards")
                .document(awardID)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setListviewAwards();
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
                while (listAward.getChildAt(totalAwards-1) == null || listAward.getChildAt(totalAwards-1) .equals("") ) { // your conditions
                }
                progressBar.setVisibility(View.INVISIBLE);
                updateAward(); // your task to execute
            }
        };
        new Thread(runnable).start();
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.addAwards)
        {
            openAddAwardDialog();
        }
        else if(v.getId()==R.id.backAwardsUser)
        {
            FragmentManager fm = getActivity().getFragmentManager();
            fm.popBackStack ("profileUser", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void showAlertDialog(final String awardID)
    {
        new AwesomeInfoDialog(getContext())
                .setTitle(Html.fromHtml("<b>"+"Delete Award"+"</b>", Html.FROM_HTML_MODE_LEGACY))
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
                        deleteAward(awardID);
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
}
