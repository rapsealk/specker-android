package com.example.sanghyunj.speckerapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
// import android.support.design.widget.FloatingActionButton;
// import android.support.design.widget.Snackbar;
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
import com.example.sanghyunj.speckerapp.activity.MapActivity;
import com.example.sanghyunj.speckerapp.activity.WriteFeedActivity;
import com.example.sanghyunj.speckerapp.fragment.ChatFragment;
import com.example.sanghyunj.speckerapp.fragment.FriendFragment;
import com.example.sanghyunj.speckerapp.fragment.HomeFragment;
import com.example.sanghyunj.speckerapp.fragment.SettingFragment;
import com.example.sanghyunj.speckerapp.fragment.SurfingFragment;
import com.example.sanghyunj.speckerapp.listener.OnActionListener;
import com.example.sanghyunj.speckerapp.model.User;

import com.example.sanghyunj.speckerapp.retrofit.Body.ChatroomMetaBody;
import com.example.sanghyunj.speckerapp.util.SharedPreferenceManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
    private TabLayout tabLayout;
    // private boolean mXORFlag;
    private int[] tabIcons;

    private FirebaseDatabase mFirebaseDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    GoogleApiClient mGoogleApiClient;

    String mUsername;
    String mPhotoUrl;

    final String TAG = MainActivity.class.getName();

    private SharedPreferenceManager mSharedPreferenceManager;
    private ChatBroadcastReceiver chatBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferenceManager = SharedPreferenceManager.getInstance(this);

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

        tabIcons = new int[] {
                R.drawable.tab_users_1x,
                R.drawable.tab_chat_1x,
                R.drawable.tab_feed_1x,
                R.drawable.tab_surf_1x,
                R.drawable.tab_mypage_1x
        };

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("onPageSelected", "position: " + position);
                tabLayout.getTabAt(0).setIcon(R.drawable.tab_users_gray_1x);
                tabLayout.getTabAt(1).setIcon(R.drawable.tab_chat_gray_1x);
                tabLayout.getTabAt(2).setIcon(R.drawable.tab_feed_gray_1x);
                tabLayout.getTabAt(3).setIcon(R.drawable.tab_surf_gray_1x);
                tabLayout.getTabAt(4).setIcon(R.drawable.tab_mypage_gray_1x);
                tabLayout.getTabAt(position).setIcon(tabIcons[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.tab_users_1x);
        tabLayout.getTabAt(1).setIcon(R.drawable.tab_chat_gray_1x);
        tabLayout.getTabAt(2).setIcon(R.drawable.tab_feed_gray_1x);
        tabLayout.getTabAt(3).setIcon(R.drawable.tab_surf_gray_1x);
        tabLayout.getTabAt(4).setIcon(R.drawable.tab_mypage_gray_1x);

        // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // startActivity(new Intent(MainActivity.this, WriteFeedActivity.class));
            }
        });
        */

        FloatingActionMenu fab = (FloatingActionMenu) findViewById(R.id.fab);
        FloatingActionButton mButtonFeed = (FloatingActionButton) findViewById(R.id.menu_item_feed);
        FloatingActionButton mButtonMap = (FloatingActionButton) findViewById(R.id.menu_item_map);

        fab.setClosedOnTouchOutside(true);

        mButtonFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WriteFeedActivity.class);
                startActivity(intent);
            }
        });

        mButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
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

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            position %= 5;

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
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
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
            */
            return null;
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
            String room = intent.getStringExtra("room");
            String message = intent.getStringExtra("message");
            long timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis());
            int index = -1;
            for (index = 0; index < ChatFragment.mChatRooms.size(); index++) {
                if (ChatFragment.mChatRooms.get(index)._id.equals(room)) {
                    ChatFragment.mChatRooms.get(index).lastChat = message;
                    ChatFragment.mChatRooms.get(index).lastTimestamp = timestamp;
                    ChatFragment.mChatRooms.add(0, ChatFragment.mChatRooms.remove(index));
                    break;
                }
            }
            if (index == ChatFragment.mChatRooms.size()) {
                ArrayList<String> participants = new ArrayList<>();
                participants.add(room);
                ChatFragment.mChatRooms.add(0, new ChatroomMetaBody(room, participants, message, timestamp));
            }
            if (!mSharedPreferenceManager.getRoomStatus(room)) {
                mSharedPreferenceManager.setUnreadChatCount(room, mSharedPreferenceManager.getUnreadChatCount(room) + 1);
            }
            ChatFragment.adapter.notifyDataSetChanged();
        }
    }
}
