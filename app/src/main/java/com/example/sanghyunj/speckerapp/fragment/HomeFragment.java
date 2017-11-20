package com.example.sanghyunj.speckerapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.action.Action;
import com.example.sanghyunj.speckerapp.action.WriteFeedAction;
import com.example.sanghyunj.speckerapp.activity.EntranceActivity;
import com.example.sanghyunj.speckerapp.adapter.RecyclerListAdapter;
import com.example.sanghyunj.speckerapp.listener.OnActionListener;
import com.example.sanghyunj.speckerapp.model.OpenProjectFactory;
import com.example.sanghyunj.speckerapp.model.OpenProjectModel;
import com.example.sanghyunj.speckerapp.retrofit.AuthWithToken;
import com.example.sanghyunj.speckerapp.retrofit.Body.GetHomeFeed;
import com.example.sanghyunj.speckerapp.retrofit.DefaultResponse;
import com.example.sanghyunj.speckerapp.retrofit.Feed;
import com.example.sanghyunj.speckerapp.retrofit.FeedData;
import com.example.sanghyunj.speckerapp.retrofit.FeedService;
import com.example.sanghyunj.speckerapp.retrofit.SignUpUser;
import com.example.sanghyunj.speckerapp.util.DragDistanceConverterEg;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sanghyunJ on 05/03/2017.
 */
public class HomeFragment extends RecyclerFragment<OpenProjectModel> {

    private static final int SIMULATE_UNSPECIFIED = 0;
    private static final int SIMULATE_FRESH_FIRST = 1;
    private static final int SIMULATE_FRESH_NO_DATA = 2;
    private static final int SIMULATE_FRESH_FAILURE = 3;

    private static final int REQUEST_DURATION = 800;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final List<OpenProjectModel> mItemList = new ArrayList<>();

    private int mSimulateStatus;

    private OnActionListener actionListener;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private String nextIndex = "";

    public HomeFragment() { }
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSimulateStatus = SIMULATE_UNSPECIFIED;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), EntranceActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            // Toast.makeText(getContext(), firebaseUser.getDisplayName()+"님. Home입니다", Toast.LENGTH_SHORT).show();
            // Database Reference
            try {

                firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference ref = firebaseDatabase.getReference("users").child(firebaseUser.getUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Toast.makeText(getContext(), dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                /* Retrofit
                FeedService feedService = FeedService.retrofit.create(FeedService.class);
                final Call<Feed> call = feedService.getFeeds(nextIndex); // new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                new NetworkCall().execute(call);
                */
            }
            catch (Exception e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getOriginAdapter().setItemList(mItemList);
        getHeaderAdapter().notifyDataSetChanged();

        getRecyclerRefreshLayout().setDragDistanceConverter(new DragDistanceConverterEg());

//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        getRecyclerRefreshLayout().setRefreshView(new RefreshViewEg(getActivity()), layoutParams);

    }

    @Override
    protected RecyclerView.LayoutManager onCreateLayoutManager() {
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
//        return layoutManager;
        return new LinearLayoutManager(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @NonNull
    @Override
    public RecyclerListAdapter createAdapter() {
        return new RecyclerListAdapter() {
            {
                addViewType(OpenProjectModel.class, new ViewHolderFactory<ViewHolder>() {
                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent) {
                        return new ItemViewHolder(parent);
                    }
                });
            }
        };
    }

    @Override
    protected InteractionListener createInteraction() {
        return new ItemInteractionListener();
    }

    private interface RequestListener {
        void onSuccess(List<OpenProjectModel> openProjectModels);
        void onFailed();
    }

    private void simulateNetworkRequest(final RequestListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(REQUEST_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (isAdded()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mSimulateStatus == SIMULATE_FRESH_FAILURE) {
                                listener.onFailed();
                            } else if (mSimulateStatus == SIMULATE_FRESH_NO_DATA) {
                                listener.onSuccess(Collections.EMPTY_LIST);
                            } else {
                                if (!nextIndex.equals("-1")) {
                                    FeedService feedService = FeedService.retrofit.create(FeedService.class);
                                    final Call<Feed> call = feedService.getFeeds(new GetHomeFeed(nextIndex)); //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                    new NetworkCallForUIUpdate(listener).execute(call);
                                }
                            }

                            mSimulateStatus = SIMULATE_UNSPECIFIED;
                        }
                    });
                }
            }
        }).start();
    }

    private class ItemInteractionListener extends InteractionListener {
        @Override
        public void requestRefresh() {
            simulateNetworkRequest(new RequestListener() {
                @Override
                public void onSuccess(List<OpenProjectModel> openProjectModels) {
                    nextIndex = "";
                    mItemList.clear();
                    mItemList.addAll(openProjectModels);
                    getHeaderAdapter().notifyDataSetChanged();
                    ItemInteractionListener.super.requestRefresh();
                }

                @Override
                public void onFailed() {
                    ItemInteractionListener.super.requestFailure();
                }
            });
        }

        @Override
        public void requestMore() {
            simulateNetworkRequest(new RequestListener() {
                @Override
                public void onSuccess(List<OpenProjectModel> openProjectModels) {
                    mItemList.addAll(openProjectModels);
                    getHeaderAdapter().notifyDataSetChanged();
                    ItemInteractionListener.super.requestMore();
                }

                @Override
                public void onFailed() {
                    ItemInteractionListener.super.requestFailure();
                }
            });
        }
    }

    private class ItemViewHolder extends RecyclerListAdapter.ViewHolder<OpenProjectModel> {
        private final TextView mTvTitle;
        private final TextView mTvContent;
        private final TextView mTvAuthor;

        private final LinearLayout mLlContentPanel;

        public ItemViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(getActivity()).inflate(R.layout.simple_list_item, parent, false));

            mTvTitle = (TextView) itemView.findViewById(R.id.title);
            mTvContent = (TextView) itemView.findViewById(R.id.content);
            mTvAuthor = (TextView) itemView.findViewById(R.id.author);

            mLlContentPanel = (LinearLayout) itemView.findViewById(R.id.content_panel);
        }

        @Override
        public void bind(final OpenProjectModel item, int position) {
            mTvTitle.setText(item.getTitle());
            mTvContent.setText(item.getContent());
            mTvAuthor.setText(item.getAuthor());

            mLlContentPanel.setBackgroundColor(Color.parseColor(item.getColor()));

            if(position==0){
                itemView.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                        Action action = new WriteFeedAction();
                        actionListener.onAction(action);

                    }
                });
            } else {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), item.getTitle(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    public void setActionListener(OnActionListener actionListener) {
        this.actionListener = actionListener;
    }

    // Retrofit2 Network
    private class NetworkCall extends AsyncTask<Call, Void, String> {

        @Override
        protected String doInBackground(Call... params) {
            try {
                Call<Feed> call = params[0];
                Response<Feed> response = call.execute();

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().data.size() == 0) nextIndex = "-1";
                        else nextIndex = response.body().nextIndex;
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
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateWithFeed(List<FeedData> listt, final RequestListener listener) {
        // 2017-10-14
        String[] color = { "#FF1B6070", "#FF234458", "#FF700957", "#FF940044", "#FF5B83ED", "#FFC47B3F" };
        List<OpenProjectModel> list = new ArrayList<>();
        int index = 0;
        for (FeedData feed: listt) {
            // Toast.makeText(getContext(), "Title: " + feed.title + ", content: " + feed.content, Toast.LENGTH_SHORT).show();
            list.add(new OpenProjectModel(feed.title, feed.content, color[index++ % 6]));
        }
        listener.onSuccess(list);
    }

    private class NetworkCallForUIUpdate extends AsyncTask<Call, Void, Feed> {

        public RequestListener listener;

        public NetworkCallForUIUpdate(final RequestListener listener) {
            this.listener = listener;
        }

        @Override protected Feed doInBackground(Call... params) {
            Call<Feed> call = params[0];
            try {
                Response<Feed> response = call.execute();
                if (response.body() != null) nextIndex = response.body().nextIndex;
                return response.isSuccessful() ? response.body() : null;
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override protected void onPostExecute(Feed result) {
            if (result == null) return;
            updateWithFeed(result.data, listener);
        }
    }
}
