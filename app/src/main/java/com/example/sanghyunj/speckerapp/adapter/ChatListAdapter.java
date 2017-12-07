package com.example.sanghyunj.speckerapp.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.retrofit.Body.ChatroomMetaBody;
import com.example.sanghyunj.speckerapp.util.SharedPreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanghyunJ on 08/03/2017.
 */
public class ChatListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<ChatroomMetaBody> list = new ArrayList<>();

    private SimpleDateFormat todayFormat;
    private SimpleDateFormat yesterdayFormat;

    private SharedPreferenceManager mSharedPreferenceManager;

    public ChatListAdapter(Context context, List<ChatroomMetaBody> chatrooms) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.list = chatrooms;

        todayFormat = new SimpleDateFormat("a hh:mm");
        yesterdayFormat = new SimpleDateFormat("y.M.d");

        mSharedPreferenceManager = SharedPreferenceManager.getInstance(context);
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_chat_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.thumbnailImage = (ImageView) convertView.findViewById(R.id.thumbnailImage);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.lastChat = (TextView) convertView.findViewById(R.id.lastChat);
            viewHolder.lastTimestamp = (TextView) convertView.findViewById(R.id.lastTimestamp);
            viewHolder.unreadCount = (TextView) convertView.findViewById(R.id.chatCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatroomMetaBody item = list.get(index);
        // viewHolder.thumbnailImage
        int participants = item.participants.size();
        viewHolder.userName.setText(item.participants.get(0).substring(0, 10) + (participants > 2 ? "..." : "") + " (" + participants + ")");
        // viewHolder.userName.setText(item._id + ((item.participants > 2) ? "(" + item.participants + ")" : ""));
        viewHolder.lastChat.setText(item.lastChat);
        if (item.lastTimestamp > 0) viewHolder.lastTimestamp.setText(todayFormat.format(item.lastTimestamp));
        int unreadCount = mSharedPreferenceManager.getUnreadChatCount(item._id);
        viewHolder.unreadCount.setText(unreadCount > 0 ? Integer.toString(unreadCount) : "");

        return convertView;
    }

    @Override
    public long getItemId(int index) { return 0; }

    @Override
    public ChatroomMetaBody getItem(int index) { return list.get(index); }

    static class ViewHolder {
        private ImageView thumbnailImage;
        private TextView userName;
        private TextView lastTimestamp;
        private TextView lastChat;
        private TextView unreadCount;
    }
}

/* RecyclerView
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>{

    private Context context;
    private List<String> mItems = new ArrayList<>();

    private SimpleDateFormat todayFormat;
    private SimpleDateFormat yesterdayFormat;

    public ChatListAdapter(Context context, List<String> list) {
        this.context = context;
        this.mItems = list;

        todayFormat = new SimpleDateFormat("a hh:mm");
        yesterdayFormat = new SimpleDateFormat("y.M.d");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // return null;

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.lastChat.setText(mItems.get(position));
        holder.lastTimestamp.setText(todayFormat.format(System.currentTimeMillis()));
    }

    @Override
    public int getItemCount() {

        // return 0;

        return mItems.size();
    }


    
    static class ViewHolder extends RecyclerView.ViewHolder{

//        private final ImageButton thumbnailImage;
//        private final TextView filterNameView;
        private final ImageView thumbnailImage;
        private final TextView userName;
        private TextView lastTimestamp;
        private TextView lastChat;

        ViewHolder(View itemView) {
            super(itemView);

//            thumbnailImage = (ImageButton) itemView.findViewById(R.id.thumbnail_image);
//            filterNameView = (TextView) itemView.findViewById(R.id.filter_name);

            thumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnailImage);
            userName = (TextView) itemView.findViewById(R.id.userName);
            lastTimestamp = (TextView) itemView.findViewById(R.id.lastTimestamp);
            lastChat = (TextView) itemView.findViewById(R.id.lastChat);
        }

//        ImageButton getThumbnailImage() {
//
//            return thumbnailImage;
//        }
//
//        TextView getFilterNameView() {
//
//            return filterNameView;
//        }
    }
}
*/
