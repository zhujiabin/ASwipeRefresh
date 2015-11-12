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

package com.baitouwei.aswiperefresh.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.baiouwei.example.R;
import com.baitouwei.aswiperefresh.sample.ui.SwipeRefreshDemoActivity;
import com.baitouwei.aswiperefresh.sample.ui.SwipeRefreshListFragment;
import com.baitouwei.aswiperefresh.sample.ui.SwipeRefreshRecycleViewFragment;
import com.baitouwei.aswiperefresh.sample.ui.SwipeRefreshScrollViewFragment;
import com.baitouwei.aswiperefresh.sample.ui.SwipeRefreshViewPagerFragment;
import com.baitouwei.aswiperefresh.sample.ui.SwipeRefreshWebViewFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler(Looper.getMainLooper());

    private ArrayAdapter adapter;
    Class[] classes = new Class[]{SwipeRefreshListFragment.class,
            SwipeRefreshRecycleViewFragment.class, SwipeRefreshScrollViewFragment.class,
            SwipeRefreshWebViewFragment.class, SwipeRefreshViewPagerFragment.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        final List<String> stringList = new ArrayList<>();
        for (int i = 0; i < classes.length; i++) {
            stringList.add(classes[i].getSimpleName());
        }
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, stringList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, SwipeRefreshDemoActivity.class);
                intent.putExtra(SwipeRefreshDemoActivity.PARAM_DEMO_CLASS, classes[position]);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
