/*
 * Copyright (C) 2015 baitouwei.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baitouwei.aswiperefresh.sample.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baiouwei.example.R;
import com.baitouwei.aswiperefresh.sample.BaseFragment;
import com.baitouwei.aswiperefresh.sample.ui.custom.LeftInSwipeView;
import com.baitouwei.aswiperefresh.sample.ui.custom.RightInSwipeView;
import com.baitouwei.aswiperefresh.sample.utils.SwipeRefreshHelp;
import com.baitouwei.swiperefresh.ASwipeRefreshLayout;

import java.util.List;
import java.util.Random;

/**
 * @author baitouwei
 */
public class SwipeRefreshRecycleViewFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SwipeRefreshRecycleViewFragment.class.getSimpleName();

    private DemoAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new DemoAdapter(data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_recycle_view_fragment, container, false);

        swipeRefreshLayout = (ASwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        view.findViewById(R.id.button1).setOnClickListener(this);
        view.findViewById(R.id.button2).setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        SwipeRefreshHelp.buildSideInSwipeRefreshLayout(swipeRefreshLayout);

        swipeRefreshLayout.setHeaderView(new LeftInSwipeView(getActivity()));
        swipeRefreshLayout.setFooterView(new RightInSwipeView(getActivity()));

        swipeRefreshLayout.setSwipeDownRefreshListener(new ASwipeRefreshLayout.OnSwipeRefreshListener() {
            @Override
            public void onSwipeRefresh(ASwipeRefreshLayout aSwipeRefreshLayout) {
                toast("onSwipeRefresh");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (new Random().nextBoolean()) {
                            swipeRefreshLayout.refreshSuccess();
                        } else {
                            swipeRefreshLayout.refreshFail();
                        }
                    }
                }, DURATION);
            }

            @Override
            public void onSwipeSuccess(ASwipeRefreshLayout aSwipeRefreshLayout) {
                toast("onSwipeSuccess");
                mAdapter.add(0, "onSwipeSuccess");
            }

            @Override
            public void onSwipeFail(ASwipeRefreshLayout aSwipeRefreshLayout) {
                toast("onSwipeFail");
            }
        });
        swipeRefreshLayout.setSwipeUpRefreshListener(new ASwipeRefreshLayout.OnSwipeRefreshListener() {
            @Override
            public void onSwipeRefresh(ASwipeRefreshLayout aSwipeRefreshLayout) {
                toast("onLoadMore");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (new Random().nextBoolean()) {
                            swipeRefreshLayout.loadMoreSuccess();
                        } else {
                            swipeRefreshLayout.loadMoreFail();
                        }
                    }
                }, DURATION);
            }

            @Override
            public void onSwipeSuccess(ASwipeRefreshLayout aSwipeRefreshLayout) {
                toast("onLoadMoreSuccess");
                mAdapter.add("onLoadMoreSuccess");
            }

            @Override
            public void onSwipeFail(ASwipeRefreshLayout aSwipeRefreshLayout) {
                toast("onLoadMoreFail");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                break;
            case R.id.button2:
                mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                break;
        }
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.ViewHolder> {
        private List<String> data;

        public DemoAdapter(List<String> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.textView.setText(data.get(position));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toast("Click " + position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void add(int pos, String item) {
            data.add(pos, item);
            notifyDataSetChanged();
        }

        public void add(String item) {
            data.add(item);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
