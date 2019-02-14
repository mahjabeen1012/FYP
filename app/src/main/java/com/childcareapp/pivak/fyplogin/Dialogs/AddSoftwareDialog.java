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
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.childcareapp.pivak.fyplogin.R;
import com.childcareapp.pivak.fyplogin.ViewSoftwareUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IDEAL on 11/19/2018.
 */

public class AddSoftwareDialog extends AppCompatDialogFragment {
    String uName,softwares;
    FirebaseFirestore mStore;
    AutoCompleteTextView sSoftwares;
    TextView softwareLabel;
    Button addSoft,cancelSoft;
    boolean btnPressed=false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_software_dialog, null);

        uName=getArguments().getString("user");
        sSoftwares=view.findViewById(R.id.addSoftwareSpinner);
        softwareLabel=view.findViewById(R.id.addSoftwareLabel);
        addSoft=view.findViewById(R.id.addSoftDialog);
        cancelSoft=view.findViewById(R.id.CancelSoftDialog);
        softwares="";
        textChangeLitener();
        populateSoftwareList();     //fill atutocompletetextview
        userPreviousSoftwares();    // get previous softwares

        cancelSoft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        addSoft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPressed=true;
                if(alreadyExist())
                {
                    showAlertDialog();
                }
                else if(!sSoftwares.getText().toString().equals(""))
                {
                    storeDataToFirestore();
                    loadPreviousFragment();
                    dismiss();
                }
                else
                {
                    softwareLabel.setTextColor(getResources().getColor(R.color.red));
                    Toast.makeText(getActivity(), "Select a Software", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(view);
        //return builder.create();

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
        sSoftwares.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && btnPressed==true)
                {
                    softwareLabel.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    softwareLabel.setTextColor(getResources().getColor(R.color.darkGrey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public  void loadPreviousFragment()
    {
        String uStatus=getArguments().getString("userStatus");
        Bundle bundle = new Bundle();
        bundle.putString("user", uName);
        bundle.putString("softwares",softwares + "," + sSoftwares.getText().toString());
        bundle.putString("userStatus", uStatus);
        bundle.putString("profileType", "myProfile");
        ViewSoftwareUser viewSoftwareUser = new ViewSoftwareUser();
        viewSoftwareUser.setArguments(bundle);
        FragmentTransaction fragmentTransaction3 = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction3.addToBackStack(null);
        if(uStatus.equals("Student")) {
            fragmentTransaction3.replace(R.id.content_student_home, viewSoftwareUser);
        }
        else if(uStatus.equals("Alumni"))
        {
            fragmentTransaction3.replace(R.id.content_alumni_home, viewSoftwareUser);
        }
        else if(uStatus.equals("Faculty"))
        {
            fragmentTransaction3.replace(R.id.content_faculty_home, viewSoftwareUser);
        }
        fragmentTransaction3.commit();
    }

    public void populateSoftwareList()
    {
        final ArrayAdapter<String> classes = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.Softwares));
        classes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSoftwares.setAdapter(classes);
        sSoftwares.setThreshold(1);
    }

    public void userPreviousSoftwares()
    {
        mStore=FirebaseFirestore.getInstance();
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
                        softwares=userData.getSoftwares();

                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Document doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Failed to get data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean alreadyExist()
    {
        String[] previousSoftwares=softwares.split(","); boolean alreadyExist=false;
        for(int a=1;a<previousSoftwares.length; a++)
        {
            String ssfotware=" "+sSoftwares.getText().toString();
            if(ssfotware.equals(previousSoftwares[a]))
            {
                alreadyExist=true;
                return true;
            }
        }
        if(alreadyExist==true)
            return true;
        else
            return false;
    }

    public void showAlertDialog()
    {
        new AwesomeInfoDialog(getContext())
                .setTitle(Html.fromHtml("<b>"+"Already Exist"+"</b>", Html.FROM_HTML_MODE_LEGACY))
                .setMessage("You've already added this item.")
                .setColoredCircle(R.color.primaryDark)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                .setCancelable(true)
                .setPositiveButtonText("OK")
                .setPositiveButtonbackgroundColor(R.color.primaryDark)
                .setPositiveButtonTextColor(R.color.white)
                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        // OK
                    }
                })
                .show();
    }

    public void storeDataToFirestore()
    {
        mStore=FirebaseFirestore.getInstance();
        Map<String, Object> software = new HashMap<>();
        software.put("softwares", softwares + ", " + sSoftwares.getText().toString());
        mStore.collection("Users").document("Student").collection(uName).document("Profile")
                .update(software).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }
}
