package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.childcareapp.pivak.fyplogin.Dialogs.EditIntroCompanyDialog;
import com.childcareapp.pivak.fyplogin.Models.CompanyModel;
import com.childcareapp.pivak.fyplogin.Models.Images;
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

import static android.content.ContentValues.TAG;

public class MyProfileCompany extends Fragment implements View.OnClickListener{
    Button editIntroCompany, jobOppertunity;
    ImageView profilePhoto;
    TextView companyName,contact,email,locationComapny,facebookLink,linkedInLink,websiteLink,twiterLink;
    String uName,status,profileType;
    Bundle bundle=new Bundle();

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.my_profile_company, container, false);
        jobOppertunity = view.findViewById(R.id.jobOppertunityHomeCompany);
        jobOppertunity.setOnClickListener(this);
        editIntroCompany=view.findViewById(R.id.editIntroCompany);
        editIntroCompany.setOnClickListener(this);
        facebookLink=view.findViewById(R.id.facebookCompany);
        twiterLink=view.findViewById(R.id.twiterCompany);
        linkedInLink=view.findViewById(R.id.linkedinCompany);
        websiteLink=view.findViewById(R.id.websiteCompany);
        linkedInLink.setOnClickListener(this);
        twiterLink.setOnClickListener(this);
        facebookLink.setOnClickListener(this);
        websiteLink.setOnClickListener(this);

        uName= getArguments().getString("user");
        status=getArguments().getString("status");
        profileType=getArguments().getString("profileType");
        if(profileType.equals("search"))
        {
            editIntroCompany.setVisibility(View.INVISIBLE);
        }
        else if(profileType.equals("myProfile"))
        {
            editIntroCompany.setVisibility(View.VISIBLE);
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setBasicProfileInfo();
                loadImage();
            }
        }, 1000);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Profile");
    }

    public void openEditDialog()
    {
        bundle.putString("user", uName);
        bundle.putString("userStatus", status);
        bundle.putString("profileType", profileType);
        EditIntroCompanyDialog editIntroDialog = new EditIntroCompanyDialog();
        editIntroDialog.setArguments(bundle);
        editIntroDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Edit Introduction");
    }

    public void setBasicProfileInfo()
    {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document("Company").collection(uName).document("Profile");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        CompanyModel companyProfile = document.toObject(CompanyModel.class);
                        /// pass arguments to edit dialog
                        bundle.putString("name", companyProfile.getName().toString());
                        bundle.putString("city",  companyProfile.getCity().toString());
                        bundle.putString("country", companyProfile.getCountry().toString());
                        bundle.putString("zipcode", companyProfile.getZipCode().toString() );
                        bundle.putString("email", companyProfile.getEmail().toString() );
                        bundle.putString("contact", companyProfile.getContact().toString());
                        bundle.putString("facebook", companyProfile.getFacebook().toString());
                        bundle.putString("twiter", companyProfile.getTwiter().toString());
                        bundle.putString("linkedIn", companyProfile.getLinkedIn().toString());
                        bundle.putString("website", companyProfile.getWebsite().toString());

                        // set textview values
                        companyName=view.findViewById(R.id.companyName);
                        companyName.setText(companyProfile.getName().toString());
                        contact=view.findViewById(R.id.contactNumberCompany);
                        if(companyProfile.getContact().equals(""))
                        {
                            contact.setVisibility(View.GONE);
                        }
                        else
                        {
                            contact.setVisibility(View.VISIBLE);
                            contact.setText(companyProfile.getContact());
                        }
                        email=view.findViewById(R.id.emailCompany);
                        if(companyProfile.getEmail().equals(""))
                        {
                            email.setVisibility(View.GONE);
                        }
                        else
                        {
                            email.setVisibility(View.VISIBLE);
                            email.setText(companyProfile.getEmail());
                        }

                        locationComapny=view.findViewById(R.id.addressCompany);

                        if(companyProfile.getCity().equals("") && companyProfile.getCountry().equals(""))
                        {
                            locationComapny.setVisibility(View.GONE);
                        }
                        else
                        {
                            locationComapny.setVisibility(View.VISIBLE);
                            locationComapny.setText(companyProfile.getCity()+","+ companyProfile.getCountry());
                        }

                        if(companyProfile.getFacebook().equals(""))
                        {
                            facebookLink.setVisibility(View.GONE);
                        }
                        else
                        {
                            facebookLink.setVisibility(View.VISIBLE);
                            facebookLink.setText(companyProfile.getFacebook());
                        }

                        if(companyProfile.getTwiter().toString().equals(""))
                        {
                            twiterLink.setVisibility(View.GONE);
                        }
                        else
                        {
                            twiterLink.setVisibility(View.VISIBLE);
                            twiterLink.setText(companyProfile.getTwiter());
                        }

                        if(companyProfile.getLinkedIn().toString().equals(""))
                        {
                            linkedInLink.setVisibility(View.GONE);
                        }
                        else
                        {
                            linkedInLink.setVisibility(View.VISIBLE);
                            linkedInLink.setText(companyProfile.getLinkedIn());
                        }

                        if(companyProfile.getWebsite().toString().equals(""))
                        {
                            websiteLink.setVisibility(View.GONE);
                        }
                        else
                        {
                            websiteLink.setVisibility(View.VISIBLE);
                            websiteLink.setText(companyProfile.getWebsite());
                        }

                    }
                    else {
                        Log.i(TAG, "Profile: Document doesn't exist");
                    }
                }
                else {
                    Log.i(TAG, "Profile: Failed to get data");
                }
            }
        });
    }

    public void loadImage()
    {
        FirebaseFirestore.getInstance().collection("Users").document("Company").collection(uName).document("Profile").collection("Image").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                {
                    //Toast.makeText(getActivity(), "Empty List", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
                    {
                        if (documentSnapshot.exists())
                        {
                            String idd=documentSnapshot.getId();
                            ///////////// Nested query to get Image
                            profilePhoto=getActivity().findViewById(R.id.companyProfileImage);
                            FirebaseFirestore.getInstance().collection("Users").document("Company").collection(uName).
                                    document("Profile").collection("Image").document(idd)
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
                                            Picasso.get().load(url.toString()).into(profilePhoto);
                                        }
                                        else
                                        {
                                            Log.i(TAG, "onComplete: Image doesn't exist");
                                        }
                                    }
                                    else
                                    {
                                        Log.i(TAG, "onComplete: Failed to get Image");
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
                Log.i(TAG, "onFailure: Failed to get Image");

            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editIntroCompany:
                openEditDialog();
                break;
            case R.id.jobOppertunityHomeCompany:
                break;
            case R.id.websiteCompany:
                if(!websiteLink.getText().toString().equals("")) {
                    String link=websiteLink.getText().toString();
                    if (!link.startsWith("http://") && !link.startsWith("https://"))
                        link = "http://" + link;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserIntent);
                }
                break;
            case R.id.linkedinCompany:
                if(!linkedInLink.getText().toString().equals("")) {
                    String link=linkedInLink.getText().toString();
                    if (!link.startsWith("http://") && !link.startsWith("https://"))
                        link = "http://" + link;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserIntent);
                }
                break;
            case R.id.twiterCompany:
                if(!twiterLink.getText().toString().equals("")) {
                    String link=twiterLink.getText().toString();
                    if (!link.startsWith("http://") && !link.startsWith("https://"))
                        link = "http://" + link;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserIntent);
                }
                break;
            case R.id.facebookCompany:
                if(!facebookLink.getText().toString().equals("")) {
                    String link=facebookLink.getText().toString();
                    if (!link.startsWith("http://") && !link.startsWith("https://"))
                        link = "http://" + link;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserIntent);
                }
                break;
            default:
                break;
        }
    }

}
