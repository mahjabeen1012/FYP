package com.childcareapp.pivak.fyplogin.Dialogs;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.childcareapp.pivak.fyplogin.ViewSkillsUser;
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
public class AddSkillDialog extends AppCompatDialogFragment {

    TextView titleLable;
    String uName,skills;
    FirebaseFirestore mStore;
    AutoCompleteTextView sSkills;
    Button addSkill,cancelSkill;
    Bundle bundle4 = new Bundle();
    boolean btnPressed=false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_skill_dialog, null);
        uName=getArguments().getString("user");
        titleLable=view.findViewById(R.id.titleLabelSkills);
        sSkills=view.findViewById(R.id.addSkillSpinner);
        addSkill=view.findViewById(R.id.addSkillDialog);
        cancelSkill=view.findViewById(R.id.CancelSkillDialog);
        skills="";
        mStore=FirebaseFirestore.getInstance();
        textChangeLitener();
        fillAutoCompleteTexviewWithSkills();
        userPreviousSkills();       // get previous skills

        cancelSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        addSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPressed=true;
                if(alreadyExist())
                {
                    showAlertDialog();
                }
                else if(!sSkills.getText().toString().equals(""))
                {
                    storeDataToFireStore();
                    loadPreviousFragment();
                    dismiss();
                }
                else
                {
                    titleLable.setTextColor(getResources().getColor(R.color.red));
                    Toast.makeText(getActivity(), "Select a skill", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public  void textChangeLitener()
    {
        sSkills.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && btnPressed==true)
                {
                    titleLable.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    titleLable.setTextColor(getResources().getColor(R.color.darkGrey));
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
        bundle4.putString("skills",skills + "," + sSkills.getText().toString());
        bundle4.putString("user", uName);
        bundle4.putString("userStatus", uStatus);
        bundle4.putString("profileType", "myProfile");
        ViewSkillsUser viewSkillsUser = new ViewSkillsUser();
        viewSkillsUser.setArguments(bundle4);
        FragmentTransaction fragmentTransaction3 = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction3.addToBackStack(null);
        if(uStatus.equals("Student")) {
            fragmentTransaction3.replace(R.id.content_student_home, viewSkillsUser);
        }
        else if(uStatus.equals("Alumni"))
        {
            fragmentTransaction3.replace(R.id.content_alumni_home, viewSkillsUser);
        }
        else if(uStatus.equals("Faculty"))
        {
            fragmentTransaction3.replace(R.id.content_faculty_home, viewSkillsUser);
        }
        fragmentTransaction3.commit();
    }
    public void fillAutoCompleteTexviewWithSkills()
    {
        final ArrayAdapter<String> classes = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.Skills));
        classes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSkills.setAdapter(classes);
        sSkills.setThreshold(1);
    }
    public void userPreviousSkills()
    {
        DocumentReference docRef = mStore.collection("Users").document("Student").collection(uName)
                .document("Profile");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        UserModel userData=document.toObject(UserModel.class);
                        skills=userData.getSkills().toString();
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
        String[] previousSkills=skills.split(",");
        boolean alreadyExist=false;
        for(int a=1;a<previousSkills.length; a++)
        {
            if(sSkills.getText().toString().equals(previousSkills[a]))
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
    public void storeDataToFireStore()
    {
        Map<String, Object> skill = new HashMap<>();
        skill.put("skills", skills + "," + sSkills.getText().toString());
        mStore=FirebaseFirestore.getInstance();
        mStore.collection("Users").document("Student").collection(uName).document("Profile")
                .update(skill).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }
}
