package com.childcareapp.pivak.fyplogin.Dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.Models.UserExperience;
import com.childcareapp.pivak.fyplogin.R;
import com.childcareapp.pivak.fyplogin.ViewExperienceUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by IDEAL on 11/19/2018.
 */

public class AddExperienceDialog extends AppCompatDialogFragment {
    EditText designation, description, company, country, city, to, from;
    TextView designationLabel, descriptionLabel, companyLabel,countryLabel, cityLabel,eDateLabel,sDateLabel;
    CheckBox current;
    Button fromButton,toButton,addExperience,cancelExperience;
    String uName,status,pButton, eDate;
    private FirebaseFirestore mStore;
    boolean btnPressed=false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_experience_dialog, null);
        bindXmAttributes(view);
        uName= getArguments().getString("user");
        status=getArguments().getString("status");

        editTextsOnClickListner();
        textChangeLitener();
        // check if experience has to be added or update the current
        checkStatus();
        // enable one at a time. either currently working or ending date
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkJobStatus();
            }
        });

        //Select Starting Date
        toButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartDate();
            }
        });

        //Select Ending date
        fromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndDate();
            }
        });
        addExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPressed=true;
                if(!designation.getText().toString().equals("") && !company.getText().toString().equals("") && !country.getText().toString().equals("") && !city.getText().toString().equals("") && !to.getText().toString().equals("") && (!from.getText().toString().equals("") || current.isChecked()))
                {
                    if(current.isChecked()==true)
                        eDate="Current";
                    else
                        eDate=from.getText().toString();
                    storeDataToFirestore();
                }
                else
                {
                    setCursorPosition(setTextColorRed());
                    Toast.makeText(getActivity(), "Enter all the required Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view).setTitle("Add Experience");

        final AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public  void textChangeLitener()
    {
        designation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && btnPressed==true)
                {
                    designationLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    designationLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        company.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && btnPressed==true)
                {
                    companyLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    companyLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        country.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && btnPressed==true)
                {
                    countryLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    countryLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && btnPressed==true)
                {
                    cityLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    cityLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        from.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && btnPressed==true)
                {
                    eDateLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    eDateLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        to.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && btnPressed==true)
                {
                    sDateLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    sDateLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void editTextsOnClickListner()
    {
        designation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                designation.setCursorVisible(true);
            }
        });
        company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                company.setCursorVisible(true);
            }
        });
        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                country.setCursorVisible(true);
            }
        });
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city.setCursorVisible(true);
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
        ViewExperienceUser viewExperienceUser = new ViewExperienceUser();
        viewExperienceUser.setArguments(bundle4);
        FragmentTransaction fragmentTransaction3 = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction3.addToBackStack(null);
        if(uStatus.equals("Student")) {
            fragmentTransaction3.replace(R.id.content_student_home, viewExperienceUser);
        }
        else if(uStatus.equals("Alumni"))
        {
            fragmentTransaction3.replace(R.id.content_alumni_home, viewExperienceUser);
        }
        else if(uStatus.equals("Faculty"))
        {
            fragmentTransaction3.replace(R.id.content_faculty_home, viewExperienceUser);
        }
        fragmentTransaction3.commit();
    }

    public void bindXmAttributes(View view)
    {
        addExperience=view.findViewById(R.id.addExperienceDialog);
        cancelExperience=view.findViewById(R.id.CancelExperienceDialog);
        designationLabel = view.findViewById(R.id.designationLabel);
        descriptionLabel=view.findViewById(R.id.descriptionLabel);
        companyLabel = view.findViewById(R.id.organizationLabel);
        countryLabel = view.findViewById(R.id.countryLabel);
        cityLabel =view.findViewById(R.id.cityLabel);
        eDateLabel = view.findViewById(R.id.eDateLabel);
        sDateLabel = view.findViewById(R.id.sDateLabel);
        designation = view.findViewById(R.id.designationAddExperience);
        description=view.findViewById(R.id.descriptionAddExperience);
        company = view.findViewById(R.id.companyAddExperience);
        country = view.findViewById(R.id.coutryAddExperience);
        city =view.findViewById(R.id.cityAddExperience);
        to = view.findViewById(R.id.toAddExperience);
        to.setEnabled(false);
        from = view.findViewById(R.id.fromAddExperience);
        from.setEnabled(false);
        toButton=view.findViewById(R.id.toButtonAddExperience);
        fromButton=view.findViewById(R.id.fromButtonAddExperience);
        current=view.findViewById(R.id.currentlyWorkHere);
    }

    public void checkStatus()
    {
        if(status.equals("update")) {
            designation.setText(getArguments().getString("post"));
            company.setText(getArguments().getString("company"));
            description.setText(getArguments().getString("description"));
            final String[] location = getArguments().getString("location").split(",");
            city.setText(location[0]);
            country.setText(location[1]);
            final String[] duration = getArguments().getString("duration").split("to");
            to.setText(duration[0]);
            if(duration[1].equals("Current"))
            {
                current.setChecked(true);
            }
            else
            {
                from.setText(duration[1]);
            }
            addExperience.setText("UPDATE");
        }
        else {
            addExperience.setText("ADD");
        }
    }

    public void checkJobStatus()
    {
        if(current.isChecked())
        {
            fromButton.setEnabled(false);
            fromButton.setBackgroundColor(getResources().getColor(R.color.lightGrey));
            from.setTextColor(getResources().getColor(R.color.lightGrey));
            eDateLabel.setTextColor(getResources().getColor(R.color.lightGrey));
        }
        else
        {
            if(btnPressed==true && from.getText().toString().equals(""))
            {
                eDateLabel.setTextColor(getResources().getColor(R.color.red));
            }
            else
                eDateLabel.setTextColor(getResources().getColor(R.color.darkGrey));
            fromButton.setEnabled(true);
            fromButton.setBackgroundColor(getResources().getColor(R.color.primaryDark));
            from.setTextColor(getResources().getColor(R.color.darkGrey));
        }
    }

    public void setStartDate()
    {
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog abc = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                to.setText(String.valueOf(dayOfMonth)+"-"+String.valueOf(month+1)+"-"+String.valueOf(year));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_WEEK));
        abc.show();
    }

    public void setEndDate()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog abc = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                from.setText(String.valueOf(dayOfMonth)+"-"+String.valueOf(month+1)+"-"+String.valueOf(year));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_WEEK));
        abc.show();
    }

    public int setTextColorRed()
    {
        int position=0; boolean setPosition=false;
        if(designation.getText().toString().equals(""))
        {
            designationLabel.setTextColor(getResources().getColor(R.color.red));
            position=1;
            setPosition=true;
        }
        else
        {
            designationLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }

        if(company.getText().toString().equals(""))
        {
            companyLabel.setTextColor(getResources().getColor(R.color.red));
            if(setPosition==false)
            {
                position=2;
                setPosition=true;
            }
        }
        else
        {
            companyLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(country.getText().toString().equals(""))
        {
            countryLabel.setTextColor(getResources().getColor(R.color.red));
            if(setPosition==false)
            {
                position=4;
                setPosition=true;
            }
        }
        else
        {
            countryLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(city.getText().toString().equals(""))
        {
            cityLabel.setTextColor(getResources().getColor(R.color.red));
            if(setPosition==false)
            {
                position=3;
                setPosition=true;
            }
        }
        else
        {
            cityLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(to.getText().toString().equals("") && current.isChecked()==false)
        {
            sDateLabel.setTextColor(getResources().getColor(R.color.red));
            if(setPosition==false)
            {
                position=6;
                setPosition=true;
            }
        }
        else
        {
            sDateLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(from.getText().toString().equals(""))
        {
            eDateLabel.setTextColor(getResources().getColor(R.color.red));
            if(setPosition==false)
            {
                position=5;
                setPosition=true;
            }
        }
        else
        {
            eDateLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        return position;
    }
    public void setCursorPosition(int position)
    {
        if(position==1) {
            designation.requestFocus();
        }
        else if(position==2) {
            company.requestFocus();
        }
        else if(position==3) {
            city.requestFocus();
        }
        else if(position==4) {
            country.requestFocus();
        }
        else if(position==5) {
            from.requestFocus();
            designation.setCursorVisible(false);
            company.setCursorVisible(false);
            city.setCursorVisible(false);
            country.setCursorVisible(false);
        }
        else if(position==6) {
            to.requestFocus();
            designation.setCursorVisible(false);
            company.setCursorVisible(false);
            city.setCursorVisible(false);
            country.setCursorVisible(false);
        }
    }
    public void storeDataToFirestore()
    {
        mStore=FirebaseFirestore.getInstance();
        if(status.equals("add")) {
            final UserExperience studentExperience= new UserExperience(designation.getText().toString(), company.getText().toString(), country.getText().toString(), city.getText().toString(),to.getText().toString(),eDate,description.getText().toString());
            mStore.collection("Users").document("Student").collection(uName).document("Profile")
                    .collection("Experience").add(studentExperience).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference aVoid) {
                    loadPreviousFragment();
                    dismiss();
                }
            });
        }
        else if(status.equals("update"))
        {
            Map<String, Object> data = new HashMap<>();
            data.put("designation",designation.getText().toString());
            data.put("organization",company.getText().toString());
            data.put("city",city.getText().toString());
            data.put("country",country.getText().toString());
            data.put("sDate",to.getText().toString());
            data.put("eDate",eDate);
            data.put("description",description.getText().toString());
            mStore.collection("Users").document("Student").collection(uName).document("Profile").collection("Experience").document(getArguments().getString("experienceId")).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    loadPreviousFragment();
                    dismiss();
                }
            });
        }

    }
}
