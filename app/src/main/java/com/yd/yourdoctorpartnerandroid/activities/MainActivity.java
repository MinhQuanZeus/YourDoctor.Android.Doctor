package com.yd.yourdoctorpartnerandroid.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nhancv.npermission.NPermission;
import com.squareup.picasso.Picasso;
import com.yd.yourdoctorpartnerandroid.DoctorApplication;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.adapters.PagerAdapter;
import com.yd.yourdoctorpartnerandroid.events.EventSend;
import com.yd.yourdoctorpartnerandroid.fragments.AboutUsFragment;
import com.yd.yourdoctorpartnerandroid.fragments.AdvisoryMenuFragment;
import com.yd.yourdoctorpartnerandroid.fragments.DoctorProfileFragment;
import com.yd.yourdoctorpartnerandroid.fragments.DoctorRankFragment;
import com.yd.yourdoctorpartnerandroid.fragments.UserProfileFragment;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.TypeCall;
import com.yd.yourdoctorpartnerandroid.models.VideoCallSession;
import com.yd.yourdoctorpartnerandroid.utils.Config;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NPermission.OnPermissionResult {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.fab_question)
    FloatingActionButton fab_question;

    @BindView(R.id.draw_layout_main)
    DrawerLayout draw_layout_main;

    @BindView(R.id.nav_view_menu)
    NavigationView navigationView_main;

    @BindView(R.id.toolbar)
    Toolbar tb_main;

    ImageView iv_ava_user;
    TextView tv_name_user;
    TextView tv_money_user;

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private Socket socket;
    private Doctor userInfo;
    private NPermission nPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //EventBus.getDefault().register(this);
        setupUI();
        setupSocket();
//        Log.d("MainActivity", "USER_INFO");
//        Log.d("MainActivity", SharedPrefs.getInstance().get("USER_INFO", Patient.class).toString());
//        Log.d("MainActivity", SharedPrefs.getInstance().get("JWT_TOKEN", String.class));
//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                // Kiểm tra Intent Filter có khớp cái nào không.
//                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
//                    // GCM đã được đăng ký thành công.
//                    // Đăng ký vào topic có tên "Global".
//                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
//                    displayFirebaseRegId();
//
//                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
//                    // Khi có tin nhắn mới về.
//                    String message = intent.getStringExtra("message");
//                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
//                   // txtMessage.setText(message);
//                }
//            }
//        };
//
        nPermission = new NPermission(true);
        nPermission.requestPermission(this, Manifest.permission.CAMERA);
        displayFirebaseRegId();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventSend eventSend) {
        if(eventSend.getType() == 1){
//            currentPatient = SharedPrefs.getInstance().get("USER_INFO", Patient.class);
//            if(currentPatient != null){
//                tvNameUser.setText(currentPatient.getFullName());
//                Picasso.with(this).load(currentPatient.getAvatar().toString()).into(ivAvaUser);
//                tvMoneyUser.setText(currentPatient.getRemainMoney() + "" );
//            }
        }
    }


    private void setupSocket() {
        socket = DoctorApplication.self().getSocket();
        socket.connect();
    }
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);

        String regId = pref.getString("regId", null);
        SharedPrefs.getInstance().get("regId", String.class);
        Log.e(TAG, "Firebase reg id: " + SharedPrefs.getInstance().get("regId", String.class));

//        if (!TextUtils.isEmpty(regId))
//            Toast.makeText(this, "Firebase Reg Id: " + regId, Toast.LENGTH_SHORT).show();
//
//        else
//            Toast.makeText(this, "Firebase Reg Id is not received yet", Toast.LENGTH_SHORT).show();
//           // txtRegId.setText("Firebase Reg Id is not received yet!");
    }



    private void setupUI() {
        ButterKnife.bind(this);
        userInfo = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
        setSupportActionBar(tb_main);
        View headerView = navigationView_main.inflateHeaderView(R.layout.nav_header_main);
        iv_ava_user = headerView.findViewById(R.id.iv_ava_user);
        tv_name_user = headerView.findViewById(R.id.tv_name_user);
        tv_money_user = headerView.findViewById(R.id.tv_money_user);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        Picasso.with(this).load("https://kenh14cdn.com/2016/160722-star-tzuyu-1469163381381-1473652430446.jpg").transform(new CropCircleTransformation()).into(iv_ava_user);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, draw_layout_main, tb_main, R.string.app_name, R.string.app_name);
        draw_layout_main.setDrawerListener(toggle);
        toggle.syncState();

        navigationView_main.setNavigationItemSelectedListener(this);

        fab_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ScreenManager.openFragment(getSupportFragmentManager(), new AdvisoryMenuFragment(), R.id.rl_container, true, true);
            }
        });

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
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
            case R.id.nav_create_advisory_main: {
                ScreenManager.openFragment(getSupportFragmentManager(), new AdvisoryMenuFragment(), R.id.rl_container, true, true);
                break;
            }
            case R.id.nav_favorite_doctor_main: {
                break;
            }
            case R.id.nav_exchange_money_main: {
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
                //Test
                handleLogOut();
                //ScreenManager.openFragment(getSupportFragmentManager(), new DoctorProfileFragment(), R.id.rl_container, true, true);
                break;

            }
        }

        draw_layout_main.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab_question.setVisibility(View.VISIBLE);
        // Đăng ký receiver vào LocalBroadcastManager.
      //  LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
      //          new IntentFilter(Config.REGISTRATION_COMPLETE));

        // Đăng ký bộ nhận tin nhắn.
       // LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
              //  new IntentFilter(Config.PUSH_NOTIFICATION));

        // Xóa các notification khi app được bật.
       // NotificationUtils.clearNotifications(getApplicationContext());
    }

    private void handleLogOut(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Đăng Xuất")
                .setMessage("Bạn có chắc muốn thoát khỏi hệ thống không?")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.backToLogin(getApplicationContext());
                        DoctorApplication.self().getSocket().close();
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
        fab_question.setVisibility(View.INVISIBLE);
       // LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onPermissionResult(String s, boolean b) {
        switch (s) {
            case Manifest.permission.CAMERA:
                if (!b) {
                    nPermission.requestPermission(this, Manifest.permission.CAMERA);
                }
                break;
            default:
                break;
        }
    }
}




