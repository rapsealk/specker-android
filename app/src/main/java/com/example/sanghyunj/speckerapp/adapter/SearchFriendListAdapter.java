package com.example.sanghyunj.speckerapp.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.controller.Firebase;
import com.example.sanghyunj.speckerapp.database.FriendDbHelper;
import com.example.sanghyunj.speckerapp.fragment.FriendFragment;
import com.example.sanghyunj.speckerapp.model.FriendList.FriendListItem;
import com.example.sanghyunj.speckerapp.model.SearchedList.SearchedListItem;
import com.example.sanghyunj.speckerapp.retrofit.Api;
import com.example.sanghyunj.speckerapp.retrofit.Body.AddFriendBody;
import com.example.sanghyunj.speckerapp.retrofit.DefaultResponse;
import com.example.sanghyunj.speckerapp.retrofit.Response.Friend;
import com.example.sanghyunj.speckerapp.retrofit.Response.SearchedUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sanghyunj on 08/06/2017.
 */

public class SearchFriendListAdapter extends RecyclerView.Adapter<SearchFriendListAdapter.SearchFriendViewHolder>{

    private List<SearchedListItem> searchedListItemList;
    private Context context;

    private FirebaseAuth mFirebaseAuth;
    private FriendDbHelper mDbHelper;

    public SearchFriendListAdapter(Context context) {
        this.context = context;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDbHelper = new FriendDbHelper(context);
    }

    public void setFriendItemList(List<SearchedListItem> searchedListItemList) {
        this.searchedListItemList = searchedListItemList;
    }

    @Override
    public SearchFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        SearchFriendViewHolder viewHolder = new SearchFriendViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchFriendViewHolder holder, int position) {
        SearchedListItem user = searchedListItemList.get(position);

        if (user.getProfile() != null) {
            Glide.with(holder.profileImage.getContext())
                    .load(user.getProfile())
                    .into(holder.profileImage);
        }
        holder.name.setText(user.getName());

        Log.d("Adapter", "isKnown: " + user.isKnow() + ", name: " + user.getName());

        if (user.isKnow()) {
            holder.statusMessage.setText("친구");
            holder.statusMessage.setVisibility(View.VISIBLE);
            holder.addFriend.setVisibility(View.GONE);
        } else {
            holder.addFriend.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    // Firebase.getInstance().addFriend(searchedListItemList.get(position).getUid());

                    FirebaseAuth.getInstance().getCurrentUser().getToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        String token = task.getResult().getToken();
                                        Api api = Api.retrofit.create(Api.class);
                                        long timestamp = System.currentTimeMillis();
                                        Call<DefaultResponse> call = api.addFriend(token, new AddFriendBody(user.getId(), timestamp));
                                        new AddFriendTask(position, timestamp).execute(call);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, "다시 시도해보세요.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (null != searchedListItemList ? searchedListItemList.size() : 0);
    }

    public static class SearchFriendViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView name;
        TextView statusMessage;
        Button addFriend;

        public SearchFriendViewHolder(View v) {
            super(v);

            name = (TextView)v.findViewById(R.id.name);
            profileImage = (CircleImageView)v.findViewById(R.id.profile_image);
            statusMessage = (TextView) v.findViewById(R.id.status_message);
            addFriend = (Button)v.findViewById(R.id.add_friend);

            statusMessage.setVisibility(View.GONE);
            addFriend.setVisibility(View.VISIBLE);
        }
    }

    private class AddFriendTask extends AsyncTask<Call, Void, String> {

        private int position;
        private long timestamp;

        public AddFriendTask(int position, long timestamp) {
            this.position = position;
            this.timestamp = timestamp;
        }

        @Override
        protected String doInBackground(Call... params) {
            try {
                Call<DefaultResponse> call = params[0];
                Response<DefaultResponse> response = call.execute();

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        return response.code() + " " + response.body().toString();
                    }
                    else return response.code() + " response.body() is null";
                }
                return response.code() + " response failed";
            }
            catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("AsyncTask", result);
            SearchedListItem item = searchedListItemList.get(position);
            SearchedUser addedFriend = (SearchedUser) item.getElement();
            FriendFragment.friendListAdapter.addChatItem(new FriendListItem(addedFriend));
            FriendFragment.friendListAdapter.notifyDataSetChanged();
            long newRowid = mDbHelper.insertFriend(mFirebaseAuth.getCurrentUser().getUid(), new Friend(addedFriend.getId(), addedFriend.getName(), addedFriend.getProfile(), timestamp));
            Log.d("Friend Added", "Row Id: " + newRowid);
            searchedListItemList.remove(position);
            notifyDataSetChanged();
        }
    }
}
