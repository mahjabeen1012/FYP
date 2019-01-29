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
import com.childcareapp.pivak.fyplogin.Dialogs.AddProjectDialog;
import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewProjects;
import com.childcareapp.pivak.fyplogin.Models.UserProjects;
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

public class ViewProjectsUser extends Fragment implements View.OnClickListener {

    Button addProjects,back;
    String uName,profileType;
    View view;
    ListView listProject;
    List<UserProjects> projectsList;
    List<String> projectObjects = new ArrayList<>();
    ProgressBar progressBar;
    int totalProjects=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.view_project_user, container, false);
        addProjects = view.findViewById(R.id.addProjects);
        back = view.findViewById(R.id.backProjectsUser);
        back.setOnClickListener(this);
        addProjects.setOnClickListener(this);
        uName=getArguments().getString("user");
        profileType=getArguments().getString("profileType");
        progressBar=view.findViewById(R.id.projectProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        if(profileType.equals("search"))
        {
            addProjects.setVisibility(View.INVISIBLE);
        }
        else if(profileType.equals("myProfile"))
        {
            addProjects.setVisibility(View.VISIBLE);
        }

        setListviewProjects();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void openAddProjectDialog()
    {
        Bundle bundle2 = new Bundle();
        bundle2.putString("user", uName);
        bundle2.putString("status", "add");
        bundle2.putString("userStatus", getArguments().getString("userStatus"));
        AddProjectDialog addProjectDialog = new AddProjectDialog();
        addProjectDialog.setArguments(bundle2);
        addProjectDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Add Project");
    }

    public void setListProject(String objID, String title, String description)
    {
        projectsList.add(new UserProjects(title, description));
        ListViewProjects listtt = new ListViewProjects(getActivity(), R.layout.listview_projects, projectsList);
        listProject.setAdapter(listtt);
        ListAdapter listAdapterrr = listProject.getAdapter();
        if (listAdapterrr == null)
        {
            return;
        }
        else
        {
            int totalHeight = 0;
            for (int i = 0; i < listAdapterrr.getCount(); i++)
            {
                View listItem = listAdapterrr.getView(i, null, listProject);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listProject.getLayoutParams();
            params.height = totalHeight + (listProject.getDividerHeight() * (listAdapterrr.getCount() - 1));
            listProject.setLayoutParams(params);
            listProject.requestLayout();
            projectObjects.add(objID);
        }
        if(listProject.getCount()==totalProjects)
        {
            loadListView();
        }
    }
    public void setListviewProjects()
    {
        listProject = view.findViewById(R.id.ListViewProjects);
        listProject.setAdapter(null);
        final FirebaseFirestore mStore=FirebaseFirestore.getInstance();
        mStore.collection("Users").document("Student").collection(uName).document("Profile").
                collection("Projects").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                {
                    Log.i(TAG, "Projects: empty list");
                    return;
                }
                else
                {
                    projectsList = new ArrayList<>();
                    totalProjects=documentSnapshots.size();
                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
                    {
                        if (documentSnapshot.exists())
                        {
                            final String idd=documentSnapshot.getId();
                            DocumentReference docRef = mStore.collection("Users").document("Student").
                                    collection(uName).document("Profile").collection("Projects").document(idd);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            ///////////////
                                            UserProjects projects=document.toObject(UserProjects.class);
                                            setListProject(idd, projects.getTitle(), projects.getDescription()); /// fill awards listview
                                        }
                                        else {
                                            Log.i(TAG, "Project: Document Doesn't Exist");
                                        }
                                    }
                                    else {
                                        Log.i(TAG, "Project: Failed to get data");
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
                Log.i(TAG, "Project: Failed to get data");
            }
        });
    }

    public void updateProject()
    {
        if(listProject.getCount()>0) {
            for (int i = 0; i <listProject.getCount(); i++) {
                String s="";
                final View view1 = listProject.getChildAt(i);
                if(view1!=null) {
                    final String projectID = projectObjects.get(i);
                    final TextView projectTitle = view1.findViewById(R.id.projectTitleUser);
                    final TextView projectDescription = (TextView) view1.findViewById(R.id.projectLinkUser);
                    final Button projectEdit = (Button) view1.findViewById(R.id.editProject);
                    final Button removeProject = (Button) view1.findViewById(R.id.deleteProject);

                    Handler handler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            // Any UI task
                            if(getArguments().getString("profileType").equals("search"))
                            {
                                projectEdit.setVisibility(view1.INVISIBLE);
                                removeProject.setVisibility(view1.INVISIBLE);
                            }
                            else
                            {
                                projectEdit.setVisibility(view1.VISIBLE);
                                removeProject.setVisibility(view1.VISIBLE);
                            }
                        }
                    };
                    handler.sendEmptyMessage(1);

                    projectEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("user", uName);
                            bundle2.putString("status", "update");
                            bundle2.putString("title", projectTitle.getText().toString());
                            bundle2.putString("description", projectDescription.getText().toString());
                            bundle2.putString("projectID", projectID);
                            bundle2.putString("userStatus", getArguments().getString("userStatus"));
                            AddProjectDialog addProjectDialog = new AddProjectDialog();
                            addProjectDialog.setArguments(bundle2);
                            addProjectDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Update Project");
                        }
                    });

                    removeProject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAlertDialog(projectTitle.getText().toString(), projectDescription.getText().toString());
                        }
                    });
                }
            }
        }
    }
    public void deleteProject(final String projectID)
    {
        FirebaseFirestore.getInstance().collection("Users").document("Student")
                .collection(uName).document("Profile").collection("Projects")
                .document(projectID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setListviewProjects();
                return;
            }
        });
    }

    public void deleteProject(final String title, final String description)
    {
        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName).document("Profile").collection("Projects").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                {
                    Log.i(TAG, "Projects: empty list");
                    return;
                }
                else
                {
                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
                    {
                        if (documentSnapshot.exists())
                        {
                            final String idd=documentSnapshot.getId();
                            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName).document("Profile").collection("Projects").document(idd);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists())
                                        {
                                            // delete Document
                                            UserProjects projects=document.toObject(UserProjects.class);
                                            if(projects.getTitle().equals(title) && projects.getDescription().equals(description)) {
                                                FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName).document("Profile").collection("Projects").document(idd).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        setListviewProjects();
                                                        final Handler handler = new Handler();
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                updateProject();
                                                            }
                                                        }, 2000);
                                                        return;
                                                    }
                                                });
                                            }
                                        }
                                        else {
                                            Log.i(TAG, "Projects: Document Doesn't Exist");
                                        }
                                    }
                                    else {
                                        Log.i(TAG, "Projects: Failed to get data");
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
                Log.i(TAG, "Projects: Failed to get data");
            }
        });
    }



    public void loadListView()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (listProject.getChildAt(totalProjects-1) == null || listProject.getChildAt(totalProjects-1) .equals("") ) { // your conditions
                }
                progressBar.setVisibility(View.INVISIBLE);
                updateProject(); // your task to execute
            }
        };
        new Thread(runnable).start();


    }
    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.addProjects)
        {
            openAddProjectDialog();
        }
        else if(v.getId()==R.id.backProjectsUser)
        {
            FragmentManager fm = getActivity().getFragmentManager();
            fm.popBackStack ("profileUser", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    public void showAlertDialog(final String title, final String description)
    {
        new AwesomeInfoDialog(getContext())
                .setTitle(Html.fromHtml("<b>"+"DELETE PROJECT"+"</b>", Html.FROM_HTML_MODE_LEGACY))
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
                        deleteProject(title, description);
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
