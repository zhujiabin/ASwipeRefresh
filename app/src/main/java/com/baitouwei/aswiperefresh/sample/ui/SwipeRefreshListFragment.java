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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.baiouwei.example.R;
import com.baitouwei.aswiperefresh.sample.BaseFragment;
import com.baitouwei.aswiperefresh.sample.utils.SwipeRefreshHelp;
import com.baitouwei.swiperefresh.ASwipeRefreshLayout;

import java.util.Random;

/**
 * Created by bai on 15/9/16.
 */
public class SwipeRefreshListFragment extends BaseFragment {
    private static final String TAG = SwipeRefreshListFragment.class.getSimpleName();

    ListView listView;
    ArrayAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.swipe_list_fragment, container, false);
        swipeRefreshLayout = (ASwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        listView = (ListView) v.findViewById(R.id.list_view);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toast("Click:" + parent.getAdapter().getItem(position).toString());
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                toast("Long Click:" + parent.getAdapter().getItem(position).toString());
                return true;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d(TAG, "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        SwipeRefreshHelp.buildMaterialSwipeRefreshLayout(swipeRefreshLayout);
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
                data.add(0, "onSwipeSuccess");
                adapter.notifyDataSetChanged();
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
                adapter.add("onLoadMoreSuccess");
            }

            @Override
            public void onSwipeFail(ASwipeRefreshLayout aSwipeRefreshLayout) {
                toast("onLoadMoreFail");
            }
        });
    }
}
