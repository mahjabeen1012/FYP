package com.childcareapp.pivak.fyplogin.Dialogs;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.Models.UserProjects;
import com.childcareapp.pivak.fyplogin.R;
import com.childcareapp.pivak.fyplogin.ViewProjectsUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddProjectDialog extends AppCompatDialogFragment {
    TextView titleLabel,descriptionLabel;
    EditText ProjectTitle,projectDescription;
    Button addProject,cancelProject;
    String uName,status;
    boolean btnPressed=false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_project_dialog, null);
        bindXmlAttributes(view);
        textChangeLitener();
        checkStatus();
        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPressed=true;
                if( !ProjectTitle.getText().toString().equals("") && !projectDescription.getText().toString().equals(""))
                {
                    storeDataToFirestore();
                    loadPreviousFragment();
                    dismiss();
                }
                else
                {
                    setTextColorRed();
                    Toast.makeText(getActivity(), "Enter all the required Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public  void textChangeLitener()
    {
        ProjectTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && btnPressed==true)
                {
                    titleLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    titleLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        projectDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && btnPressed==true)
                {
                    descriptionLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    descriptionLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public  void loadPreviousFragment()
    {

        String uStatus=getArguments().getString("userStatus");
        Bundle bundle4 = new Bundle();
        bundle4.putString("user", uName);
        bundle4.putString("userStatus", uStatus);
        bundle4.putString("profileType", "myProfile");
        ViewProjectsUser viewProjectsUser = new ViewProjectsUser();
        viewProjectsUser.setArguments(bundle4);
        FragmentTransaction fragmentTransaction3 = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction3.addToBackStack(null);
        if(uStatus.equals("Student")) {
            fragmentTransaction3.replace(R.id.content_student_home, viewProjectsUser);
        }
        else if(uStatus.equals("Alumni"))
        {
            fragmentTransaction3.replace(R.id.content_alumni_home, viewProjectsUser);
        }
        else if(uStatus.equals("Faculty"))
        {
            fragmentTransaction3.replace(R.id.content_faculty_home, viewProjectsUser);
        }
        fragmentTransaction3.commit();
    }

    public void bindXmlAttributes(View view)
    {
        titleLabel=view.findViewById(R.id.addProjectTitleLabel);
        descriptionLabel=view.findViewById(R.id.linkAddProject);
        projectDescription=view.findViewById(R.id.addProjectLink);
        ProjectTitle=view.findViewById(R.id.titleAddProject);
        addProject=view.findViewById(R.id.addProjectDialogA);
        cancelProject=view.findViewById(R.id.CancelProjectDialogA);
    }

    public void checkStatus()
    {
        uName=getArguments().getString("user");
        status=getArguments().getString("status");
        if(status.equals("update")) {
            ProjectTitle.setText(getArguments().getString("title"));
            projectDescription.setText(getArguments().getString("description"));
            addProject.setText("UPDATE");
        }
        else {
            addProject.setText("ADD");
        }
    }

    public void setTextColorRed()
    {
        boolean postionSet=false;
        if(ProjectTitle.getText().toString().equals(""))
        {
            titleLabel.setTextColor(getResources().getColor(R.color.red));
            ProjectTitle.requestFocus();
            postionSet=true;
        }
        else
        {
            titleLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(projectDescription.getText().toString().equals(""))
        {
            descriptionLabel.setTextColor(getResources().getColor(R.color.red));
            if(postionSet==false)
            {
                projectDescription.requestFocus();
                postionSet=true;
            }
        }
        else
        {
            descriptionLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
    }

    public void storeDataToFirestore()
    {
        if(status.equals("add")) {
            final UserProjects projects = new UserProjects(ProjectTitle.getText().toString(), projectDescription.getText().toString());
            FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName).document("Profile").collection("Projects").add(projects).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference aVoid) {

                }
            });
        }
        else if(status.equals("update"))
        {
            Map<String, Object> data = new HashMap<>();
            data.put("title",ProjectTitle.getText().toString());
            data.put("description",projectDescription.getText().toString());
            FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName).document("Profile").collection("Projects").document(getArguments().getString("projectID")).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
    }
}
