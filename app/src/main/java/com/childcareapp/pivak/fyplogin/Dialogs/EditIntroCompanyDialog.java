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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.MyProfileCompany;
import com.childcareapp.pivak.fyplogin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EditIntroCompanyDialog extends AppCompatDialogFragment {

    TextView nameLabel,countryLabel,cityLabel,zipcodeLabel,emailLabel,contactLabel,facebookLabel,linkedInLabel,twiterLabel,websiteLabel;
    EditText name,country,city,zipcode,email,contact,facebook,linkedIn,twiter,website;
    String uName,pName,pCountry,pCity,pZipcode,pEmail,pContact,pFacebook,pLinkedIn,pTwiter,pWebsite,imageLink;
    Button uploadImage,cancelBtn,updateBtn;
    ImageView profileImage;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_intro_company_dialog, null);
        uploadImage=view.findViewById(R.id.uploadImageCompany);
        imageLink="";
        getArgumentss();
        bindResources(view);
        textChangeLitener();
        setPreviousValues();
        loadPhoto();

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImageBottomSheetDialog bottomSheetSelectImage = new SelectImageBottomSheetDialog();
                Bundle bundle2 = new Bundle();
                bundle2.putString("user", uName);
                bundle2.putString("status","Company");
                bundle2.putString("profileType",getArguments().getString("profileType"));
                bundle2.putString("imageLink",imageLink);
                bottomSheetSelectImage.setArguments(bundle2);
                bottomSheetSelectImage.show(getActivity().getSupportFragmentManager(), "Edit Image");
                //dismiss();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("") || website.getText().toString().equals("") || email.getText().toString().equals("")
                        || city.getText().toString().equals("") || country.getText().toString().equals(""))
                {
                    Toast.makeText(getContext(), "Enter all the required fields", Toast.LENGTH_SHORT).show();
                    setTextColor();
                }
                else
                {
                    storeDataToFirestore();
                    Toast.makeText(getContext(), "Profile updated Successfully", Toast.LENGTH_SHORT).show();
                    loadPreviousFragment();
                    dismiss();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPreviousFragment();
                dismiss();
            }
        });

        builder.setView(view).setTitle("Edit Introduction");
        final AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public  void loadPreviousFragment()
    {
        FragmentManager fm = getActivity().getFragmentManager();
        fm.popBackStack (null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        String uStatus=getArguments().getString("userStatus");
        Bundle bundle4 = new Bundle();
        bundle4.putString("user", uName);
        bundle4.putString("userStatus", uStatus);
        bundle4.putString("profileType",getArguments().getString("profileType"));
        MyProfileCompany myProfileCompany = new MyProfileCompany();
        myProfileCompany.setArguments(bundle4);
        FragmentTransaction fragmentTransaction3 = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction3.addToBackStack(null);
        fragmentTransaction3.replace(R.id.content_company_home, myProfileCompany);
        fragmentTransaction3.commit();
    }
    public void getArgumentss()
    {
        uName=getArguments().getString("user");
        pName=getArguments().getString("name");
        pCountry=getArguments().getString("country");
        pCity=getArguments().getString("city");
        pZipcode=getArguments().getString("zipcode");
        pContact=getArguments().getString("contact");
        pEmail=getArguments().getString("email");
        pFacebook=getArguments().getString("facebook");
        pTwiter=getArguments().getString("twiter");
        pLinkedIn=getArguments().getString("linkedIn");
        pWebsite=getArguments().getString("website");
    }
    public void bindResources(View view)
    {
        updateBtn=view.findViewById(R.id.saveChangesCompany);
        cancelBtn=view.findViewById(R.id.CancelChangesCompany);
        profileImage=view.findViewById(R.id.editDialogImageviewCompany);
        name=view.findViewById(R.id.companyNameEdit);
        country=view.findViewById(R.id.coutryEditCompany);
        city=view.findViewById(R.id.cityEditCompany);
        zipcode=view.findViewById(R.id.zipcodeEditCompany);
        contact=view.findViewById(R.id.contactNumberEditCompany);
        email=view.findViewById(R.id.emailEditCompany);
        facebook=view.findViewById(R.id.facebookEditCompany);
        twiter=view.findViewById(R.id.twitterEditCompany);
        linkedIn=view.findViewById(R.id.linkedinEditCompany);
        website=view.findViewById(R.id.websiteEditCompany);

        nameLabel=view.findViewById(R.id.companyNameLabel);
        countryLabel=view.findViewById(R.id.comapnyCountryLabel);
        cityLabel=view.findViewById(R.id.companyCityLabel);
        emailLabel=view.findViewById(R.id.companyEmailLabel);
        websiteLabel=view.findViewById(R.id.companyWebsiteLabel);


    }
    public  void textChangeLitener()
    {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0)
                {
                    nameLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    nameLabel.setTextColor(getResources().getColor(R.color.darkGrey));
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

        website.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0)
                {
                    websiteLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    websiteLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void setPreviousValues()
    {
        name.setText(pName);
        country.setText(pCountry);
        city.setText(pCity);
        zipcode.setText(pZipcode);
        contact.setText(pContact);
        email.setText(pEmail);
        facebook.setText(pFacebook);
        twiter.setText(pTwiter);
        website.setText(pWebsite);
        linkedIn.setText(pLinkedIn);
    }
    public void loadPhoto()
    {
        FirebaseFirestore.getInstance().collection("Users").document("Company").collection(uName).document("Profile").collection("Image").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                            FirebaseFirestore.getInstance().collection("Users").document("Company").
                                    collection(uName).document("Profile").collection("Image").document(idd)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                            Picasso.get().load(url.toString()).into(profileImage);
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
        data.put("name",name.getText().toString());
        data.put("country",country.getText().toString());
        data.put("city",city.getText().toString());
        data.put("zipCode",zipcode.getText().toString());
        data.put("contact",contact.getText().toString());
        data.put("email",email.getText().toString());
        data.put("facebook",facebook.getText().toString());
        data.put("twiter",twiter.getText().toString());
        data.put("linkedIn",linkedIn.getText().toString());
        data.put("website",website.getText().toString());

        FirebaseFirestore.getInstance().collection("Users").document("Company").collection(uName)
                .document("Profile").update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });

    }
    public void setTextColor()
    {
        if(name.getText().toString().equals(""))
        {
            nameLabel.setTextColor(getResources().getColor(R.color.red));
        }
        else
        {
            nameLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(country.getText().toString().equals(""))
        {
            countryLabel.setTextColor(getResources().getColor(R.color.red));
        }
        else
        {
            countryLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(city.getText().toString().equals(""))
        {
            cityLabel.setTextColor(getResources().getColor(R.color.red));
        }
        else
        {
            cityLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(email.getText().toString().equals(""))
        {
            emailLabel.setTextColor(getResources().getColor(R.color.red));
        }
        else
        {
            emailLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
        if(website.getText().toString().equals(""))
        {
            websiteLabel.setTextColor(getResources().getColor(R.color.red));
        }
        else
        {
            websiteLabel.setTextColor(getResources().getColor(R.color.darkGrey));
        }
    }
}
