package com.example.sanghyunj.speckerapp.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by rapsealk on 2017. 12. 1..
 */
/*
public class OnRecyclerMarkerClickListener implements RecyclerView.OnItemTouchListener {

    public static interface OnItemClickListener {
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mClickListener;
    private GestureDetector mGestureDetector;

    public OnRecyclerMarkerClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mClickListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView == null || mClickListener == null) return;
                mClickListener.onItemLongClick(childView, recyclerView.getChildPosition(childView));
            }
            @Override
            public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
                View childView = view.findChildViewUnder(e.getX(), e.getY());

            }
        });
    }
}
*/