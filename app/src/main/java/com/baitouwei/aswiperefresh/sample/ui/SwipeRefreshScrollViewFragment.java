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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baiouwei.example.R;
import com.baitouwei.aswiperefresh.sample.BaseFragment;
import com.baitouwei.aswiperefresh.sample.ui.custom.DebugSwipeView;
import com.baitouwei.aswiperefresh.sample.ui.custom.DrawerSwipeView;
import com.baitouwei.aswiperefresh.sample.utils.SwipeRefreshHelp;
import com.baitouwei.swiperefresh.ASwipeRefreshLayout;

import java.util.Random;

/**
 * Created by bai on 15/9/16.
 */
public class SwipeRefreshScrollViewFragment extends BaseFragment {
    private static final String TAG = SwipeRefreshScrollViewFragment.class.getSimpleName();

    private LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_scrollview_fragment, container, false);
        swipeRefreshLayout = (ASwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        linearLayout = (LinearLayout) view.findViewById(R.id.ll);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwipeRefreshHelp.buildDrawerSwipeRefreshLayout(swipeRefreshLayout);

        swipeRefreshLayout.setHeaderView(new DrawerSwipeView(getActivity()));
        swipeRefreshLayout.setFooterView(new DebugSwipeView(getActivity()));

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
                linearLayout.addView(getTextView("onSwipeSuccess"), 0);
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
                linearLayout.addView(getTextView("onLoadMoreSuccess"));
            }

            @Override
            public void onSwipeFail(ASwipeRefreshLayout aSwipeRefreshLayout) {
                toast("onLoadMoreFail");
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        for (String s : data) {
            linearLayout.addView(getTextView(s));
        }
    }

    private View getTextView(String s) {
        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setTextSize(24);
        textView.setPadding(2, 2, 2, 2);
        textView.setText(s);
        return textView;
    }
}
