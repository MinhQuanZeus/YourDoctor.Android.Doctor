package com.yd.yourdoctorpartnerandroid.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.nhancv.npermission.NPermission;
import com.squareup.picasso.Picasso;
import com.yd.yourdoctorpartnerandroid.DoctorApplication;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.adapters.PagerAdapter;
import com.yd.yourdoctorpartnerandroid.events.EventSend;
import com.yd.yourdoctorpartnerandroid.fragments.AboutUsFragment;
import com.yd.yourdoctorpartnerandroid.fragments.BankingFragment;
import com.yd.yourdoctorpartnerandroid.fragments.DoctorProfileFragment;
import com.yd.yourdoctorpartnerandroid.fragments.DoctorRankFragment;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.utils.Config;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.Utils;
import com.yd.yourdoctorpartnerandroid.utils.ZoomImageViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_PERMISSION_CODE = 1;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.draw_layout_main)
    DrawerLayout draw_layout_main;

    @BindView(R.id.nav_view_menu)
    NavigationView navigationView_main;

    @BindView(R.id.toolbar)
    Toolbar tb_main;

    ImageView ivAvaUser;
    ImageView ivAvaUserBackGroud;
    TextView tvNameUser;
    RatingBar rbMainDoctor;

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private Socket socket;
    private Doctor currentDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        setupUI();
        setupSocket();
    }


    private void setupSocket() {
        socket = DoctorApplication.self().getSocket();
        socket.connect();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventSend eventSend) {
        if(eventSend.getType() == 1){
            currentDoctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
            if(currentDoctor != null){
                tvNameUser.setText(currentDoctor.getFullName());
                ZoomImageViewUtils.loadImageManual(getApplicationContext(),currentDoctor.getAvatar().toString(),ivAvaUserBackGroud);
                ZoomImageViewUtils.loadCircleImage(getApplicationContext(),currentDoctor.getAvatar().toString(),ivAvaUser);
                tvNameUser.setText(currentDoctor.getFullName());
                rbMainDoctor.setRating(currentDoctor.getCurrentRating());
            }
        }
    }



    private void setupUI() {
        ButterKnife.bind(this);
        if (!checkPermission()) {
            requestPermission();
        }
        setSupportActionBar(tb_main);
        View headerView = navigationView_main.inflateHeaderView(R.layout.nav_header_main);
        ivAvaUser = headerView.findViewById(R.id.iv_ava_user);
        tvNameUser = headerView.findViewById(R.id.tv_name_user);
        ivAvaUserBackGroud = headerView.findViewById(R.id.iv_ava_user_back_groud);
        rbMainDoctor = headerView.findViewById(R.id.rbMainDoctor);
        currentDoctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
        if(currentDoctor != null){
            tvNameUser.setText(currentDoctor.getFullName());
            ZoomImageViewUtils.loadImageManual(getApplicationContext(),currentDoctor.getAvatar().toString(),ivAvaUserBackGroud);
            ZoomImageViewUtils.loadCircleImage(getApplicationContext(),currentDoctor.getAvatar().toString(),ivAvaUser);
            tvNameUser.setText(currentDoctor.getFullName());
            rbMainDoctor.setRating(currentDoctor.getCurrentRating());
        }


        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, draw_layout_main, tb_main, R.string.app_name, R.string.app_name);
        draw_layout_main.setDrawerListener(toggle);
        toggle.syncState();

        navigationView_main.setNavigationItemSelectedListener(this);


        tb_main.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw_layout_main.openDrawer(GravityCompat.START);
            }
        });


        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_notifications_none_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_book_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_history_black_24dp));

        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.icon_unselected), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.icon_selected), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.icon_unselected), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.icon_unselected), PorterDuff.Mode.SRC_IN);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), 4);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.getTabAt(1).select();
        viewPager.setCurrentItem(1);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.icon_unselected), PorterDuff.Mode.SRC_IN);
                tabLayout.getTabAt(tab.getPosition()).getIcon().setColorFilter(getResources().getColor(R.color.icon_selected), PorterDuff.Mode.SRC_IN);


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tabLayout.getTabAt(tab.getPosition()).getIcon().setColorFilter(getResources().getColor(R.color.icon_unselected), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onBackPressed() {

        if (draw_layout_main.isDrawerOpen(GravityCompat.START)) {
            draw_layout_main.closeDrawer(GravityCompat.START);
        } else {
            handleLogOut();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                draw_layout_main.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        if (item == null) return false;
        switch (item.getItemId()) {
            case R.id.nav_exchange_money_main: {
                ScreenManager.openFragment(getSupportFragmentManager(), new BankingFragment(), R.id.rl_container, true, true);
                break;
            }
            case R.id.nav_profile_main: {
                ScreenManager.openFragment(getSupportFragmentManager(), new DoctorProfileFragment(), R.id.rl_container, true, true);
                break;
            }
            case R.id.nav_ranking_docto_main: {
                ScreenManager.openFragment(getSupportFragmentManager(), new DoctorRankFragment(), R.id.rl_container, true, true);
                break;
            }
            case R.id.navAboutUs:{
                ScreenManager.openFragment(getSupportFragmentManager(), new AboutUsFragment(), R.id.rl_container, true, true);
                break;
            }
            case R.id.nav_logout_main: {
                handleLogOut();
                break;

            }
        }

        draw_layout_main.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void handleLogOut(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Đăng Xuất")
                .setMessage("Bạn có chắc muốn thoát khỏi hệ thống không?")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //DoctorApplication.self().getSocket().close();
                        DoctorApplication.self().getSocket().disconnect();
                        Utils.backToLogin(getApplicationContext());
                    }

                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                CAMERA);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        requestPermission();
                    }
                }
                break;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{RECORD_AUDIO, CAMERA}, REQUEST_PERMISSION_CODE);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}




