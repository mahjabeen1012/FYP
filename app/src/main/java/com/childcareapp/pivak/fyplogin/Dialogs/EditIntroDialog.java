package com.childcareapp.pivak.fyplogin.Dialogs;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.UserExperience;
import com.childcareapp.pivak.fyplogin.MyProfileUser;
import com.childcareapp.pivak.fyplogin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by IDEAL on 11/19/2018.
 */

public class EditIntroDialog extends AppCompatDialogFragment {
    EditText firstName, lastName, headline,country,city,zipcode, contact,email;
    TextView fNameLabel, lNameLabel, cityLabel,countryLabel,emailLabel;
    FirebaseFirestore mStore;
    String uName,plName,pfName,pContact,pEmail,pCity,pCountry,pZipcode,pHeadline,imageLink;
    Spinner work;
    Button uploadImage,saveChanges,cancelDialog;
    ImageView img;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_intro_dialog, null);
        imageLink="";
        getArgumentss();        // get Arguments from previous activity
        bindResources(view);
        textChangeLitener();
        setPreviousValues();    // Load previous values in editTexts
        loadPhoto();            // Load previous image
        fillSpinner();      // Fill Spinner with current experiences

        // UPLOAD PHOTO
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImageBottomSheetDialog bottomSheetSelectImage = new SelectImageBottomSheetDialog();
                Bundle bundle2 = new Bundle();
                bundle2.putString("user", uName);
                bundle2.putString("imageLink",imageLink);
                bundle2.putString("status","Student");
                bottomSheetSelectImage.setArguments(bundle2);
                bottomSheetSelectImage.show(getActivity().getSupportFragmentManager(), "Edit Image");

            }
        });

        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPreviousFragment();
                dismiss();
            }
        });
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstName.getText().toString().equals("")|| lastName.getText().toString().equals("") || city.getText().toString().equals("")
                        || country.getText().toString().equals("") || email.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Enter all the required fields", Toast.LENGTH_SHORT).show();
                    setTextColor();
                }
                else {
                    storeDataToFirestore();
                    Toast.makeText(getContext(), "Profile updated Successfully", Toast.LENGTH_SHORT).show();
                    loadPreviousFragment();
                    dismiss();
                }
            }
        });
        builder.setView(view).setTitle("Edit Profile");

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.darkGrey));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkGrey));
            }
        });

        dialog.show();
        return dialog;
    }

    public  void textChangeLitener()
    {
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0)
                {
                    fNameLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    fNameLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0)
                {
                    lNameLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    lNameLabel.setTextColor(getResources().getColor(R.color.darkGrey));
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
                if(count==0)
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

        country.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0)
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

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0)
                {
                    emailLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    emailLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public  void loadPreviousFragment()
    {
        FragmentManager fm = getActivity().getFragmentManager();
        fm.popBackStack (null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        String uStatus=getArguments().getString("userStatus");
        Bundle bundle4 = new Bundle();
        bundle4.putString("user", uName);
        bundle4.putString("status", uStatus);
        bundle4.putString("profileType","myProfile");
        MyProfileUser myProfileUser = new MyProfileUser();
        myProfileUser.setArguments(bundle4);
        FragmentTransaction fragmentTransaction3 = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction3.addToBackStack(null);
        if(uStatus.equals("Student")) {
            fragmentTransaction3.replace(R.id.content_student_home, myProfileUser);
        }
        else if(uStatus.equals("Alumni"))
        {
            fragmentTransaction3.replace(R.id.content_alumni_home, myProfileUser);
        }
        else if(uStatus.equals("Faculty"))
        {
            fragmentTransaction3.replace(R.id.content_faculty_home, myProfileUser);
        }
        fragmentTransaction3.commit();
    }

    public void getArgumentss()
    {
        uName=getArguments().getString("user");
        plName=getArguments().getString("lName");
        pfName=getArguments().getString("fName");
        pCountry=getArguments().getString("country");
        pCity=getArguments().getString("city");
        pZipcode=getArguments().getString("zipcode");
        pHeadline=getArguments().getString("headline");
        pContact=getArguments().getString("contact");
        pEmail=getArguments().getString("email");
    }
    public void bindResources(View view)
    {
        saveChanges=view.findViewById(R.id.saveChanges);
        cancelDialog=view.findViewById(R.id.CancelChanges);
        img=view.findViewById(R.id.editDialogImageview);
        firstName=view.findViewById(R.id.firstNameEdit);
        lastName=view.findViewById(R.id.lastNameEdit);
        headline=view.findViewById(R.id.headlineEdit);
        country=view.findViewById(R.id.coutryEdit);
        city=view.findViewById(R.id.cityEdit);
        zipcode=view.findViewById(R.id.zipcodeEdit);
        contact=view.findViewById(R.id.contactNumberEdit);
        email=view.findViewById(R.id.emailEdit);
        work=view.findViewById(R.id.currentPositionEdit);
        uploadImage=view.findViewById(R.id.uploadImage);
        fNameLabel=view.findViewById(R.id.fNameLabel);
        lNameLabel=view.findViewById(R.id.lNameLabel);
        cityLabel=view.findViewById(R.id.cityProfileEditLabel);
        countryLabel=view.findViewById(R.id.countryProfileEditLabel);
        emailLabel=view.findViewById(R.id.emailProfileEditLabel);

    }
    public void fillSpinner()
    {
        mStore.collection("Users").document("Student").collection(uName).document("Profile").collection("Experience").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                {
                    // Toast.makeText(getActivity(), "Empty List", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    final List<String> experiences = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
                    {
                        if (documentSnapshot.exists())
                        {
                            String idd=documentSnapshot.getId();
                            DocumentReference docRef = mStore.collection("Users").document("Student").collection(uName).document("Profile").collection("Experience").document(idd);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists())
                                        {
                                            ///////////////
                                            UserExperience studentExperience=document.toObject(UserExperience.class);
                                            ///// SET CURRENT POSITION
                                            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                            String date=studentExperience.geteDate().toString();
                                            if( date.equals("Current"))
                                            {
                                                experiences.add(studentExperience.getDesignation()+" at "+ studentExperience.getOrganization());
                                            }
                                            else if (currentDate.compareTo(date) > 0)
                                            {
                                                experiences.add(studentExperience.getDesignation()+" at "+ studentExperience.getOrganization());
                                            }
                                            /////////////

                                            if(getActivity()!=null) {
                                                final ArrayAdapter<String> classes = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, experiences);
                                                classes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                work.setAdapter(classes);
                                            }
                                        }
                                        else
                                        {
                                            // Toast.makeText(getActivity(), "Document doesn't exist", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        // Toast.makeText(getActivity(), "Failed to get data", Toast.LENGTH_SHORT).show();
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
                //   Toast.makeText(getActivity(), "Failed to get Data", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void setPreviousValues()
    {
        firstName.setText(pfName);
        lastName.setText(plName);
        headline.setText(pHeadline);
        country.setText(pCountry);
        city.setText(pCity);
        zipcode.setText(pZipcode);
        contact.setText(pContact);
        email.setText(pEmail);
    }
    public void loadPhoto()
    {
        mStore=FirebaseFirestore.getInstance();
        mStore.collection("Users").document("Student").collection(uName).document("Profile").collection("Image").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                {
                    // Toast.makeText(getActivity(), "Empty List", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
                    {
                        if (documentSnapshot.exists())
                        {
                            String idd=documentSnapshot.getId();
                            imageLink=idd;
                            //Toast.makeText(getActivity(), documentSnapshot.getId().toString(), Toast.LENGTH_LONG).show();
                            ///////////// Nested query to get Image
                            DocumentReference imgRef = mStore.collection("Users").document("Student").collection(uName).document("Profile").collection("Image").document(idd);
                            imgRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists())
                                        {
                                            ///////////////
                                            Images imag=document.toObject(Images.class);
                                            URL url = null;
                                            try
                                            {
                                                url=new URL(imag.getUrl());
                                            }
                                            catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            Picasso.get().load(url.toString()).into(img);
                                        }
                                        else
                                        {
                                            //Toast.makeText(getActivity(), "Image doesn't exist", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        // Toast.makeText(getActivity(), "Failed to get Image", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            //// end of nested query

                        }
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Toast.makeText(getActivity(), "Failed to get Image", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void storeDataToFirestore()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("fName",firstName.getText().toString());
        data.put("lName",lastName.getText().toString());
        data.put("headline",headline.getText().toString());
        data.put("country",country.getText().toString());
        data.put("city",city.getText().toString());
        data.put("zipCode",zipcode.getText().toString());
        data.put("contact",contact.getText().toString());
        data.put("email",email.getText().toString());
        if(work.getCount()!=0) {
            data.put("currentPosition", work.getSelectedItem().toString());
        }
        else
        {
            data.put("currentPosition", "");
        }
        mStore=FirebaseFirestore.getInstance();
        mStore.collection("Users").document("Student").collection(uName).document("Profile").update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });

    }
    public void setTextColor()
    {
        boolean setPosition=false;
        if(firstName.getText().toString().equals(""))
        {
            fNameLabel.setTextColor(getResources().getColor(R.color.red));
            firstName.requestFocus();
            setPosition=true;
        }
        else
        {
            fNameLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(lastName.getText().toString().equals(""))
        {
            lNameLabel.setTextColor(getResources().getColor(R.color.red));
            if(setPosition==false)
            {
                lastName.requestFocus();
                setPosition=true;
            }
        }
        else
        {
            lNameLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(city.getText().toString().equals(""))
        {
            cityLabel.setTextColor(getResources().getColor(R.color.red));
            if(setPosition==false)
            {
                city.requestFocus();
                setPosition=true;
            }
        }
        else
        {
            cityLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(country.getText().toString().equals(""))
        {
            countryLabel.setTextColor(getResources().getColor(R.color.red));
            if(setPosition==false)
            {
                country.requestFocus();
                setPosition=true;
            }
        }
        else
        {
            countryLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(email.getText().toString().equals(""))
        {
            emailLabel.setTextColor(getResources().getColor(R.color.red));
            if(setPosition==false)
            {
                email.requestFocus();
            }
        }
        else
        {
            emailLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
    }
}
