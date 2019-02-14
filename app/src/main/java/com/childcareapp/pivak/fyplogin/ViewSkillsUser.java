package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.childcareapp.pivak.fyplogin.Dialogs.AddSkillDialog;
import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewSkill;
import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewSkillsUser extends Fragment implements View.OnClickListener{
    Button addSkill,back;
    String uName,profileType;
    ListView listSkill;
    int totalSkills=0;
    Bundle bundle = new Bundle();
    ProgressBar progressBar;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.view_skills_user, container, false);
        addSkill = view.findViewById(R.id.addSkills);
        back = view.findViewById(R.id.backSkillsUser);
        back.setOnClickListener(this);
        addSkill.setOnClickListener(this);
        uName= getArguments().getString("user");
        profileType=getArguments().getString("profileType");
        listSkill = view.findViewById(R.id.listViewSkillss);
        progressBar=view.findViewById(R.id.skillsProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        if(profileType.equals("search"))
        {
            addSkill.setVisibility(View.INVISIBLE);
        }
        else if(profileType.equals("myProfile"))
        {
            addSkill.setVisibility(View.VISIBLE);
        }

        if(!getArguments().getString("skills").equals("")) {
            setListSkills(getArguments().getString("skills"));
            loadListView();
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void openAddSkillDialog()
    {
        Bundle bundle2 = new Bundle();
        bundle2.putString("user", uName);
        bundle2.putString("status", "add");
        bundle2.putString("userStatus", getArguments().getString("userStatus"));
        AddSkillDialog addSkillDialog = new AddSkillDialog();
        addSkillDialog.setArguments(bundle2);
        addSkillDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Add Skill");
    }
    public void setListSkills(String skills)
    {
        List<UserModel> skilsListt = new ArrayList<>();
        UserModel dataaaa;
        String[] strr = skills.split(",");
        totalSkills=0;
        for (int i = 0; i < strr.length; i++) {
            if (!strr[i].equals("")) {
                dataaaa = new UserModel(strr[i],"");
                skilsListt.add(dataaaa);
                totalSkills++;
            }
        }
        ListViewSkill listttt = new ListViewSkill(getActivity(), R.layout.listview_skills, skilsListt);
        listSkill.setAdapter(listttt);

        ListAdapter listAdapterrrr = listSkill.getAdapter();
        if (listAdapterrrr == null) {
            return;
        } else {
            int totalHeight = 0;
            for (int i = 0; i < listAdapterrrr.getCount(); i++) {
                View listItem = listAdapterrrr.getView(i, null, listSkill);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listSkill.getLayoutParams();
            params.height = totalHeight + (listSkill.getDividerHeight() * (listAdapterrrr.getCount() - 1));
            listSkill.setLayoutParams(params);
            listSkill.requestLayout();
        }
    }
    public void removeSkill()
    {
        final List<String> skillsObjs=new ArrayList<>();
        final String[] skills={""};
        skillsObjs.clear();
        if(listSkill.getCount()>0) {
            for (int i = 0; i <listSkill.getCount(); i++)
            {
                String s="";
                View view1 = listSkill.getChildAt(i);
                TextView skillTitle= (TextView) view1.findViewById(R.id.skillTitle);
                skillsObjs.add(skillTitle.getText().toString());
            }
            for (int i = 0; i <listSkill.getCount(); i++) {
                View view1 = listSkill.getChildAt(i);
                final Button deleteSkill= (Button) view1.findViewById(R.id.removeSkill);
                final TextView skillTitle= (TextView) view1.findViewById(R.id.skillTitle);

                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        // Any UI task
                        if(profileType.equals("search"))
                        {
                            deleteSkill.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            deleteSkill.setVisibility(View.VISIBLE);
                        }
                    }
                };
                handler.sendEmptyMessage(1);


                deleteSkill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AwesomeInfoDialog(getContext())
                                .setTitle(Html.fromHtml("<b>"+"Delete Skill"+"</b>", Html.FROM_HTML_MODE_LEGACY))
                                .setMessage("Are you sure you want to delete this item?")
                                .setColoredCircle(R.color.primaryDark)
                                .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                                .setCancelable(true)
                                .setPositiveButtonText("DELETE")
                                .setPositiveButtonbackgroundColor(R.color.primaryDark)
                                .setPositiveButtonTextColor(R.color.white)
                                .setNegativeButtonText("CANCEL")
                                .setNegativeButtonbackgroundColor(R.color.primaryDark)
                                .setNegativeButtonTextColor(R.color.white)
                                .setPositiveButtonClick(new Closure() {
                                    @Override
                                    public void exec() {
                                        skills[0]="";
                                        for(int a=0;a<skillsObjs.size();a++)
                                        {
                                            if(!skillsObjs.get(a).equals(skillTitle.getText().toString()))
                                            {
                                                skills[0]=skills[0]+", "+skillsObjs.get(a);
                                            }
                                        }
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("skills", skills[0]);
                                        FirebaseFirestore.getInstance().collection("Users").document("Student")
                                                .collection(uName).document("Profile").update(data)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                setListSkills(skills[0]);
                                                loadListView();
                                            }
                                        });
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
                });
            }
        }
    }

    public void loadListView()
    {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                while (listSkill.getChildAt(totalSkills-1) == null ||
                        listSkill.getChildAt(totalSkills-1) .equals("") ) { // your conditions
                }
                progressBar.setVisibility(View.INVISIBLE);
                removeSkill(); // your task to execute
            }
        };
        new Thread(runnable).start();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.addSkills)
        {
            openAddSkillDialog();
        }
        else if(v.getId()==R.id.backSkillsUser)
        {
            FragmentManager fm = getActivity().getFragmentManager();
            fm.popBackStack ("profileUser", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
