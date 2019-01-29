package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MembersUser extends Fragment implements View.OnClickListener {

    Spinner batch, degree, decipline, campus;
    EditText username;
    Button searchUserBtn;
    String uName,status;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.members_user, container, false);

        uName=getArguments().getString("user");
        status=getArguments().getString("status");

        searchUserBtn=view.findViewById(R.id.searchUserBtn);
        searchUserBtn.setOnClickListener(this);

        username=view.findViewById(R.id.nameSearch);
        batch = view.findViewById(R.id.batchSearch);
        degree = view.findViewById(R.id.degreeSearch);
        decipline = view.findViewById(R.id.deciplineSearch);
        campus = view.findViewById(R.id.campusSearch);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Members");

    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.searchUserBtn)
        {
            if(username.getText().toString().equals("") && batch.getSelectedItem().toString().equals("") && degree.getSelectedItem().toString()
                    .equals("") && decipline.getSelectedItem().toString().equals("") && campus.getSelectedItem().toString().equals(""))
            {
                Toast.makeText(getActivity(), "Please Select one Item", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Bundle bundle=new Bundle();
                bundle.putString("name",username.getText().toString());
                bundle.putString("batch", batch.getSelectedItem().toString());
                bundle.putString("degree", degree.getSelectedItem().toString());
                bundle.putString("discipline", decipline.getSelectedItem().toString());
                bundle.putString("campus", campus.getSelectedItem().toString());
                bundle.putString("status", status);
                bundle.putString("loggedInUserID", uName);
                bundle.putString("senderName", getArguments().getString("senderName"));
                bundle.putString("senderPhoto", getArguments().getString("senderPhoto"));
                SearchActivity searchActivity = new SearchActivity();
                searchActivity.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                if(status.equals("Student")) {
                    fragmentTransaction.replace(R.id.content_student_home, searchActivity);
                }
                else if(status.equals("Alumni"))
                {
                    fragmentTransaction.replace(R.id.content_alumni_home, searchActivity);
                }
                else if(status.equals("Faculty"))
                {
                    fragmentTransaction.replace(R.id.content_faculty_home, searchActivity);
                }
                else if(status.equals("Company"))
                {
                    fragmentTransaction.replace(R.id.content_company_home, searchActivity);
                }
                fragmentTransaction.commit();
            }
        }
    }
}