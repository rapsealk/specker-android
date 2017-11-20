package com.example.sanghyunj.speckerapp.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.adapter.RecyclerListAdapter;
import com.example.sanghyunj.speckerapp.listener.OnActionListener;
import com.example.sanghyunj.speckerapp.model.OpenProjectFactory;
import com.example.sanghyunj.speckerapp.model.OpenProjectModel;
import com.example.sanghyunj.speckerapp.util.DragDistanceConverterEg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sanghyunJ on 05/03/2017.
 */
public class SurfingFragment extends RecyclerFragment<OpenProjectModel> {
    private static final int SIMULATE_UNSPECIFIED = 0;
    private static final int SIMULATE_FRESH_FIRST = 1;
    private static final int SIMULATE_FRESH_NO_DATA = 2;
    private static final int SIMULATE_FRESH_FAILURE = 3;

    private static final int REQUEST_DURATION = 800;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final List<OpenProjectModel> mItemList = new ArrayList<>();

    private int mSimulateStatus;

    private OnActionListener actionListener;

    public SurfingFragment() { }
    public static SurfingFragment newInstance() {
        return new SurfingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSimulateStatus = SIMULATE_UNSPECIFIED;
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
        return new GridLayoutManager(getContext(),2);
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
                addViewType(OpenProjectModel.class, new ViewHolderFactory<RecyclerListAdapter.ViewHolder>() {
                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent) {
                        return new SurfingFragment.ItemViewHolder(parent);
                    }
                });
            }
        };
    }

    @Override
    protected InteractionListener createInteraction() {
        return new SurfingFragment.ItemInteractionListener();
    }

    private void simulateNetworkRequest(final SurfingFragment.RequestListener listener) {
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
                                listener.onSuccess(OpenProjectFactory.createOpenProjects());
                            }

                            mSimulateStatus = SIMULATE_UNSPECIFIED;
                        }
                    });
                }
            }
        }).start();
    }

    private interface RequestListener {
        void onSuccess(List<OpenProjectModel> openProjectModels);

        void onFailed();
    }

    private class ItemInteractionListener extends InteractionListener {
        @Override
        public void requestRefresh() {
            simulateNetworkRequest(new SurfingFragment.RequestListener() {
                @Override
                public void onSuccess(List<OpenProjectModel> openProjectModels) {
                    mItemList.clear();
                    mItemList.addAll(openProjectModels);
                    getHeaderAdapter().notifyDataSetChanged();
                    SurfingFragment.ItemInteractionListener.super.requestRefresh();
                }

                @Override
                public void onFailed() {
                    SurfingFragment.ItemInteractionListener.super.requestFailure();
                }
            });
        }

        @Override
        public void requestMore() {
            simulateNetworkRequest(new SurfingFragment.RequestListener() {
                @Override
                public void onSuccess(List<OpenProjectModel> openProjectModels) {
                    mItemList.addAll(openProjectModels);
                    getHeaderAdapter().notifyDataSetChanged();
                    SurfingFragment.ItemInteractionListener.super.requestMore();
                }

                @Override
                public void onFailed() {
                    SurfingFragment.ItemInteractionListener.super.requestFailure();
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), item.getTitle(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void setActionListener(OnActionListener actionListener) {
        this.actionListener = actionListener;
    }
}
