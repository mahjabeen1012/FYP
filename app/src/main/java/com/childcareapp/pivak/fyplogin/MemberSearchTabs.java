package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MemberSearchTabs extends Fragment {
    View view;
    private FragmentActivity myContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.member_search_tabs, container, false);

        BottomNavigationView bottomNav = view.findViewById(R.id.nav_drawer_search);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            Bundle bundle=new Bundle();
            MembersUser membersUser = new MembersUser();
            bundle.putString("user", getArguments().getString("user"));
            bundle.putString("senderName", getArguments().getString("senderName"));
            bundle.putString("senderPhoto", getArguments().getString("senderPhoto"));
            bundle.putString("status", getArguments().getString("status"));
            membersUser.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.bottom_nav_search, membersUser);
            fragmentTransaction.commit();
        }
        return view;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_search_member:
                            Bundle bundle=new Bundle();
                            MembersUser membersUser = new MembersUser();
                            bundle.putString("user", getArguments().getString("user"));
                            bundle.putString("senderName", getArguments().getString("senderName"));
                            bundle.putString("senderPhoto", getArguments().getString("senderPhoto"));
                            bundle.putString("status", getArguments().getString("status"));
                            membersUser.setArguments(bundle);
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.bottom_nav_search, membersUser);
                            fragmentTransaction.commit();
                            break;
                        case R.id.nav_search_company:
                            MembersCompanyUser memberCompanyUser = new MembersCompanyUser();
                            Bundle bundle2=new Bundle();
                            bundle2.putString("user", getArguments().getString("user"));
                            bundle2.putString("status", getArguments().getString("status"));
                            bundle2.putString("senderName", getArguments().getString("senderName"));
                            bundle2.putString("senderPhoto", getArguments().getString("senderPhoto"));
                            memberCompanyUser.setArguments(bundle2);
                            FragmentTransaction fragmentTransactionn = getFragmentManager().beginTransaction();
                            fragmentTransactionn.replace(R.id.bottom_nav_search, memberCompanyUser);
                            fragmentTransactionn.commit();
                            break;
                    }

                    return true;
                }
            };

    @Override
    public void onAttach(Context context) {
        myContext=(FragmentActivity) context;
        super.onAttach(context);
    }
}
