package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewAwardCV;
import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewEducationCV;
import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewExperienceCV;
import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewProjectCV;
import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewSkillsCV;
import com.childcareapp.pivak.fyplogin.Models.CVData;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.UserAwards;
import com.childcareapp.pivak.fyplogin.Models.UserEducation;
import com.childcareapp.pivak.fyplogin.Models.UserExperience;
import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.childcareapp.pivak.fyplogin.Models.UserProjects;
import com.example.circulardialog.CDialog;
import com.example.circulardialog.extras.CDConstants;
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
import com.lmntrx.android.library.livin.missme.ProgressDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CVFragment extends Fragment {

    ListView listviewEducation;
    ListView listviewExperience;
    ListView listviewAward;
    ListView listviewProjects;
    GridView gridviewSkills;
    GridView girdviewSoftwares;
    ImageView cvImage;
    TextView cvName,cvAdress,cvContact,cvEmail,aboutMe,cvSoftwares,cvSkills,cvExperiments, cvProjects,cvAwards, aboutmeLabel;

    List<CVData> educationList;
    List<CVData> skillsList;
    List<CVData> awardList;
    List<CVData> softwaresList;
    List<CVData> experienceList;
    List<CVData> projectsList;
    int totalEcucationObjs=0;
    int totalExperienceObjs=0;
    int totalProjects=0;
    int totalSkills=0;
    int totalSoftwares=0;
    int totalAwards=0;
    CVData data;
    String uName,status;
    View view;
    ListAdapter listAdapterrrrr;

    ScrollView linearLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cv_fragment, container, false);

        uName=getArguments().getString("user");
        status=getArguments().getString("status");
        listviewEducation = view.findViewById(R.id.listview_education_cv);
        listviewExperience = view.findViewById(R.id.listview_experience_cv);
        girdviewSoftwares = view.findViewById(R.id.gridview_softwares_cv);
        gridviewSkills = view.findViewById(R.id.gridview_skills_cv);
        listviewProjects = view.findViewById(R.id.listview_projects_cv);
        listviewAward = view.findViewById(R.id.listview_awards_cv);

        setListviews();
        loadCV();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    layoutToImage(view);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 4000);

        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public void setListviews()
    {
        loadImage();
    }
    public void populateEducationListView(String degree, String inst,String duration,String location)
    {
        data = new CVData(inst+", "+location, degree, duration);
        educationList.add(data);
        ListViewEducationCV listEducation = new ListViewEducationCV(getActivity(), R.layout.listview_education_cv, educationList);
        listviewEducation.setAdapter(listEducation);
        ListAdapter listAdapterrr = listviewEducation.getAdapter();
        if (listAdapterrr == null) {

        } else {
            int totalHeight = 0;
            for (int i = 0; i < listAdapterrr.getCount(); i++) {
                View listItem = listAdapterrr.getView(i, null, listviewEducation);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listviewEducation.getLayoutParams();
            params.height = totalHeight + (listviewEducation.getDividerHeight() * (listAdapterrr.getCount() - 1));
            listviewEducation.setLayoutParams(params);
            listviewEducation.requestLayout();
        }
        if(listEducation.getCount()==totalEcucationObjs)
        {
            setExperienceData();
        }
    }

    public  void setEducationData()
    {
        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName)
                .document("Profile").collection("Education").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    educationList = new ArrayList<>();
                    totalEcucationObjs=queryDocumentSnapshots.size();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        UserEducation userEducation=doc.toObject(UserEducation.class);
                        //// Set listview
                        String[] sYear=userEducation.getsDate().split("-");
                        String endDate="";
                        if(userEducation.geteDate().equals("Current"))
                        {
                            endDate="Current";
                        }
                        else {
                            String[] eYear = userEducation.geteDate().split("-");
                            endDate=eYear[2];
                        }
                        populateEducationListView(userEducation.getDegree(), userEducation.getInstitution(),
                                sYear[2]+" to "+endDate,
                                userEducation.getCity()+","+userEducation.getCountry());
                    }
                }
                else
                {
                    Log.i(TAG, "onEvent: Empty List");
                }
            }});
    }

    public void populateExperienceListview(String designation, String organization,String duration,String location, String description)
    {
        data = new CVData(duration, designation, organization+", "+location, description);
        experienceList.add(data);
        ListViewExperienceCV listExperience = new ListViewExperienceCV(getActivity(), R.layout.listview_experience_cv, experienceList);
        listviewExperience.setAdapter(listExperience);
        ListAdapter listAdapterrrr = listviewExperience.getAdapter();
        if (listAdapterrrr == null) {

        } else {
            int totalHeight = 0;
            for (int i = 0; i < listAdapterrrr.getCount(); i++) {
                View listItem = listAdapterrrr.getView(i, null, listviewExperience);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listviewExperience.getLayoutParams();
            params.height = totalHeight + (listviewExperience.getDividerHeight() * (listAdapterrrr.getCount() - 1));
            listviewExperience.setLayoutParams(params);
            listviewExperience.requestLayout();
        }
        if(listExperience.getCount()==totalExperienceObjs)
        {
            setAwardsData();
        }
    }
    public void setExperienceData()
    {
        cvExperiments=view.findViewById(R.id.cvExperienceLabel);
        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName)
                .document("Profile").collection("Experience").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    cvExperiments.setVisibility(View.VISIBLE);
                    totalExperienceObjs=queryDocumentSnapshots.size();
                    experienceList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        UserExperience studentExperience=doc.toObject(UserExperience.class);

                        populateExperienceListview(studentExperience.getDesignation(), studentExperience.getOrganization(),
                                studentExperience.getsDate()+" to "+ studentExperience.geteDate(),
                                studentExperience.getCity()+","+studentExperience.getCountry(),
                                studentExperience.getDescription());
                    }
                }
                else
                {
                    Log.i(TAG, "onEvent: Empty List");
                }
            }});
    }

    public  void populateSoftwaresGridview(String softwares, String skills)
    {

        softwaresList = new ArrayList<>();
        String[] software=softwares.split(",");
        totalSoftwares=0;
        for(int a=1; a<software.length; a++)
        {
            data = new CVData(software[a]);
            softwaresList.add(data);
            totalSoftwares++;
        }
        ListViewSkillsCV listSoftwares = new ListViewSkillsCV(getActivity(), R.layout.listview_skills_cv, softwaresList);
        girdviewSoftwares.setAdapter(listSoftwares);
        ListAdapter listAdapterr = girdviewSoftwares.getAdapter();
        if (listAdapterr == null) {

        } else {

            int totalHeight = 0;
            for (int i = 0; i < listAdapterr.getCount(); i++) {
                View listItem = listAdapterr.getView(i, null, girdviewSoftwares);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = girdviewSoftwares.getLayoutParams();
            params.height = totalHeight + (girdviewSoftwares.getHeight() * (listAdapterr.getCount() - 1));
            girdviewSoftwares.setLayoutParams(params);
            girdviewSoftwares.requestLayout();
        }
        if(girdviewSoftwares.getCount()==totalSoftwares)
        {
            if(skills.equals("")) {
                cvSkills.setVisibility(View.INVISIBLE);
                setEducationData();
            }
            else {
                cvSkills.setVisibility(View.VISIBLE);
                populateSkillsGridview(skills);
            }
        }
    }
    public void populateSkillsGridview(String skills)
    {
        skillsList = new ArrayList<>();
        String[] skill=skills.split(",");
        totalSkills=0;
        for(int a=1; a<skill.length; a++)
        {
            data = new CVData(skill[a]);
            skillsList.add(data);
            totalSkills++;
        }
        ListViewSkillsCV listSkills = new ListViewSkillsCV(getActivity(), R.layout.listview_skills_cv, skillsList);
        gridviewSkills.setAdapter(listSkills);
        ListAdapter listAdapter = gridviewSkills.getAdapter();
        if (listAdapter == null) {

        } else {
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, gridviewSkills);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = gridviewSkills.getLayoutParams();
            params.height = totalHeight + (gridviewSkills.getHeight() * (listAdapter.getCount() - 1));
            gridviewSkills.setLayoutParams(params);
            gridviewSkills.requestLayout();
        }
        if(gridviewSkills.getCount()==totalSkills)
        {
            setEducationData();
        }
    }
    public void setBasicInfoSoftwaresAndSkills()
    {
        cvAdress=view.findViewById(R.id.cvAddress);
        cvName=view.findViewById(R.id.cvName);
        cvEmail=view.findViewById(R.id.cvEmail);
        aboutMe=view.findViewById(R.id.aboutMeCV);
        cvContact=view.findViewById(R.id.cvContactNumber);
        cvSoftwares=view.findViewById(R.id.cvSoftwareLabel);
        cvSkills=view.findViewById(R.id.cvSkillsLabel);
        aboutmeLabel=view.findViewById(R.id.aboutMeLabel);

        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName)
                .document("Profile").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UserModel userData = document.toObject(UserModel.class);
                        cvAdress.setText(userData.getCity()+", "+userData.getCountry());
                        cvName.setText(userData.getfName()+" "+userData.getlName());
                        cvEmail.setText(userData.getEmail());
                        if(userData.getHeadline().equals(""))
                        {
                            aboutmeLabel.setVisibility(View.INVISIBLE);
                        }
                        else {
                            aboutmeLabel.setVisibility(View.VISIBLE);
                            aboutMe.setText(userData.getHeadline());
                        }
                        if(userData.getContact().equals(""))
                        {
                            cvContact.setVisibility(View.INVISIBLE);
                        }
                        else {
                            cvContact.setVisibility(View.VISIBLE);
                            cvContact.setText(userData.getContact());
                        }
                        if(userData.getSoftwares().equals(""))
                        {
                            cvSoftwares.setVisibility(View.INVISIBLE);
                        }
                        else {
                            cvSoftwares.setVisibility(View.VISIBLE);
                            populateSoftwaresGridview(userData.getSoftwares(), userData.getSkills());
                        }
//                        if(userData.getSkills().equals(""))
//                        {
//                            cvSkills.setVisibility(View.INVISIBLE);
//                        }
//                        else {
//                            cvSkills.setVisibility(View.VISIBLE);
//                            populateSkillsGridview(userData.getSkills());
//                        }
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

    public void populateProjectsListview(String title, String description)
    {
        data = new CVData(title,  description);
        projectsList.add(data);
        ListViewProjectCV listProject = new ListViewProjectCV(getActivity(), R.layout.listview_project_cv, projectsList);
        listviewProjects.setAdapter(listProject);
        listAdapterrrrr = listviewProjects.getAdapter();
        if (listAdapterrrrr == null) {

        } else {
            int totalHeight = 0;
            for (int i = 0; i < listAdapterrrrr.getCount(); i++) {
                View listItem = listAdapterrrrr.getView(i, null, listviewProjects);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listviewProjects.getLayoutParams();
            params.height = totalHeight + (listviewProjects.getDividerHeight() * (listAdapterrrrr.getCount() - 1));
            listviewProjects.setLayoutParams(params);
            listviewProjects.requestLayout();
        }
    }
    public void setProjectsData()
    {
        cvProjects=view.findViewById(R.id.cvProjectsLabel);
        listviewProjects.setAdapter(null);
        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName)
                .document("Profile").collection("Projects").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    cvProjects.setVisibility(View.VISIBLE);
                    totalProjects=queryDocumentSnapshots.size();
                    projectsList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        UserProjects projects=doc.toObject(UserProjects.class);
                        populateProjectsListview(projects.getTitle(), projects.getDescription());
                    }
                }
                else
                {
                    Log.i(TAG, "onEvent: Empty List");
                }
            }});
    }
    public void populateAwardsListview(String title, String year, String description)
    {
        data = new CVData(year, title, description,  1);
        awardList.add(data);
        ListViewAwardCV listAward = new ListViewAwardCV(getActivity(), R.layout.listview_awards_cv, awardList);
        listviewAward.setAdapter(listAward);
        listAdapterrrrr = listviewAward.getAdapter();
        if (listAdapterrrrr == null) {

        } else {
            int totalHeight = 0;
            for (int i = 0; i < listAdapterrrrr.getCount(); i++) {
                View listItem = listAdapterrrrr.getView(i, null, listviewAward);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listviewAward.getLayoutParams();
            params.height = totalHeight + (listviewAward.getDividerHeight() * (listAdapterrrrr.getCount() - 1));
            listviewAward.setLayoutParams(params);
            listviewAward.requestLayout();
        }
        if(listAward.getCount()==totalAwards)
        {
            setProjectsData();
        }
    }
    public void setAwardsData()
    {
        cvAwards=view.findViewById(R.id.cvAwardsLabel);
        listviewAward.setAdapter(null);
        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName)
                .document("Profile").collection("Awards").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    awardList = new ArrayList<>();
                    totalAwards=queryDocumentSnapshots.size();
                    cvAwards.setVisibility(View.VISIBLE);
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        UserAwards awards=doc.toObject(UserAwards.class);
                        populateAwardsListview(awards.getTitle(), awards.getYear(),awards.getDescription());
                    }
                }
                else
                {
                    Log.i(TAG, "onEvent: Empty List");
                }
            }});
    }

    public void loadImage()
    {
        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName)
                .document("Profile").collection("Image").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                {
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
                            cvImage=view.findViewById(R.id.cvImage);
                            DocumentReference imgRef = FirebaseFirestore.getInstance().collection("Users")
                                    .document(status).collection(uName).document("Profile")
                                    .collection("Image").document(idd);
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
                                            Picasso.get().load(url.toString()).into(cvImage);
                                            setBasicInfoSoftwaresAndSkills();
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

   public void layoutToImage(View view) throws IOException
   {
       // Bind and convert the layout into bitmap
       linearLayout = view.findViewById(R.id.layout);
       linearLayout.setDrawingCacheEnabled(true);
       linearLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
               View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
       linearLayout.layout(0, 0, linearLayout.getMeasuredWidth(), linearLayout.getMeasuredHeight());
       linearLayout.buildDrawingCache();
       Bitmap bm = Bitmap.createBitmap(linearLayout.getWidth(), linearLayout.getHeight(), Bitmap.Config.ARGB_8888);
       Canvas cc = new Canvas(bm);
       linearLayout.draw(cc);
       //Create a PDF Document and set its width and height like A4 size paper
       PdfDocument pdfDocument = new PdfDocument();
       double singlee = bm.getWidth() / 8.3;
       double widthh = singlee * 8.3;
       double height = singlee * 11.5;
       int heightCounter = (int) height - 58;
       int heightCheck = 0;
       boolean pageCount = true;
       int pageCounter = 1;
       //Start a loop, the pageCounter decides how many pages will be there in the counter
       while (pageCount == true)
       {
           if (bm.getHeight() < heightCounter)
           {
               pageCount = false;
           }
           Paint paint = new Paint();
           paint.setStyle(Paint.Style.STROKE);
           paint.setColor(Color.WHITE);
           paint.setStrokeWidth(60);
           PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder((int) widthh, (int) height, 1).create();
           PdfDocument.Page page = pdfDocument.startPage(pi);
           Canvas canvas = page.getCanvas();
           bm = Bitmap.createScaledBitmap(bm, bm.getWidth(), bm.getHeight(), true);
           canvas.drawBitmap(bm, 0, (int) -heightCheck + 38, null);
           canvas.drawLine(0, (int) height, bm.getWidth(), (int) height, paint);
           canvas.drawLine(0, 0, bm.getWidth(), 0, paint);
           Paint pageText = new Paint();
           pageText.setColor(Color.BLACK);
           pageText.setTextSize(10);
           canvas.drawText("Page: "+pageCounter, bm.getWidth() - 75,25, pageText);
           pageCounter++;
           heightCheck = (int) heightCounter;
           heightCounter = heightCounter + (int) height - 58;
           pdfDocument.finishPage(page);
       }
       //Save the generated PDF into gallary
       File root = new File(Environment.getExternalStorageDirectory(), "PDF Folder");
       if (!root.exists())
       {
           root.mkdir();
       }
       File file = new File(root, "myCV.pdf");
       try
       {
           FileOutputStream fileOutputStream = new FileOutputStream(file);
           pdfDocument.writeTo(fileOutputStream);
           showSuccess();

       } catch (IOException e)
       {
           e.printStackTrace();
           showFaliure();
       }
       pdfDocument.close();
   }

    public void showProgress()
    {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.dismiss();
    }

    public void showSuccess()
    {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // Any UI task
                new CDialog(getContext()).createAlert("DOWNLOADED",
                        CDConstants.SUCCESS,   // Type of dialog
                        CDConstants.LARGE)    //  size of dialog
                        .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)     //  Animation for enter/exit
                        .setDuration(2000)   // in milliseconds
                        .setBackgroundColor(getResources().getColor(R.color.primary))
                        .setTextSize(CDConstants.LARGE_TEXT_SIZE)  // CDConstants.LARGE_TEXT_SIZE, CDConstants.NORMAL_TEXT_SIZE
                        .show();
            }
        };
        handler.sendEmptyMessage(1);

    }

    public void showFaliure()
    {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // Any UI task
                new CDialog(getContext()).createAlert("Something went wrong",
                        CDConstants.ERROR,   // Type of dialog
                        CDConstants.LARGE)    //  size of dialog
                        .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)     //  Animation for enter/exit
                        .setDuration(2000)   // in milliseconds
                        .setBackgroundColor(getResources().getColor(R.color.red))
                        .setTextSize(CDConstants.NORMAL_TEXT_SIZE)  // CDConstants.LARGE_TEXT_SIZE, CDConstants.NORMAL_TEXT_SIZE
                        .show();
            }
        };
        handler.sendEmptyMessage(1);

    }

    public void loadCV()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while ((listviewEducation.getChildAt(totalEcucationObjs-1) == null ||
                        listviewEducation.getChildAt(totalEcucationObjs-1) .equals("")) &&
                        (listviewExperience.getChildAt(totalExperienceObjs-1) == null ||
                        listviewExperience.getChildAt(totalExperienceObjs-1) .equals("")) &&
                        (listviewAward.getChildAt(totalAwards-1) == null ||
                                listviewAward.getChildAt(totalAwards-1) .equals(""))&&
                        (listviewProjects.getChildAt(totalProjects-1) == null ||
                                listviewProjects.getChildAt(totalProjects-1) .equals(""))&&
                        (gridviewSkills.getChildAt(totalSkills-1) == null ||
                                gridviewSkills.getChildAt(totalSkills-1) .equals(""))&&
                        (girdviewSoftwares.getChildAt(totalSoftwares-1) == null ||
                                girdviewSoftwares.getChildAt(totalSoftwares-1) .equals(""))) { // your conditions
                }

                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        // Any UI task
                        final Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    layoutToImage(view);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 2000);
                    }
                };
                handler.sendEmptyMessage(1);



            }
        };
        new Thread(runnable).start();
    }

}
