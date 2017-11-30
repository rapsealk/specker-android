package com.example.sanghyunj.speckerapp.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.retrofit.Response.SPKMarker;

import java.util.ArrayList;

/**
 * Created by rapsealk on 2017. 11. 30..
 */

public class TeamListAdapter extends BaseAdapter { // <V, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<SPKMarker> markers = new ArrayList<>();

    public TeamListAdapter(Context context, ArrayList<SPKMarker> markers) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.markers = markers;
    }

    @Override
    public SPKMarker getItem(int position) {
        return markers.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_team_on_map, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.snippet = (TextView) convertView.findViewById(R.id.snippet);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SPKMarker marker = markers.get(position);
        viewHolder.title.setText(marker.getTitle());
        viewHolder.snippet.setText(marker.getSnippet());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return markers.get(position).getTimestamp();
    }

    @Override
    public int getCount() {
        return markers.size();
    }

    /*
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_on_map, parent, false);
        return (VH) new TeamListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        SPKMarker marker = markers.get(position);
        ((TeamListViewHolder) viewHolder).title.setText(marker.getTitle());
        ((TeamListViewHolder) viewHolder).snippet.setText(marker.getSnippet());
    }

    @Override
    public int getItemCount() {
        return markers.size();
    }
    */

    public void setItems(ArrayList<SPKMarker> markers) {
        this.markers = markers;
    }

    public void addItem(SPKMarker marker) {
        markers.add(marker);
    }

    static class ViewHolder {
        private TextView title;
        private TextView snippet;
    }
}

class TeamListViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView snippet;

    public TeamListViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        snippet = (TextView) view.findViewById(R.id.snippet);
    }
}