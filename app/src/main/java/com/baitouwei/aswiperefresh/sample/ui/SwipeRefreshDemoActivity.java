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
import android.view.Menu;
import android.view.MenuItem;

import com.baiouwei.example.R;
import com.baitouwei.aswiperefresh.sample.BaseActivity;
import com.baitouwei.aswiperefresh.sample.BaseFragment;
import com.baitouwei.swiperefresh.ASwipeRefreshLayout;


/**
 * Created by bai on 15/9/16.
 */
public class SwipeRefreshDemoActivity extends BaseActivity {
    private static final String TAG = SwipeRefreshDemoActivity.class.getSimpleName();
    public static final String PARAM_DEMO_CLASS = "swipe_refresh_demo_class";

    private ASwipeRefreshLayout refreshLayout;
    private BaseFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        Class c = (Class) getIntent().getSerializableExtra(PARAM_DEMO_CLASS);
        if (c != null) {
            if (c.equals(SwipeRefreshListFragment.class)) {
                fragment = new SwipeRefreshListFragment();
            } else if (c.equals(SwipeRefreshRecycleViewFragment.class)) {
                fragment = new SwipeRefreshRecycleViewFragment();

            } else if (c.equals(SwipeRefreshScrollViewFragment.class)) {
                fragment = new SwipeRefreshScrollViewFragment();

            } else if (c.equals(SwipeRefreshWebViewFragment.class)) {
                fragment = new SwipeRefreshWebViewFragment();
            } else if (c.equals(SwipeRefreshViewPagerFragment.class)) {
                fragment = new SwipeRefreshViewPagerFragment();
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, fragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_demo, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        refreshLayout = fragment.getSwipeRefreshLayout();
        if (refreshLayout == null) {
            return false;
        }
        menu.findItem(R.id.action_auto_refresh).setChecked(refreshLayout.isAutoSwipeDownRefresh());
        menu.findItem(R.id.action_auto_load_more).setChecked(refreshLayout.isAutoLoadMore());
        menu.findItem(R.id.action_auto_scroll_after_loadmore).setChecked(refreshLayout.isAutoScrollAfterLoadMore());
        menu.findItem(R.id.action_loadmore_skip_num).setChecked(refreshLayout.getLoadMoreSkipNum() > 0 ? true : false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        refreshLayout = fragment.getSwipeRefreshLayout();
        if (refreshLayout == null) {
            return false;
        }

        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshLayout.refreshStart();
                return true;
            case R.id.action_refresh_success:
                refreshLayout.refreshSuccess();
                return true;
            case R.id.action_refresh_fail:
                refreshLayout.refreshFail();
                return true;
            case R.id.action_load_more:
                refreshLayout.loadMoreStart();
                return true;
            case R.id.action_load_more_success:
                refreshLayout.loadMoreSuccess();
                return true;
            case R.id.action_load_more_fail:
                refreshLayout.loadMoreFail();
                return true;
            case R.id.action_auto_load_more:
                item.setChecked(!item.isChecked());
                refreshLayout.setAutoLoadMore(item.isChecked());
                return false;
            case R.id.action_auto_refresh:
                item.setChecked(!item.isChecked());
                refreshLayout.setAutoRefresh(item.isChecked());
                return false;
            case R.id.action_auto_scroll_after_loadmore:
                item.setChecked(!item.isChecked());
                refreshLayout.setAutoScrollAfterLoadMore(item.isChecked());
                return false;
            case R.id.action_loadmore_skip_num:
                item.setChecked(!item.isChecked());
                refreshLayout.setLoadMoreSkipNum(item.isChecked() == true ? 3 : 0);
                return false;
        }

        return super.onOptionsItemSelected(item);
    }
}
