package com.yd.yourdoctorpartnerandroid.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.events.EventSend;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.Specialist;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryTransactionFragment extends Fragment {

    @BindView(R.id.tabHistory)
    TabLayout tabHistory;

    @BindView(R.id.vpHistory)
    ViewPager vpHistory;

    @BindView(R.id.tv_remain_money_history)
    TextView tvRemainMoneyHistory;

    private Doctor currentDoctor;


    public HistoryTransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_transaction, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        setUpData();
        return view;
    }
    private void setUpData(){
        currentDoctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
        if(currentDoctor != null) tvRemainMoneyHistory.setText("Số dư tài khoản : " + Utils.formatStringNumber(currentDoctor.getRemainMoney()) +" đ");
        vpHistory.setCurrentItem(0);
        tabHistory.setupWithViewPager(vpHistory);
        tabHistory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpHistory.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        HistoryTransactionFragment.ViewPagerAdapter adapter = new HistoryTransactionFragment.ViewPagerAdapter(getFragmentManager());
        vpHistory.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventSend eventSend) {
        if(eventSend.getType() == 1){
            currentDoctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
            if(currentDoctor != null){
                tvRemainMoneyHistory.setText("Số dư tài khoản : " + Utils.formatStringNumber(currentDoctor.getRemainMoney()) +" đ");
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        //private List typeHistory;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            //this.typeHistory = typeHistory;
        }


        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:{
                    return new ListChatHistoryFragment();
                }
                case 1:{
                    return new VideoCallHistoryFragment();
                }
                case 2:{
                    return new ListPaymentHistoryFragment();
                }
                case 3:{
                    return new BankingHistoryFragment();
                }
            }
            return null;

        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0 :{
                    return "Chat";
                }
                case 1 :{
                    return "Video Call";
                }
                case 2 :{
                    return "Thanh toán";
                }
                case 3 :{
                    return "Ngân Hàng";
                }
            }
            return "Chat";
        }
    }

}
