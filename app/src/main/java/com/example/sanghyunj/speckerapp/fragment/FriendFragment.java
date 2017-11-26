package com.example.sanghyunj.speckerapp.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.activity.UserActivity;
import com.example.sanghyunj.speckerapp.activity.UserInfoActivity;
import com.example.sanghyunj.speckerapp.adapter.FriendListAdapter;
import com.example.sanghyunj.speckerapp.controller.Firebase;
import com.example.sanghyunj.speckerapp.controller.FriendListObserver;
import com.example.sanghyunj.speckerapp.database.FriendDbHelper;
import com.example.sanghyunj.speckerapp.listener.OnFriendListItemClickListener;
import com.example.sanghyunj.speckerapp.listener.OnFriendListItemLongClickListener;
import com.example.sanghyunj.speckerapp.model.FriendList.FriendListItem;
import com.example.sanghyunj.speckerapp.retrofit.Api;
import com.example.sanghyunj.speckerapp.retrofit.Body.GetFriendListBody;
import com.example.sanghyunj.speckerapp.retrofit.Body.RemoveFriendBody;
import com.example.sanghyunj.speckerapp.retrofit.DefaultResponse;
import com.example.sanghyunj.speckerapp.retrofit.Response.Friend;
import com.example.sanghyunj.speckerapp.retrofit.Response.GetFriendsListResponse;
import com.example.sanghyunj.speckerapp.util.OrderingByKoreanEnglishNumberSpecial;
import com.example.sanghyunj.speckerapp.view.CoordinateStickyListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Response;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by sanghyunJ on 05/03/2017.
 */
public class FriendFragment extends Fragment implements FriendListObserver{

    private ExpandableStickyListHeadersListView mListView;
    public static FriendListAdapter friendListAdapter;
    WeakHashMap<View,Integer> mOriginalViewHeightPool = new WeakHashMap<View, Integer>();

    private List<FriendListItem> friendListItemList;

    private FirebaseAuth mFirebaseAuth;

    public FriendDbHelper mDbHelper;
    public static long mLastFriendAddedAt = -1;

    public FriendFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_friend, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mListView = (CoordinateStickyListView) rootView.findViewById(R.id.friend_list);
        mListView.setAnimExecutor(new AnimationExecutor());

        friendListAdapter = new FriendListAdapter(getContext());

        friendListItemList = new ArrayList<>();

        friendListAdapter.setChatItems(friendListItemList);

        friendListAdapter.setOnFriendListItemClickListener(new OnFriendListItemClickListener() {
            @Override
            public void onClick(int position) {
                FriendListItem friend = (FriendListItem) friendListAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), UserActivity.class);
                // Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtra("userName", friend.getName());
                intent.putExtra("profileImage", friend.getProfileImage());
                intent.putExtra("userId", friend.getUid());
                startActivity(intent);
            }
        });

        mListView.setAdapter(friendListAdapter);

        mListView.setVerticalScrollBarEnabled(false);
        mListView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (mListView.isHeaderCollapsed(headerId)) {
                    mListView.expand(headerId);
                } else {
                    mListView.collapse(headerId);
                }
            }
        });

        // Log.d("asdsadasd","sadasdasdas");
        // TODO check friends list
        Firebase.getInstance().addFriendListObserver(this);
        Firebase.getInstance().notifyFriendListObserver();

        mDbHelper = new FriendDbHelper(getContext());

        ArrayList<Friend> mFriends = mDbHelper.getFriends();
        for (Friend friend: mFriends) {
            friendListAdapter.addChatItem(new FriendListItem(friend));
            mLastFriendAddedAt = friend.getTimestamp();
        }
        friendListAdapter.notifyDataSetChanged();

        Api api = Api.retrofit.create(Api.class);
        FirebaseAuth.getInstance().getCurrentUser().getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            Call<GetFriendsListResponse> call = api.getFriendsList(token, new GetFriendListBody(mLastFriendAddedAt));
                            new GetFriendsListFromServerTask().execute(call);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        return rootView;
    }

    private class AnimationExecutor implements ExpandableStickyListHeadersListView.IAnimationExecutor {

        @Override
        public void executeAnim(final View target, final int animType) {
            if(ExpandableStickyListHeadersListView.ANIMATION_EXPAND==animType&&target.getVisibility()==View.VISIBLE){
                return;
            }
            if(ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE==animType&&target.getVisibility()!=View.VISIBLE){
                return;
            }
            if(mOriginalViewHeightPool.get(target)==null){

                mOriginalViewHeightPool.put(target,target.getHeight());

            }
            final int viewHeight = mOriginalViewHeightPool.get(target);
            float animStartY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f : viewHeight;
            float animEndY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight : 0f;
            final ViewGroup.LayoutParams lp = target.getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofFloat(animStartY, animEndY);
            animator.setDuration(200);
            target.setVisibility(View.VISIBLE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND) {
                        target.setVisibility(View.VISIBLE);
                    } else {
                        target.setVisibility(View.GONE);
                    }
                    target.getLayoutParams().height = viewHeight;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    lp.height = ((Float) valueAnimator.getAnimatedValue()).intValue();
                    target.setLayoutParams(lp);
                    target.requestLayout();
                }
            });
            animator.start();
        }
    }

    @Override
    public void update(List<FriendListItem> friendListItemList) {
        Collections.sort(friendListItemList, OrderingByKoreanEnglishNumberSpecial.getComparator());
        friendListAdapter.setChatItems(friendListItemList);
        friendListAdapter.notifyDataSetChanged();
    }

    private class GetFriendsListFromServerTask extends AsyncTask<Call, Void, String> {

        @Override
        protected String doInBackground(Call... params) {
            try {
                Call<GetFriendsListResponse> call = params[0];
                Response<GetFriendsListResponse> response = call.execute();

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for (Friend friend: response.body().friends) {
                            friendListAdapter.addChatItem(new FriendListItem(friend));
                            mLastFriendAddedAt = friend.getTimestamp();
                            long newRowId = mDbHelper.insertFriend(mFirebaseAuth.getCurrentUser().getUid(), friend);
                            Log.d("FriendListItem", friend.toString());
                            Log.d("Friend Inserted", "Row: " + newRowId);
                        }
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
            friendListAdapter.notifyDataSetChanged();
        }
    }

    private class RemoveFriendTask extends AsyncTask<Call, Void, String> {

        private String friendUid;

        public RemoveFriendTask(String friendUid) {
            this.friendUid = friendUid;
        }

        @Override
        protected String doInBackground(Call... params) {
            Call<DefaultResponse> call = params[0];
            Response<DefaultResponse> response;
            try {
                response = call.execute();
            }
            catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }
            if (!response.isSuccessful()) return response.code() + " response failed";
            if (response.body() == null) return response.code() + " response.body() is null";
            if (response.body().result.equals("ok")) {
                int result = mDbHelper.removeFriend(mFirebaseAuth.getCurrentUser().getUid(), friendUid);
                Log.d("Remove Friend", "result: " + result + ", friend: " + friendUid);
                friendListAdapter.removeChatItemByUid(friendUid);
            }
            return response.body().result;
        }

        @Override protected void onPostExecute(String result) {
            Log.d("RemoveFriendTask", result);
            friendListAdapter.notifyDataSetChanged();
        }
    }
}

