package com.example.sanghyunj.speckerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.listener.OnFriendListItemClickListener;
import com.example.sanghyunj.speckerapp.model.FriendList.FriendListItem;
import com.example.sanghyunj.speckerapp.model.User;
import com.example.sanghyunj.speckerapp.util.ChatListConverter;
import com.example.sanghyunj.speckerapp.util.OrderingByKoreanEnglishNumberSpecial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/*
 * @FriendFragment
 */

public class FriendListAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

    private final Context mContext;
    private List<FriendListItem> friendListItemList;
    private String[] chatListIndicator;
    private int k = 0;

    private int[] mSectionIndices = { 0 };

    private LayoutInflater mInflater;

    private OnFriendListItemClickListener onFriendListItemClickListener;

    public FriendListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        chatListIndicator = context.getResources().getStringArray(R.array.chatListIndicator);
    }

    public void addChatItem(FriendListItem friendListItem) {
        this.friendListItemList.add(friendListItem);
        sortItemsByName();
    }

    public void setChatItems(List<FriendListItem> friendListItemList) {
        this.friendListItemList = new ArrayList<>();
        this.friendListItemList.addAll(friendListItemList);
    }

    public void setOnFriendListItemClickListener(OnFriendListItemClickListener onFriendListItemClickListener) {
        this.onFriendListItemClickListener = onFriendListItemClickListener;
    }

    @Override
    public int getCount() {
        return friendListItemList.size();
    }

    @Override
    public Object getItem(int position) {
//        return ((User)friendListItemList.get(position)).getName();
        return friendListItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_friend, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.profile_image = (CircleImageView) convertView.findViewById(R.id.profile_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(friendListItemList.get(position).getName());

        Glide.with(mContext).load(friendListItemList.get(position).getProfileImage())
                .into(holder.profile_image);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFriendListItemClickListener.onClick(position);
            }
        });

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.item_friend_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.header);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
//        CharSequence headerChar = mCountries[position].subSequence(0, 1);

        holder.text.setText(ChatListConverter.code2String(friendListItemList.get(position).getType()));

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
//        return friendListItemList.get(position).getType()+'전';
        return friendListItemList.get(position).getType();
    }

    @Override
    public int getPositionForSection(int section) {

        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {

        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return chatListIndicator;
    }

    public void clear() {
        mSectionIndices = new int[0];

        notifyDataSetChanged();
    }

    public void restore() {
        notifyDataSetChanged();
    }

    private class HeaderViewHolder {
        TextView text;
    }

    private class ViewHolder {
        TextView name;
        CircleImageView profile_image;
    }

    public void sortItemsByName() {
        Collections.sort(this.friendListItemList, OrderingByKoreanEnglishNumberSpecial.getComparator());
        // notifyDataSetChanged();
    }
}
