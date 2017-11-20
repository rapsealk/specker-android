package com.example.sanghyunj.speckerapp.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.view.pulltozoom.PullToZoomListViewEx;


/**
 * Created by lineplus on 2017. 6. 14..
 */

public class UserInfoActivity extends Activity {

    private PullToZoomListViewEx listView;
    private static final int LOW_DPI_STATUS_BAR_HEIGHT = 19;
    private static final int MEDIUM_DPI_STATUS_BAR_HEIGHT = 25;
    private static final int HIGH_DPI_STATUS_BAR_HEIGHT = 38;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        listView = (PullToZoomListViewEx) findViewById(R.id.listview);

        String[] adapterData = new String[]{};

        listView.setAdapter(new ArrayAdapter<String>(UserInfoActivity.this, android.R.layout.simple_list_item_1, adapterData));

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(mScreenWidth,mScreenHeight-getStatusBarSizeOnCreate());
        listView.setHeaderLayoutParams(localObject);

        String userName = getIntent().getStringExtra("userName");
        String profileImage = getIntent().getStringExtra("profileImage");
    }

    private int getStatusBarSizeOnCreate(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

        int statusBarHeight;

        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_HIGH:
                statusBarHeight = HIGH_DPI_STATUS_BAR_HEIGHT;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
                break;
            case DisplayMetrics.DENSITY_LOW:
                statusBarHeight = LOW_DPI_STATUS_BAR_HEIGHT;
                break;
            default:
                statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
        }

        return statusBarHeight;
//        Log.i("StatusBarTest" , "onCreate StatusBar Height= " + statusBarHeight);
    }
}
