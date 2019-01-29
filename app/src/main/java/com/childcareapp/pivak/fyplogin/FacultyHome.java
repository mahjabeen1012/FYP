package com.childcareapp.pivak.fyplogin;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.childcareapp.pivak.fyplogin.Dialogs.ChangePasswordDialog;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class FacultyHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String uName="";
    ImageView img;
    TextView name,status;
    Bundle messagesBundle = new Bundle();
    Bundle searchBundle = new Bundle();

    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            uName=bundle.getString("user");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        img=hView.findViewById(R.id.navFacultyImageview);
        status=hView.findViewById(R.id.navFacultyStatus);
        name=hView.findViewById(R.id.navFacultyName);
        loadPhoto();            //LOAD PROFILE PHOTO
        setNameAndStatus();     // SET NAME AND STATUS


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        getSupportActionBar().setTitle("Home");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.primaryDark));
        }

        Bundle bundle2 = new Bundle();
        bundle2.putString("user", uName);
        bundle2.putString("status", "Faculty");
        bundle2.putString("profileType","myProfile");
        MyProfileUser myProfileStudent = new MyProfileUser();
        myProfileStudent.setArguments(bundle2);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_faculty_home, myProfileStudent);
        fragmentTransaction.commit();
        Button notificationButton = findViewById(R.id.notification_button_faculty);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if((count%2) == 0)
                {
                    FragmentManager fm = getFragmentManager();
                    fm.popBackStack ("nFaculty", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                else
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("user", uName);
                    bundle.putString("userType", "Faculty");
                    Notifications notifications = new Notifications();
                    notifications.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack("nFaculty");
                    fragmentTransaction.replace(R.id.content_faculty_home, notifications);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.faculty_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.changePassword) {
            Bundle bundle = new Bundle();
            bundle.putString("user", uName);
            ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog();
            changePasswordDialog.setArguments(bundle);
            changePasswordDialog.show(getSupportFragmentManager(), "Change Password");
            return true;
        }
        else if (id == R.id.logOut)
        {
            showAlertDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            Bundle bundle2 = new Bundle();
            bundle2.putString("user", uName);
            bundle2.putString("status", "Faculty");
            bundle2.putString("profileType","myProfile");
            MyProfileUser myProfileStudent = new MyProfileUser();
            myProfileStudent.setArguments(bundle2);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_faculty_home, myProfileStudent);
            fragmentTransaction.commit();

        } else if (id == R.id.membersFaculty) {
            callFragment("search");

        } else if (id == R.id.liveSessions) {

        } else if (id == R.id.jobRecommendation) {

        } else if (id == R.id.cvGenerator) {
            Bundle bundle2 = new Bundle();
            bundle2.putString("user", uName);
            bundle2.putString("status","Faculty");
            CVFragment cvFragment = new CVFragment();
            cvFragment.setArguments(bundle2);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_faculty_home, cvFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.helpPortal) {

        } else if (id == R.id.homee) {
            img.buildDrawingCache();
            Bitmap bmap = img.getDrawingCache();
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bmap.compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] byteArray=stream.toByteArray();

            Bundle bundle2 = new Bundle();
            bundle2.putString("user", uName);
            bundle2.putString("status", "Faculty");
            bundle2.putString("userType", "Student");
            bundle2.putByteArray("image", byteArray);
            HomeActivity homeActivity = new HomeActivity();
            homeActivity.setArguments(bundle2);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_faculty_home, homeActivity);
            fragmentTransaction.commit();

        }
        else if (id == R.id.messages) {
            callFragment("message");
        }
        else if (id == R.id.liveSessionss)
        {

        }else if (id == R.id.jobOpportunity)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadPhoto()
    {

        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName)
                .document("Profile").collection("Image")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
    public void setNameAndStatus()
    {
        final FirebaseFirestore mStore=FirebaseFirestore.getInstance();
        DocumentReference docRef = mStore.collection("Users").document("Student").collection(uName).document("Profile");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        ///////////////
                        UserModel userData=document.toObject(UserModel.class);
                        name.setText(userData.getfName().toString()+" "+ userData.getlName().toString());
                        status.setText(userData.getStatus());
                        messagesBundle.putString("senderName", userData.getfName()+" "+userData.getlName());
                        searchBundle.putString("senderName", userData.getfName()+" "+userData.getlName());
                    }
                }
            }
        });
    }

    public void callFragment(final String type)
    {
        final FirebaseFirestore mStore=FirebaseFirestore.getInstance();
        DocumentReference docRef = mStore.collection("Users").document("Student").collection(uName).document("Profile");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        ///////////////
                        final UserModel userData=document.toObject(UserModel.class);
                        final String name=userData.getfName()+" "+userData.getlName();
                        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName)
                                .document("Profile").collection("Image")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                                if(type.equals("message"))
                                                {
                                                    messagesBundle.putString("status", "Faculty");
                                                    messagesBundle.putString("user", uName);
                                                    messagesBundle.putString("userType", "Student");
                                                    messagesBundle.putString("senderName", name);
                                                    messagesBundle.putString("senderPhoto", url.toString());
                                                    AllContacts chatFragment = new AllContacts();
                                                    chatFragment.setArguments(messagesBundle);
                                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                                    fragmentTransaction.replace(R.id.content_faculty_home, chatFragment);
                                                    fragmentTransaction.commit();


                                                }
                                                else if(type.equals("search"))
                                                {
                                                    searchBundle.putString("user", uName);
                                                    searchBundle.putString("status", "Faculty");
                                                    searchBundle.putString("senderName", name);
                                                    searchBundle.putString("senderPhoto", url.toString());
                                                    MemberSearchTabs membersUser = new MemberSearchTabs();
                                                    membersUser.setArguments(searchBundle);
                                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                                    fragmentTransaction.replace(R.id.content_faculty_home, membersUser);
                                                    fragmentTransaction.commit();

                                                }
                                                return;
                                            }
                                        }
                                        else
                                        {
                                            Log.i(TAG, "onEvent: Empty List");
                                        }
                                    }});


                    }
                }
            }
        });
    }

    public void showAlertDialog()
    {
        new AwesomeInfoDialog(this)
                .setTitle(Html.fromHtml("<b>"+"Logout"+"</b>", Html.FROM_HTML_MODE_LEGACY))
                .setMessage("Logout now? Are you sure?")
                .setColoredCircle(R.color.primaryDark)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                .setCancelable(true)
                .setPositiveButtonText("LOGOUT")
                .setPositiveButtonbackgroundColor(R.color.primaryDark)
                .setPositiveButtonTextColor(R.color.white)
                .setNegativeButtonText("CANCEL")
                .setNegativeButtonbackgroundColor(R.color.primaryDark)
                .setNegativeButtonTextColor(R.color.white)
                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {

                        startActivity(new Intent(FacultyHome.this, MainActivity.class));
                    }
                })
                .setNegativeButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        //cancel
                    }
                })
                .show();
    }
}
