package com.example.sanghyunj.speckerapp.fragment;

/**
 * Created by sanghyunJ on 07/03/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.listener.OnActionListener;

/**
 * Created by sanghyunJ on 05/03/2017.
 */
public class SettingFragment extends Fragment {

    private OnActionListener actionListener;

    public SettingFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("SettingFragment");
        return rootView;
    }

    public void setActionListener(OnActionListener actionListener) {
        this.actionListener = actionListener;
    }
}
