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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.baiouwei.example.R;
import com.baitouwei.aswiperefresh.sample.BaseFragment;
import com.baitouwei.aswiperefresh.sample.utils.SwipeRefreshHelp;
import com.baitouwei.swiperefresh.ASwipeRefreshLayout;
import com.baitouwei.swiperefresh.internal.DefaultSwipeView;

import java.util.Random;

/**
 * Created by bai on 15/9/16.
 */
public class SwipeRefreshWebViewFragment extends BaseFragment {
    private static final String TAG = SwipeRefreshWebViewFragment.class.getSimpleName();

    private static final String LOAD_URL = "http://www.v2ex.com";
    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.swipe_webview_fragment, container, false);

        swipeRefreshLayout = (ASwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        webView = (WebView) v.findViewById(R.id.web_view);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setUseWideViewPort(false);

        SwipeRefreshHelp.buildNormalSwipeRefreshLayout(swipeRefreshLayout);
        swipeRefreshLayout.setHeaderView(new DefaultSwipeView(getActivity()));
        swipeRefreshLayout.setFooterView(new DefaultSwipeView(getActivity()));
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
                webView.loadUrl(LOAD_URL);
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
            }

            @Override
            public void onSwipeFail(ASwipeRefreshLayout aSwipeRefreshLayout) {
                toast("onLoadMoreFail");
            }

        });

        webView.loadUrl(LOAD_URL);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
