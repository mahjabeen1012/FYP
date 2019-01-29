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

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.childcareapp.pivak.fyplogin.Dialogs.AddSoftwareDialog;
import com.childcareapp.pivak.fyplogin.ListviewAdapters.ListViewSoftware;
import com.childcareapp.pivak.fyplogin.Models.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewSoftwareUser extends Fragment implements View.OnClickListener{
    Button addSoftware, back;
    String uName,profileType;
    ListView listSoftware;
    int totalSoftware=0;
    ProgressBar progressBar;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.view_software_user, container, false);
        addSoftware = view.findViewById(R.id.addSoftware);
        back = view.findViewById(R.id.backSoftwareUser);
        back.setOnClickListener(this);
        addSoftware.setOnClickListener(this);
        uName= getArguments().getString("user");
        profileType=getArguments().getString("profileType");
        progressBar=view.findViewById(R.id.softwaresProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        if(profileType.equals("search"))
        {
            addSoftware.setVisibility(View.INVISIBLE);
        }
        else if(profileType.equals("myProfile"))
        {
            addSoftware.setVisibility(View.VISIBLE);
        }
        if(!getArguments().getString("softwares").equals("")) {
            setListSoftwares(getArguments().getString("softwares"));
            loadListView();
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void loadListView()
    {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                while (listSoftware.getChildAt(totalSoftware-1) == null ||
                        listSoftware.getChildAt(totalSoftware-1) .equals("") ) { // your conditions
                }
                progressBar.setVisibility(View.INVISIBLE);
                removeSoftware(); // your task to execute
            }
        };
        new Thread(runnable).start();
    }
    public void openAddSoftwareDialog()
    {
        Bundle bundle2 = new Bundle();
        bundle2.putString("user", uName);
        bundle2.putString("status", "add");
        bundle2.putString("userStatus", getArguments().getString("userStatus"));
        AddSoftwareDialog addSoftwareDialog = new AddSoftwareDialog();
        addSoftwareDialog.setArguments(bundle2);
        addSoftwareDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(),"Add Software");
    }
    public void setListSoftwares(String softwares)
    {
        listSoftware = view.findViewById(R.id.listViewSoftwaree);
        List<UserModel> studentsListtttt = new ArrayList<>();
        UserModel dataaaaa;
        String[] str1 = softwares.split(",");
        totalSoftware=0;
        for (int i = 0; i < str1.length; i++) {
            if (!str1[i].equals("")) {
                dataaaaa = new UserModel("", str1[i]);
                studentsListtttt.add(dataaaaa);
                totalSoftware++;
            }
        }
        ListViewSoftware listtttt = new ListViewSoftware(getActivity(), R.layout.listview_software, studentsListtttt);
        listSoftware.setAdapter(listtttt);

        ListAdapter listAdapterrrrr = listSoftware.getAdapter();
        if (listAdapterrrrr == null) {
            return;
        } else {
            int totalHeight = 0;
            for (int i = 0; i < listAdapterrrrr.getCount(); i++) {
                View listItem = listAdapterrrrr.getView(i, null, listSoftware);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listSoftware.getLayoutParams();
            params.height = totalHeight + (listSoftware.getDividerHeight() * (listAdapterrrrr.getCount() - 1));
            listSoftware.setLayoutParams(params);
            listSoftware.requestLayout();
        }
    }

    public void removeSoftware()
    {
        final List<String> softwareObjs=new ArrayList<>();
        final String[] softwares={""};
        softwareObjs.clear();
        if(listSoftware.getCount()>0) {
            for (int i = 0; i <listSoftware.getCount(); i++)
            {
                String s="";
                View view1 = listSoftware.getChildAt(i);
                TextView skillTitle= (TextView) view1.findViewById(R.id.softwareTitle);
                softwareObjs.add(skillTitle.getText().toString());
            }
            for (int i = 0; i <listSoftware.getCount(); i++) {
                final View view1 = listSoftware.getChildAt(i);
                final Button removeSoftware= (Button) view1.findViewById(R.id.removeSoftware);

                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        // Any UI task
                        if(profileType.equals("search"))
                        {
                            removeSoftware.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            removeSoftware.setVisibility(View.VISIBLE);
                        }
                    }
                };
                handler.sendEmptyMessage(1);

                final TextView skillTitle= (TextView) view1.findViewById(R.id.softwareTitle);
                removeSoftware.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        new AwesomeInfoDialog(getContext())
                                .setTitle(Html.fromHtml("<b>"+"DELETE SOFTWARE"+"</b>", Html.FROM_HTML_MODE_LEGACY))
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


                                        softwares[0]="";
                                        for(int a=0;a<softwareObjs.size();a++)
                                        {
                                            if(!softwareObjs.get(a).equals(skillTitle.getText().toString()))
                                            {
                                                softwares[0]=softwares[0]+","+softwareObjs.get(a);
                                            }
                                        }
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("softwares",softwares[0]);
                                        FirebaseFirestore.getInstance().collection("Users").document("Student").collection(uName)
                                                .document("Profile").update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                setListSoftwares(softwares[0]);
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
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.addSoftware)
        {
            openAddSoftwareDialog();
        }
        else if(v.getId()==R.id.backSoftwareUser)
        {
            FragmentManager fm = getActivity().getFragmentManager();
            fm.popBackStack ("profileUser", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

}
