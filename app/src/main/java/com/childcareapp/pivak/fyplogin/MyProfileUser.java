package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.childcareapp.pivak.fyplogin.Dialogs.EditIntroDialog;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.UserEducation;
import com.childcareapp.pivak.fyplogin.Models.UserExperience;
import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class MyProfileUser extends Fragment implements View.OnClickListener{
    ImageView img;
    TextView sName,headLine,uEmail,address,currentPosition,currentEducation;
    ProgressBar progressBar;
    public Button edit;
    String uName,status, profileType;
    FirebaseFirestore mStore;
    Bundle skillsBundle = new Bundle(), softwareBundle=new Bundle(), bundle = new Bundle();
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.my_profile_users, container, false);
        Button experience, education, awards, skills, softwares, posts, projects;
        projects = view.findViewById(R.id.projectHomeUser);
        experience = view.findViewById(R.id.experienceHomeUser);
        education = view.findViewById(R.id.educationHomeUser);
        awards = view.findViewById(R.id.honorsAndAwardsHomeUser);
        skills = view.findViewById(R.id.skillHomeUser);
        softwares = view.findViewById(R.id.softwaresHomeUser);
        posts = view.findViewById(R.id.postsHomeUser);
        currentEducation=view.findViewById(R.id.currentEducation);
        edit = view.findViewById(R.id.edit);

        projects.setOnClickListener(this);
        experience.setOnClickListener(this);
        education.setOnClickListener(this);
        awards.setOnClickListener(this);
        skills.setOnClickListener(this);
        softwares.setOnClickListener(this);
        posts.setOnClickListener(this);
        edit.setOnClickListener(this);

        uName= getArguments().getString("user");
        status=getArguments().getString("status");
//        progressBar=view.findViewById(R.id.userProfileProgress);
//        progressBar.setVisibility(View.VISIBLE);
        profileType=getArguments().getString("profileType");

        if(profileType.equals("search"))
        {
            edit.setVisibility(View.INVISIBLE);
        }
        else if(profileType.equals("myProfile"))
        {
            edit.setVisibility(View.VISIBLE);
        }

        loadPhoto();
        setBasicProfileInfo();
        setEducation();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Profile");


    }


    public void openEditDialog()
    {
        bundle.putString("userStatus", status);
        bundle.putString("user", uName);
        EditIntroDialog editIntroDialog = new EditIntroDialog();
        editIntroDialog.setArguments(bundle);
        editIntroDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Edit Introduction");

    }

    public void loadPhoto()
    {
        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName)
                .document("Profile").collection("Image").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                        @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if (queryDocumentSnapshots != null) {

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                Images imag = doc.toObject(Images.class);
                                URL url = null;
                                try {
                                    url = new URL(imag.getUrl());
                                } catch (MalformedURLException e1) {
                                    e1.printStackTrace();
                                }
                                img=view.findViewById(R.id.studentProfilePhoto);
                                Picasso.get().load(url.toString()).into(img);
                                return;
                            }
                        }
                        else
                        {
                            Log.i(TAG, "onEvent: Empty List");
                        }
                    }});
    }

    public void setTextviews(String name, String addersss, String email, String currentPositionn, String headlinee)
    {
        sName = view.findViewById(R.id.studentName);
        currentPosition = view.findViewById(R.id.currentPosition);
        headLine = view.findViewById(R.id.headline);
        uEmail = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);

        address.setText(addersss);
        if (!addersss.equals("")) {
            address.setText(addersss);
            address.setVisibility(View.VISIBLE);
        }
        else {
            address.setVisibility(View.GONE);
        }

        sName.setText(name);
        if (!currentPositionn.equals("")) {
            currentPosition.setText(currentPositionn);
            currentPosition.setVisibility(View.VISIBLE);
        }
        else {
            setExperience();
        }

        uEmail.setText(email);
        if (!email.equals("")) {
            uEmail.setText(email);
            uEmail.setVisibility(View.VISIBLE);
        }
        else {
            uEmail.setVisibility(View.GONE);
        }
        if (!headlinee.equals("")) {
            headLine.setText(headlinee);
            headLine.setVisibility(View.VISIBLE);
        }
        else {
            headLine.setVisibility(View.GONE);
        }

        if(currentPositionn.equals(""))
        {
            currentPosition.setVisibility(View.GONE);
        }
    }
    public void setBasicProfileInfo()
    {
        mStore=FirebaseFirestore.getInstance();
        DocumentReference docRef = mStore.collection("Users").document("Student").collection(uName)
                .document("Profile");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        UserModel userProfile = document.toObject(UserModel.class);
                        /// pass arguments to edit dialog
                        passArgumentsToEditDialog(userProfile.getfName(),userProfile.getlName(),userProfile.getEmail(),userProfile.getHeadline()
                                ,userProfile.getContact(),userProfile.getCountry(),userProfile.getCity(),userProfile.getZipCode());
                        // set textview values
                        setTextviews(userProfile.getfName()+ " " + userProfile.getlName()
                                ,userProfile.getCity() + "," + userProfile.getCountry()
                                ,userProfile.getEmail(),userProfile.getCurrentPosition()
                                , userProfile.getHeadline());
                        if(!userProfile.getSkills().equals("")) {
                            skillsBundle.putString("skills",userProfile.getSkills());
                        }
                        else
                            skillsBundle.putString("skills","");
                        if(!userProfile.getSoftwares().equals("")) {
                            softwareBundle.putString("softwares",userProfile.getSoftwares());
                        }
                        else {
                            softwareBundle.putString("softwares","");
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

    public void passArgumentsToEditDialog(String fName, String lName, String emaill, String headlinee, String contactt, String countryy, String cityy, String zipcodee)
    {
        bundle.putString("fName", fName);
        bundle.putString("lName", lName);
        bundle.putString("email", emaill);
        bundle.putString("headline", headlinee);
        bundle.putString("contact", contactt);
        bundle.putString("country", countryy);
        bundle.putString("city", cityy);
        bundle.putString("zipcode", zipcodee);
    }

    public void setEducation()
    {
        mStore.collection("Users").document("Student").collection(uName).document("Profile")
                .collection("Education").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots != null) {

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        UserEducation userEducation=doc.toObject(UserEducation.class);
                        // SET CURRENT EDUCATION
                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        String datee=userEducation.geteDate();
                        if( datee.equals("Current"))
                        {
                            currentEducation.setText("Student at "+ userEducation.getInstitution());
                            currentEducation.setVisibility(View.VISIBLE);
                        } else if (currentDate.compareTo(datee)>0)
                        {
                            currentEducation.setText("Student at "+ userEducation.getInstitution());
                            currentEducation.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            currentEducation.setVisibility(View.GONE);
                        }
                    }
                }
                else
                {
                    Log.i(TAG, "onEvent: Empty List");
                }
            }});
    }

    public void setExperience()
    {
        mStore.collection("Users").document("Student").collection(uName).document("Profile")
                .collection("Experience").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots != null) {

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        UserExperience studentExperience=doc.toObject(UserExperience.class);
                        ///// SET CURRENT POSITION
                        currentPosition=getActivity().findViewById(R.id.currentPosition);
                        if(currentPosition.getText().equals(""))
                        {
                            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                            String date = studentExperience.geteDate();
                            if (date.equals("Current")) {
                                currentPosition.setVisibility(View.VISIBLE);
                                currentPosition.setText(studentExperience.getDesignation() + " at " + studentExperience.getOrganization());
                            } else if (currentDate.compareTo(date) > 0) {
                                currentPosition.setVisibility(View.VISIBLE);
                                currentPosition.setText(studentExperience.getDesignation() + " at " + studentExperience.getOrganization());
                            }
                            else
                            {
                                currentPosition.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                else
                {
                    Log.i(TAG, "onEvent: Empty List");
                }
            }});
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.edit:
                openEditDialog();
                break;
            case R.id.experienceHomeUser:
                Bundle bundle2 = new Bundle();
                bundle2.putString("user", uName);
                bundle2.putString("userStatus", status);
                bundle2.putString("profileType", profileType);
                ViewExperienceUser viewExperienceUser = new ViewExperienceUser();
                viewExperienceUser.setArguments(bundle2);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack("profileUser");
                if(status.equals("Student")) {
                    fragmentTransaction.replace(R.id.content_student_home, viewExperienceUser);
                }
                else if(status.equals("Alumni"))
                {
                    fragmentTransaction.replace(R.id.content_alumni_home, viewExperienceUser);
                }
                else if(status.equals("Faculty"))
                {
                    fragmentTransaction.replace(R.id.content_faculty_home, viewExperienceUser);
                }
                else if(status.equals("Company"))
                {
                    fragmentTransaction.replace(R.id.content_company_home, viewExperienceUser);
                }
                fragmentTransaction.commit();
                break;
            case R.id.educationHomeUser:
                Bundle bundle3 = new Bundle();
                bundle3.putString("user", uName);
                bundle3.putString("userStatus", status);
                bundle3.putString("profileType", profileType);
                ViewEducationUser viewEducationUser = new ViewEducationUser();
                viewEducationUser.setArguments(bundle3);
                FragmentTransaction fragmentTransaction2 = getFragmentManager().beginTransaction();
                fragmentTransaction2.addToBackStack("profileUser");
                if(status.equals("Student")) {
                    fragmentTransaction2.replace(R.id.content_student_home, viewEducationUser);
                }
                else if(status.equals("Alumni"))
                {
                    fragmentTransaction2.replace(R.id.content_alumni_home, viewEducationUser);
                }
                else if(status.equals("Faculty"))
                {
                    fragmentTransaction2.replace(R.id.content_faculty_home, viewEducationUser);
                }
                else if(status.equals("Company"))
                {
                    fragmentTransaction2.replace(R.id.content_company_home, viewEducationUser);
                }
                fragmentTransaction2.commit();
                break;
            case R.id.skillHomeUser:
                skillsBundle.putString("user", uName);
                skillsBundle.putString("userStatus", status);
                skillsBundle.putString("profileType", profileType);
                ViewSkillsUser viewSkillsUser = new ViewSkillsUser();
                viewSkillsUser.setArguments(skillsBundle);
                FragmentTransaction fragmentTransaction5 = getFragmentManager().beginTransaction();
                fragmentTransaction5.addToBackStack("profileUser");
                if(status.equals("Student")) {
                    fragmentTransaction5.replace(R.id.content_student_home, viewSkillsUser);
                }
                else if(status.equals("Alumni"))
                {
                    fragmentTransaction5.replace(R.id.content_alumni_home, viewSkillsUser);
                }
                else if(status.equals("Faculty"))
                {
                    fragmentTransaction5.replace(R.id.content_faculty_home, viewSkillsUser);
                }
                else if(status.equals("Company"))
                {
                    fragmentTransaction5.replace(R.id.content_company_home, viewSkillsUser);
                }
                fragmentTransaction5.commit();
                break;
            case R.id.softwaresHomeUser:

                softwareBundle.putString("user", uName);
                softwareBundle.putString("userStatus", status);
                softwareBundle.putString("profileType", profileType);
                ViewSoftwareUser viewSoftwareUser= new ViewSoftwareUser();
                viewSoftwareUser.setArguments(softwareBundle);
                FragmentTransaction fragmentTransaction4 = getFragmentManager().beginTransaction();
                fragmentTransaction4.addToBackStack("profileUser");
                if(status.equals("Student")) {
                    fragmentTransaction4.replace(R.id.content_student_home, viewSoftwareUser);
                }
                else if(status.equals("Alumni"))
                {
                    fragmentTransaction4.replace(R.id.content_alumni_home, viewSoftwareUser);
                }
                else if(status.equals("Faculty"))
                {
                    fragmentTransaction4.replace(R.id.content_faculty_home, viewSoftwareUser);
                }
                else if(status.equals("Company"))
                {
                    fragmentTransaction4.replace(R.id.content_company_home, viewSoftwareUser);
                }
                fragmentTransaction4.commit();
                break;
            case R.id.postsHomeUser:
                Bundle bundle7 = new Bundle();
                bundle7.putString("user", uName);
                bundle7.putString("userStatus", status);
                ViewProfileUser viewProfileUser = new ViewProfileUser();
                viewProfileUser.setArguments(bundle7);
                FragmentTransaction fragmentTransaction6 = getFragmentManager().beginTransaction();
                fragmentTransaction6.addToBackStack(null);
                if(status.equals("Student")) {
                    fragmentTransaction6.replace(R.id.content_student_home, viewProfileUser);
                }
                else if(status.equals("Alumni"))
                {
                    fragmentTransaction6.replace(R.id.content_alumni_home, viewProfileUser);
                }
                else if(status.equals("Faculty"))
                {
                    fragmentTransaction6.replace(R.id.content_faculty_home, viewProfileUser);
                }
                else if(status.equals("Company"))
                {
                    fragmentTransaction6.replace(R.id.content_company_home, viewProfileUser);
                }
                fragmentTransaction6.commit();
                break;
            case R.id.honorsAndAwardsHomeUser:
                Bundle bundle4 = new Bundle();
                bundle4.putString("user", uName);
                bundle4.putString("userStatus", status);
                bundle4.putString("profileType", profileType);
                ViewAwardsUser viewAwardsUser = new ViewAwardsUser();
                viewAwardsUser.setArguments(bundle4);
                FragmentTransaction fragmentTransaction3 = getFragmentManager().beginTransaction();
                fragmentTransaction3.addToBackStack("profileUser");
                if(status.equals("Student")) {
                    fragmentTransaction3.replace(R.id.content_student_home, viewAwardsUser);
                }
                else if(status.equals("Alumni"))
                {
                    fragmentTransaction3.replace(R.id.content_alumni_home, viewAwardsUser);
                }
                else if(status.equals("Faculty"))
                {
                    fragmentTransaction3.replace(R.id.content_faculty_home, viewAwardsUser);
                }
                else if(status.equals("Company"))
                {
                    fragmentTransaction3.replace(R.id.content_company_home, viewAwardsUser);
                }
                fragmentTransaction3.commit();
                break;
            case R.id.projectHomeUser:
                Bundle bundle5 = new Bundle();
                bundle5.putString("user", uName);
                bundle5.putString("userStatus", status);
                bundle5.putString("profileType", profileType);
                ViewProjectsUser viewProjectsUser = new ViewProjectsUser();
                viewProjectsUser.setArguments(bundle5);
                fragmentTransaction4 = getFragmentManager().beginTransaction();
                fragmentTransaction4.addToBackStack("profileUser");
                if(status.equals("Student")) {
                    fragmentTransaction4.replace(R.id.content_student_home, viewProjectsUser);
                }
                else if(status.equals("Alumni"))
                {
                    fragmentTransaction4.replace(R.id.content_alumni_home, viewProjectsUser);
                }
                else if(status.equals("Faculty"))
                {
                    fragmentTransaction4.replace(R.id.content_faculty_home, viewProjectsUser);
                }
                else if(status.equals("Company"))
                {
                    fragmentTransaction4.replace(R.id.content_company_home, viewProjectsUser);
                }
                fragmentTransaction4.commit();
                break;
            default:
                break;
        }

    }
}
