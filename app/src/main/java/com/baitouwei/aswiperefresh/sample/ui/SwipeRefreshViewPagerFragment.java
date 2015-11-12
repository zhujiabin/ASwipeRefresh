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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baiouwei.example.R;
import com.baitouwei.aswiperefresh.sample.BaseFragment;


/**
 * @author baitouwei
 */
public class SwipeRefreshViewPagerFragment extends BaseFragment {
    private static final String TAG = SwipeRefreshViewPagerFragment.class.getSimpleName();

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.swipe_view_pager_fragment, container, false);

        viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void findSwipeRefreshLayout() {
        Fragment f = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + viewPager.getCurrentItem());
        if (f != null && f instanceof BaseFragment) {
            swipeRefreshLayout = ((BaseFragment) f).getSwipeRefreshLayout();
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            switch (position) {
                case 0:
                    f = new SwipeRefreshListFragment();
                    break;
                case 1:
                    f = new SwipeRefreshRecycleViewFragment();
                    break;
                case 2:
                    f = new SwipeRefreshScrollViewFragment();
                    break;
                case 3:
                    f = new SwipeRefreshWebViewFragment();
                    break;
                default:
                    f = new SwipeRefreshListFragment();
                    break;
            }
            return f;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getItem(position).getClass().getSimpleName();
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }
    }
}
