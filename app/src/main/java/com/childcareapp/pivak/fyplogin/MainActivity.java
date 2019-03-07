package com.childcareapp.pivak.fyplogin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.childcareapp.pivak.fyplogin.Models.CompanyModel;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.UserEducation;
import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class MainActivity extends AppCompatActivity {

    Button signIn;
    EditText username,pass;
    CheckBox rememberMe;;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        getSupportActionBar().setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.primaryDark));
        }


        signIn=findViewById(R.id.signIn);
        username=findViewById(R.id.username);
        pass=findViewById(R.id.password);
        rememberMe=findViewById(R.id.rememberMe);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        mStore=FirebaseFirestore.getInstance();

        ///// Register Company
//        final CompanyModel companyProfile = new CompanyModel("Netsole","Pakistan","Lahore","","","netsole@live.com","Company","netsole@facebook.com","","","www.netsole.com");
//
//        mAuth.createUserWithEmailAndPassword("netsole@live.com", "123456")
//                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(MainActivity.this, "Succeeded", Toast.LENGTH_SHORT).show();
//                            mStore.collection("Users").document("Company").collection("netsole@live.com").document("Profile").set(companyProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Toast.makeText(MainActivity.this, "Company Added Successfully", Toast.LENGTH_SHORT).show();
//
//                                }
//                            });
//
//                        } else {
//                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//                        }
//                        // ...
//                    }
//                });

        ////// Insert Image

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profileimage);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
//        final StorageReference sReff = mStorageRef.child("Images/netsole.jpg");
//        UploadTask uploadTask = sReff.putBytes(data);
//        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if(!task.isSuccessful())
//                {
//                    throw task.getException();
//                }
//                return sReff.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    Uri downloadUri = task.getResult();
//                    Images img = new Images("f158016",downloadUri.toString());
//                    mStore.collection("Users").document("Student").collection("f158016@nu.edu.pk").document("Profile").collection("Image").add(img).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                        @Override
//                                        public void onSuccess(DocumentReference aVoid) {
//                                             Toast.makeText(MainActivity.this, "Photo Added Successfully", Toast.LENGTH_SHORT).show();
//
//                                        }
//                                    });
//                } else {
//                    Toast.makeText(MainActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//
//


        /// generate random userID
        //int random = ThreadLocalRandom.current().nextInt(8011, 8060);
        //txt1.setText("15f"+random+"@nu.edu.pk");

        // generate random city
//        String[] str={"Faisalabad", "Lahore", "Karachi", "Multan", "Peshawar", "Islamabad", "Quetta"};
//        int random = ThreadLocalRandom.current().nextInt(0, 5);
//        txt1.setText(str[random]);

        //// random campus
        // String[] str={"Islamabad", "Lahore", "Karachi", "Peshawar", "Faisalabad"};
        //int random = ThreadLocalRandom.current().nextInt(0, 5);
        //txt1.setText(str[random]);

        //// random Discipline
        // String[] str={"Computer Science", "Electric Engineering", "Management Sciences", "Accounting and Finance", "Civil Engineering"};
        //int random = ThreadLocalRandom.current().nextInt(0, 5);
        //txt1.setText(str[random]);

        //// filter skills
//        String ss="";
//        String str="Android : 62, Project Planning : 52, C++ : 43, Microsoft Excel : 65, HTML : 34, Kotlin : 23, Graphic Designing : 34";
//        String[] userSkills=str.split(",");
//        String str1="Android, Graphic Designing, C++, HTML";
//        String[] linkedInSkills=str1.split(",");
//        for(int a=0; a< userSkills.length; a++)
//        {
//            for(int b=0; b<linkedInSkills.length; b++)
//            {
//                String[] currentSkill=userSkills[a].split(":");
//                if(currentSkill[0].equalsIgnoreCase(linkedInSkills[b]) || currentSkill[0].contains(linkedInSkills[b]) || linkedInSkills[b]
//                        .contains(currentSkill[0]))
//                {
//                    ss=ss+currentSkill[0];
//                }
//            }
//        }
//        txt1.setText(ss);







//        String[] cities={"Faisalabad", "Lahore", "Karachi", "Multan", "Peshawar", "Islamabad", "Quetta"};
//        String[] campus={"Islamabad", "Lahore", "Karachi", "Peshawar", "Faisalabad"};
//        try
//        {
//            AssetManager assetManager=getAssets();
//            InputStream is=assetManager.open("cs.xls");
//            Workbook wb= Workbook.getWorkbook(is);
//            Sheet s= wb.getSheet(0);
//            int row=s.getRows();
//            int col=s.getColumns();
//            String xx="";
//            //for(int c=0; c<10; c++)
//            {
//                //Cell cell=s.getCell(1,c);
//                //xx=xx + cell.getContents()+"\n";
//
//                int random1= ThreadLocalRandom.current().nextInt(0, 5);
//                final UserModel studentProfile = new UserModel(s.getCell(4,1).getContents(), s.getCell(5,1).getContents(),
//                        "Pakistan",cities[random1],
//                        "","",s.getCell(4,1).getContents()+s.getCell(5,1).getContents()+"@gmail.com","Student",
//                        "","","","","BS","2015", "Computer Science",campus[random1],
//                        "");
//                final UserEducation userEducation  = new UserEducation("Bechelors",
//                        "National University of Computer and Emerging Sciences", "Pakistan",campus[random1],"01-08-2015",
//                        "01-06-2019");
//
//                // authenticate ID
//                /// generate random userID
//                final boolean[] falseID = {true};
//                final int[] random = {8018};
//                //txt1.setText("15f"+random+"@nu.edu.pk");
//                    mAuth.createUserWithEmailAndPassword("f15"+ random[0] +"@nu.edu.pk", "123456")
//                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(MainActivity.this, "Succeded", Toast.LENGTH_SHORT).show();
//                                        mStore.collection("Users").document("Student")
//                                                .collection("f15"+ random[0] +"@nu.edu.pk")
//                                                .document("Profile").set(studentProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                mStore.collection("Users").document("Student")
//                                                        .collection("f15"+ random[0] +"@nu.edu.pk").document("Profile")
//                                                        .collection("Education").add(userEducation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                    @Override
//                                                    public void onSuccess(DocumentReference aVoid) {
//                                                        updateUsersList("f15"+ random[0] +"@nu.edu.pk");
//                                                    }
//                                                });
////                                            mStore.collection("Users").document("Student").collection("f158280@nu.edu.pk").document("Profile").collection("Experience").add(studentExperience).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
////                                                @Override
////                                                public void onSuccess(DocumentReference aVoid) {
////                                                    Toast.makeText(MainActivity.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
////
////                                                }
////                                            });
//                                                Toast.makeText(MainActivity.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
//
//                                            }
//                                        });
//
//                                    } else {
//                                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//                                    }
//                                    // ...
//                                }
//                            });
//                //// end authentication
//
//            }
//        }
//        catch(Exception e)
//        {
//
//        }

























         //Insert data to firestore
//        final UserModel studentProfile = new UserModel("Usman", "Arif", "Pakistan","Faisalabad",
//                "","","usman@nu.edu.pk","Faculty","","","","","","",""
//                ,"");
//        final UserEducation userEducation  = new UserEducation("Bechelors",
//                "National University of Computer and Emerging Sciences", "Pakistan","Faisalabad","01-08-2015",
//                "01-06-2019");
//
//                        mAuth.createUserWithEmailAndPassword("usman@nu.edu.pk", "123456")
//                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(MainActivity.this, "Succeded", Toast.LENGTH_SHORT).show();
//                                    mStore.collection("Users").document("Student").collection("usman@nu.edu.pk")
//                                            .document("Profile").set(studentProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            mStore.collection("Users").document("Student")
//                                                    .collection("usman@nu.edu.pk").document("Profile")
//                                                    .collection("Education").add(userEducation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                @Override
//                                                public void onSuccess(DocumentReference aVoid) {
//
//                                                }
//                                            });
////                                            mStore.collection("Users").document("Student").collection("f158280@nu.edu.pk").document("Profile").collection("Experience").add(studentExperience).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
////                                                @Override
////                                                public void onSuccess(DocumentReference aVoid) {
////                                                    Toast.makeText(MainActivity.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
////
////                                                }
////                                            });
//                                            Toast.makeText(MainActivity.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
//
//                                        }
//                                    });
//
//                                } else {
//                                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//                                }
//                                // ...
//                            }
//                        });
// updateUsersList("usman@nu.edu.pk");



        ////////////////// Remember Me

        SharedPreferences sharedPreferences=getSharedPreferences("myprefrences",MODE_PRIVATE);
        username.setText(sharedPreferences.getString("username",""));
        pass.setText(sharedPreferences.getString("pass",""));
        if(sharedPreferences.getString("rememberMe","").equals("true"))
        {
            rememberMe.setChecked(true);
        }
        ////////////////////////// SignIn
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!username.getText().toString().equals("") && !pass.getText().toString().equals("")) {
                    disableSignInButton();
                    showProgressbar();
                    mAuth.signInWithEmailAndPassword(username.getText().toString(), pass.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        if(rememberMe.isChecked())
                                        {
                                            SharedPreferences sharedPreferences=getSharedPreferences("myprefrences",MODE_PRIVATE);
                                            SharedPreferences.Editor editor=sharedPreferences.edit();
                                            editor.putString("username",username.getText().toString());
                                            editor.putString("pass", pass.getText().toString());
                                            editor.putString("rememberMe", "true");
                                            editor.commit();
                                        }
                                        else
                                        {
                                            SharedPreferences sharedPreferences=getSharedPreferences("myprefrences",MODE_PRIVATE);
                                            SharedPreferences.Editor editor=sharedPreferences.edit();
                                            editor.putString("username","");
                                            editor.putString("pass", "");
                                            editor.putString("rememberMe", "false");
                                            editor.commit();
                                        }
                                        /////////// LOGIN
                                        DocumentReference docRef = mStore.collection("Users").document("Student").collection(username.getText().toString()).document("Profile");
                                        Task<DocumentSnapshot> documentSnapshotTask = docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        UserModel userData = document.toObject(UserModel.class);
                                                        String status= userData.getStatus().toString();
                                                        Intent intent = null;
                                                        if(status.equals("Student"))
                                                        {
                                                            intent = new Intent(MainActivity.this, StudentHome.class);
                                                            intent.putExtra("user", username.getText().toString());
                                                            startActivity(intent);
                                                        }
                                                        else if(status.equals("Alumni"))
                                                        {
                                                            intent = new Intent(MainActivity.this, AlumniHome.class);
                                                            intent.putExtra("user", username.getText().toString());
                                                            startActivity(intent);
                                                        }
                                                        else if(status.equals("Faculty"))
                                                        {
                                                            intent = new Intent(MainActivity.this, FacultyHome.class);
                                                            intent.putExtra("user", username.getText().toString());
                                                            startActivity(intent);
                                                        }
                                                        enableSignInButton();
                                                        hideProgressbar();
                                                    }
                                                }
                                            }
                                        });

                                        DocumentReference docReff = mStore.collection("Users").document("Company").collection(username.getText().toString()).document("Profile");
                                        Task<DocumentSnapshot> documentSnapshotTaskk = docReff.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        enableSignInButton();
                                                        hideProgressbar();
                                                        Intent intent = new Intent(MainActivity.this, CompanyHome.class);
                                                        intent.putExtra("user", username.getText().toString());
                                                        startActivity(intent);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        String err=task.getException().getLocalizedMessage();
                                        if(isNetworkConnected()!=false && internetIsConnected()!=false) {
                                            if (err.equals("There is no user record corresponding to this identifier. The user may have been deleted.") || !username.getText().toString().contains("@")) {
                                                showAlertDialog("Incorrect Username",
                                                        "User name you entered doesn't exist. Please try again. \n");
                                                enableSignInButton();
                                                hideProgressbar();
                                            } else {
                                                //Toast.makeText(MainActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                                                showAlertDialog("Incorrect Password",
                                                        "The password you entered is incorrect. Please try again. \n");
                                                //Snackbar.make(findViewById(R.id.main_layout), "Invalid Password", Snackbar.LENGTH_SHORT).show();
                                                enableSignInButton();
                                                hideProgressbar();
                                            }
                                        }
                                        else
                                        {
                                            showAlertDialog("Internet not connected",
                                                    "Check your internet connection and try again. \n");
                                            //Snackbar.make(findViewById(R.id.main_layout), "Invalid Password", Snackbar.LENGTH_SHORT).show();
                                            enableSignInButton();
                                            hideProgressbar();
                                        }
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Enter required fields ", Toast.LENGTH_SHORT).show();
                    enableSignInButton();
                    hideProgressbar();
                }
            }
        });
    }

    public void updateUsersList(final String userr)
    {
        // getPrevious Users
        FirebaseFirestore.getInstance().collection("Users").document("Student").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        String finalUserList="";
                        String users=document.getData().get("users").toString();
                        if(!users.equals("")) {
                            finalUserList = users + "," + userr;
                        }

                        else
                        {
                            finalUserList=","+ userr;
                        }
                        Map<String, Object> data = new HashMap<>();
                        data.put("users",finalUserList);
                        FirebaseFirestore.getInstance().collection("Users").document("Student")
                                .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                        //////////
                    }
                    else
                    {
                        Map<String, Object> data = new HashMap<>();
                        data.put("users",","+userr);
                        FirebaseFirestore.getInstance().collection("Users").document("Student")
                                .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void showProgressbar()
    {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);
    }
    public void hideProgressbar()
    {
        progressBar.setVisibility(View.GONE);
    }

    public void disableSignInButton()
    {
        signIn.setEnabled(false);
        username.setEnabled(false);
        pass.setEnabled(false);
        rememberMe.setEnabled(false);
        signIn.setText("");
    }
    public void enableSignInButton()
    {
        signIn.setEnabled(true);
        signIn.setText("SIGN IN");
        username.setEnabled(true);
        pass.setEnabled(true);
        rememberMe.setEnabled(true);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplication().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showDialog(String title, String message)
    {
        /// get screen width and hight
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //////////////////////////

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        /////// Bold Title
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            builder.setTitle(Html.fromHtml("<b>"+title+"</b>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            builder.setTitle(Html.fromHtml("<b>"+title+"</b>"));

        }
        ///////////  set button
        builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        /// set dialog size
        dialog.getWindow().setLayout(width-150, (height/3)-90);

        ///////// set button position
        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 20;
        btnPositive.setLayoutParams(layoutParams);
        btnPositive.setBackgroundColor(getResources().getColor(R.color.white));
        btnPositive.setWidth(1000);

        /////////// set button's color
        btnPositive.setTextColor(getResources().getColor(R.color.lightBlue));
        btnPositive.setText(Html.fromHtml("<b>"+"Try Again"+"</b>", Html.FROM_HTML_MODE_LEGACY));
    }

    public void showAlertDialog(String title, String message)
    {
        new AwesomeInfoDialog(this)
                .setTitle(Html.fromHtml("<b>"+title+"</b>", Html.FROM_HTML_MODE_LEGACY))
                .setMessage(message)
                .setColoredCircle(R.color.red)
                .setDialogIconAndColor(R.drawable.ic_dialog_error, R.color.white)
                .setCancelable(true)
                .setPositiveButtonText("Try Again")
                .setPositiveButtonbackgroundColor(R.color.red)
                .setPositiveButtonTextColor(R.color.white)

                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {

                    }
                })
                .show();
    }

}
