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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.Models.UserAwards;
import com.childcareapp.pivak.fyplogin.R;
import com.childcareapp.pivak.fyplogin.ViewAwardsUser;
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

public class AddAwardDialog extends AppCompatDialogFragment {
    EditText title,date,description;
    TextView titleLabel,dateLabel,descriptionLabel;
    Button dateBtn, addAward,cancelAward;
    FirebaseFirestore mStore;
    String uName,status;
    View view;
    boolean btnPressed=false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.add_award_dialog, null);

        bindXmlAttributes(view);
        checkStatus();      // check either add a new award or update the current one.
        // Select Year
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setYear();
            }
        });
        textChangeLitener();
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setCursorVisible(true);
            }
        });
        addAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPressed=true;
                if( !title.getText().toString().equals("") && !date.getText().toString().equals(""))
                {
                    storeDataToFirestore();
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
        cancelAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(view).setTitle("Add Award");
        final AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }


    public  void textChangeLitener()
    {
        title.addTextChangedListener(new TextWatcher() {
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

        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dateLabel.setTextColor(getResources().getColor(R.color.darkGrey));
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
        ViewAwardsUser viewAwardsUser = new ViewAwardsUser();
        viewAwardsUser.setArguments(bundle4);
        FragmentTransaction fragmentTransaction3 = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction3.addToBackStack(null);
        if(uStatus.equals("Student")) {
            fragmentTransaction3.replace(R.id.content_student_home, viewAwardsUser);
        }
        else if(uStatus.equals("Alumni"))
        {
            fragmentTransaction3.replace(R.id.content_alumni_home, viewAwardsUser);
        }
        else if(uStatus.equals("Faculty"))
        {
            fragmentTransaction3.replace(R.id.content_faculty_home, viewAwardsUser);
        }
        fragmentTransaction3.commit();
    }
    public  void bindXmlAttributes(View view)
    {

        addAward=view.findViewById(R.id.addAwardDialogA);
        descriptionLabel=view.findViewById(R.id.descriptionLabelAward);
        description=view.findViewById(R.id.descriptionAddAward);
        cancelAward=view.findViewById(R.id.CancelAwardDialogA);
        titleLabel=view.findViewById(R.id.titleLabel);
        dateLabel=view.findViewById(R.id.yearLabel);
        title=view.findViewById(R.id.titleAddAward);
        date=view.findViewById(R.id.addAwardYear);
        date.setEnabled(false);
        dateBtn=view.findViewById(R.id.toButtonAddAwards);
    }

    public void checkStatus()
    {
        uName=getArguments().getString("user");
        status=getArguments().getString("status");
        if(status.equals("update")) {
            title.setText(getArguments().getString("title"));
            date.setText(getArguments().getString("year"));
            description.setText(getArguments().getString("description"));
            addAward.setText("UPDATE");
        }
        else {
            addAward.setText("ADD");
        }
    }
    public void setYear()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog abc = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.setText(String.valueOf(year));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_WEEK));
        abc.show();
    }

    public int setTextColorRed()
    {
        int position=0; boolean dec=false;
        if(title.getText().toString().equals(""))
        {
            titleLabel.setTextColor(getResources().getColor(R.color.red));
            position=1;
            dec=true;
        }
        else
        {
            titleLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(date.getText().toString().equals(""))
        {
            dateLabel.setTextColor(getResources().getColor(R.color.red));
            if(dec==false)
                position=2;
        }
        else
        {
            dateLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        return position;
    }

    public void setCursorPosition(int position)
    {
        if(position==1) {
            title.requestFocus();
        }
        else if(position==2) {
            dateBtn.requestFocus();
            title.setCursorVisible(false);
        }
    }
    public  void storeDataToFirestore()
    {
        mStore=FirebaseFirestore.getInstance();
        if(status.equals("add")) {
            final UserAwards awards = new UserAwards(title.getText().toString(), date.getText().toString(),description.getText().toString());
            mStore.collection("Users").document("Student").collection(uName).document("Profile")
                    .collection("Awards").add(awards).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference aVoid) {

                }
            });
        }
        else if(status.equals("update"))
        {
            Map<String, Object> data = new HashMap<>();
            data.put("title",title.getText().toString());
            data.put("year",date.getText().toString());
            data.put("description",description.getText().toString());
            mStore.collection("Users").document("Student").collection(uName)
                    .document("Profile").collection("Awards").document(getArguments().getString("awardID"))
                    .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
    }

}
