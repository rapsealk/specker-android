package com.example.sanghyunj.speckerapp.listener;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by rapsealk on 2017. 11. 26..
 */

public interface OnFriendListItemLongClickListener {
    boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id);
}
