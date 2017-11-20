package com.example.sanghyunj.speckerapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sanghyunj.speckerapp.action.Action;
import com.example.sanghyunj.speckerapp.activity.AddFriendActivity;
import com.example.sanghyunj.speckerapp.activity.EntranceActivity;
import com.example.sanghyunj.speckerapp.activity.LaunchActivity;
import com.example.sanghyunj.speckerapp.activity.WriteFeedActivity;
import com.example.sanghyunj.speckerapp.fragment.ChatFragment;
import com.example.sanghyunj.speckerapp.fragment.FriendFragment;
import com.example.sanghyunj.speckerapp.fragment.HomeFragment;
import com.example.sanghyunj.speckerapp.fragment.SettingFragment;
import com.example.sanghyunj.speckerapp.fragment.SurfingFragment;
import com.example.sanghyunj.speckerapp.listener.OnActionListener;
import com.example.sanghyunj.speckerapp.model.User;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private FirebaseDatabase mFirebaseDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    GoogleApiClient mGoogleApiClient;

    String mUsername;
    String mPhotoUrl;

    final String TAG = MainActivity.class.getName();

    private ChatBroadcastReceiver chatBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatBroadcastReceiver = new ChatBroadcastReceiver();
        registerReceiver(chatBroadcastReceiver, new IntentFilter("com.example.sanghyunj.speckerapp.RECEIVE_CHAT"));

        startActivity(new Intent(this, LaunchActivity.class));
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if ( firebaseUser == null ) {
            // FIXME: 로그인이 필요합니다.
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, EntranceActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            mUsername = firebaseUser.getDisplayName();
            if ( firebaseUser.getPhotoUrl() != null ) {
                mPhotoUrl = firebaseUser.getPhotoUrl().toString();

                User user = new User();
                user.setEmail(firebaseUser.getEmail());
                user.setFcmToken(FirebaseInstanceId.getInstance().getToken());
                user.setName(firebaseUser.getDisplayName());
                user.setProfile(firebaseUser.getPhotoUrl().toString());
                user.setUid(firebaseUser.getUid());

                mFirebaseDatabaseReference = FirebaseDatabase.getInstance();
//                if(mFirebaseDatabaseReference.getReference()==null)
                mFirebaseDatabaseReference.getReference().child("users").child(user.getUid()).setValue(user);
            }

            Toast.makeText(this, mUsername+"님 환영합니다.", Toast.LENGTH_LONG).show();
        }


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API).build();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // startActivity(new Intent(MainActivity.this, WriteFeedActivity.class));
            }
        });

        permissionCheck();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(chatBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.toolbar_button) {
            Intent intent = new Intent(this, AddFriendActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private FriendFragment friendFragment = null;
        private ChatFragment chatFragment = null;
        private HomeFragment homeFragment = null;
        private SurfingFragment surfingFragment = null;
        private SettingFragment settingFragment = null;

        private ArrayList<CharSequence> pageTitles = null;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            pageTitles = new ArrayList<>();
            pageTitles.add("친구목록");
            pageTitles.add("채팅");
            pageTitles.add("홈");
            pageTitles.add("서핑");
            pageTitles.add("정보");
        }

        @Override
        public Fragment getItem(int position) {

            //position %= 5;

            Log.d("SectionFragment", "position is " + position);

            switch (position){
                case 0: {
                    if (friendFragment == null) friendFragment = new FriendFragment();
                    // FriendFragment friendFragment = new FriendFragment();
                    return friendFragment;
                }
                case 1: {
                    if (chatFragment == null) chatFragment = new ChatFragment();
                    // ChatFragment chatFragment = new ChatFragment();
                    return chatFragment;
                }
                case 2: {
                    if (homeFragment == null) homeFragment = new HomeFragment();
                    // HomeFragment homeFragment = new HomeFragment();
                    return homeFragment;
                }
                case 3: {
                    if (surfingFragment == null) surfingFragment = new SurfingFragment();
                    // SurfingFragment surfingFragment = new SurfingFragment();
                    return surfingFragment;
                }
                case 4: {
                    if (settingFragment == null) settingFragment = new SettingFragment();
                    // SettingFragment settingFragment = new SettingFragment();
                    return settingFragment;
                }
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return pageTitles.size();
            // return 3; // 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d("Page", "Number: " + position);
            return pageTitles.get(position);
            /*
            switch (position) {
                case 0:
                    return "친구목록";
                case 1:
                    return "채팅";
                case 2:
                    return "홈";

                case 3:
                    return "서핑";
                case 4:
                    return "정보";

            }
            return null;
            */
        }
    }

    public void permissionCheck(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.INTERNET)
                .check();
    }

    public class ChatBroadcastReceiver extends BroadcastReceiver {

        public ChatBroadcastReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BroadcastReceiver", "onReceive with action RECEIVE_CHAT");
            Log.d("message", intent.getStringExtra("message"));
            // TODO FIX POSITION
            ChatFragment.mChatRooms.get(0).lastChat = intent.getStringExtra("message");
            ChatFragment.adapter.notifyDataSetChanged();
        }
    }
}
