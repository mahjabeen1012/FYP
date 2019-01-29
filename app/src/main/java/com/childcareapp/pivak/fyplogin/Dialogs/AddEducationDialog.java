package com.childcareapp.pivak.fyplogin.Dialogs;

import android.app.DatePickerDialog;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.Models.UserEducation;
import com.childcareapp.pivak.fyplogin.R;
import com.childcareapp.pivak.fyplogin.ViewEducationUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by IDEAL on 11/19/2018.
 */

public class AddEducationDialog extends AppCompatDialogFragment {
    EditText degree,institution, country,city,sDate,eDate;
    TextView degreeLabel,instLabel,countryLabel, cityLabel,sDateLabel,eDateLabel;
    CheckBox current;
    Button eDateBtn, sDateBtn,addEdu,cancelEdu;
    String uName,endDate,status;
    private FirebaseFirestore mStore;
    boolean btnPressed=false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_education_dialog, null);

        bindXmlAttributes(view);
        textChangeLitener();
        editTextsOnClickListner();
        //check either add new education instance or update the current one
        checkStatus();
        // enable one at a time. either currently working or end date
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkJobStatus();
            }
        });

        //Select Starting Date
        sDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartDate();
            }
        });

        //Select Ending date
        eDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndDateate();
            }
        });

        addEdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPressed=true;
                if(!degree.getText().toString().equals("") && !institution.getText().toString().equals("") && !country.getText().toString()
                        .equals("") && !city.getText().toString().equals("") && !sDate.getText().toString().equals("") && (!eDate.getText()
                        .toString().equals("") || current.isChecked()==true))
                {
                    if(current.isChecked()==true)
                        endDate="Current";
                    else
                        endDate=eDate.getText().toString();

                    storeDataToFireStore();
                    loadPreviousFragment();
                    dismiss();
                }
                else
                {
                    setCursorPosition(setTextColorRed());
                    Toast.makeText(getActivity(), "Enter all the required Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setTitle("Add Education");
        cancelEdu.setOnClickListener(new View.OnClickListener() {
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

    public void bindXmlAttributes(View view)
    {
        uName= getArguments().getString("user");
        addEdu=view.findViewById(R.id.addEduDialogA);
        cancelEdu=view.findViewById(R.id.CancelEduDialogA);
        degreeLabel=view.findViewById(R.id.degreeLabelEducation);
        instLabel=view.findViewById(R.id.instLabelEducation);
        countryLabel=view.findViewById(R.id.countryLabelEducation);
        cityLabel=view.findViewById(R.id.cityLabelEducation);
        sDateLabel=view.findViewById(R.id.sDateLabelEducation);
        eDateLabel=view.findViewById(R.id.eDateLabelEducation);
        degree=view.findViewById(R.id.degreeAddEducation);
        institution=view.findViewById(R.id.institutionAddEduzation);
        country=view.findViewById(R.id.coutryAddEducation);
        city=view.findViewById(R.id.cityAddEducation);
        sDate=view.findViewById(R.id.fromAddEducation);
        sDate.setEnabled(false);
        eDate=view.findViewById(R.id.toAddEducation);
        eDate.setEnabled(false);
        current=view.findViewById(R.id.currentlyStudyHere);
        eDateBtn=view.findViewById(R.id.toButtonAddEducation);
        sDateBtn=view.findViewById(R.id.fromButtonAddEducation);
        mStore = FirebaseFirestore.getInstance();
    }

    public  void textChangeLitener()
    {
        degree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && btnPressed==true)
                {
                    degreeLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    degreeLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        institution.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && btnPressed==true)
                {
                    instLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    instLabel.setTextColor(getResources().getColor(R.color.darkGrey));
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
        sDate.addTextChangedListener(new TextWatcher() {
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
        eDate.addTextChangedListener(new TextWatcher() {
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
    }

    public void editTextsOnClickListner()
    {
        degree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                degree.setCursorVisible(true);
            }
        });
        institution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                institution.setCursorVisible(true);
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

    public void checkStatus()
    {
        status=getArguments().getString("status");
        if(status.equals("update")) {
            degree.setText(getArguments().getString("degree"));
            institution.setText(getArguments().getString("institution"));
            final String[] location = getArguments().getString("location").split(",");
            city.setText(location[0]);
            country.setText(location[1]);
            final String[] duration = getArguments().getString("duration").split("to");
            sDate.setText(duration[0]);
            if(duration[1].equals("Current"))
            {
                current.setChecked(true);
            }
            else
            {
                eDate.setText(duration[1]);
            }
            addEdu.setText("UPDATE");
        }
        else {
            addEdu.setText("ADD");
        }
    }

    public  void setStartDate()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog abc = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                sDate.setText(String.valueOf(dayOfMonth)+"-"+String.valueOf(month+1)+"-"+String.valueOf(year));

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_WEEK));
        abc.show();
    }

    public  void loadPreviousFragment()
    {
        String uStatus=getArguments().getString("userStatus");
        Bundle bundle4 = new Bundle();
        bundle4.putString("user", uName);
        bundle4.putString("userStatus", uStatus);
        bundle4.putString("profileType", "myProfile");
        ViewEducationUser viewEducationUser = new ViewEducationUser();
        viewEducationUser.setArguments(bundle4);
        FragmentTransaction fragmentTransaction3 = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction3.addToBackStack(null);
        if(uStatus.equals("Student")) {
            fragmentTransaction3.replace(R.id.content_student_home, viewEducationUser);
        }
        else if(uStatus.equals("Alumni"))
        {
            fragmentTransaction3.replace(R.id.content_alumni_home, viewEducationUser);
        }
        else if(uStatus.equals("Faculty"))
        {
            fragmentTransaction3.replace(R.id.content_faculty_home, viewEducationUser);
        }
        fragmentTransaction3.commit();
    }

    public void setEndDateate()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog abc = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                eDate.setText(String.valueOf(dayOfMonth)+"-"+String.valueOf(month+1)+"-"+String.valueOf(year));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_WEEK));
        abc.show();
    }

    public void checkJobStatus()
    {
        if(current.isChecked())
        {
            eDateBtn.setEnabled(false);
            eDateBtn.setBackgroundColor(getResources().getColor(R.color.lightGrey));
            eDate.setTextColor(getResources().getColor(R.color.lightGrey));
            eDateLabel.setTextColor(getResources().getColor(R.color.lightGrey));
        }
        else
        {
            if(btnPressed==true && eDate.getText().toString().equals(""))
            {
                eDateLabel.setTextColor(getResources().getColor(R.color.red));
            }
            else
                eDateLabel.setTextColor(getResources().getColor(R.color.darkGrey));
            eDateBtn.setEnabled(true);
            eDateBtn.setBackgroundColor(getResources().getColor(R.color.primaryDark));
            eDate.setTextColor(getResources().getColor(R.color.darkGrey));
        }
    }

    public int setTextColorRed()
    {
        int position=0; boolean positionSet=false;
        if(degree.getText().toString().equals(""))
        {
            degreeLabel.setTextColor(getResources().getColor(R.color.red));
            position=1;
            positionSet=true;
        }
        else
        {
            degreeLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(institution.getText().toString().equals(""))
        {
            instLabel.setTextColor(getResources().getColor(R.color.red));
            if(positionSet==false)
            {
                position=2;
                positionSet=true;
            }
        }
        else
        {
            instLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(country.getText().toString().equals(""))
        {
            countryLabel.setTextColor(getResources().getColor(R.color.red));
            if(positionSet==false)
            {
                position=4;
                positionSet=true;
            }
        }
        else
        {
            countryLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(city.getText().toString().equals(""))
        {
            cityLabel.setTextColor(getResources().getColor(R.color.red));
            if(positionSet==false)
            {
                position=3;
                positionSet=true;
            }
        }
        else
        {
            cityLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(eDate.getText().toString().equals("") && current.isChecked()==false)
        {
            eDateLabel.setTextColor(getResources().getColor(R.color.red));
            if(positionSet==false)
            {
                position=6;
                positionSet=true;
            }
        }
        else
        {
            eDateLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(sDate.getText().toString().equals(""))
        {
            sDateLabel.setTextColor(getResources().getColor(R.color.red));
            if(positionSet==false)
            {
                position=5;
                positionSet=true;
            }
        }
        else
        {
            sDateLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        return position;
    }
    public void setCursorPosition(int position)
    {
        if(position==1) {
            degree.requestFocus();
        }
        else if(position==2) {
            institution.requestFocus();
        }
        else if(position==3) {
            city.requestFocus();
        }
        else if(position==4) {
            country.requestFocus();
        }
        else if(position==5) {
            sDateBtn.requestFocus();
            degree.setCursorVisible(false);
            institution.setCursorVisible(false);
            city.setCursorVisible(false);
            country.setCursorVisible(false);
        }
        else if(position==6) {
            eDateBtn.requestFocus();
            degree.setCursorVisible(false);
            institution.setCursorVisible(false);
            city.setCursorVisible(false);
            country.setCursorVisible(false);
        }
    }

    public void storeDataToFireStore()
    {
        if(status.equals("add"))
        {
            final UserEducation userEducation = new UserEducation(degree.getText().toString(), institution.getText().toString(),
                    country.getText().toString(), city.getText().toString(), sDate.getText().toString(), endDate);
            mStore.collection("Users").document("Student").collection(uName).document("Profile")
                    .collection("Education").add(userEducation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference aVoid) {
                }
            });
        }
        else if(status.equals("update"))
        {
            Map<String, Object> data = new HashMap<>();
            data.put("degree",degree.getText().toString());
            data.put("institution",institution.getText().toString());
            data.put("city",city.getText().toString());
            data.put("country",country.getText().toString());
            data.put("sDate",sDate.getText().toString());
            data.put("eDate",endDate);
            mStore.collection("Users").document("Student").collection(uName).document("Profile")
                    .collection("Education").document(getArguments().getString("educationID"))
                    .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }

    }
}
