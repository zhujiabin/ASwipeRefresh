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

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.baitouwei.swiperefresh.ASwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bai on 15/10/19.
 */
public class BaseFragment extends Fragment {
    protected ASwipeRefreshLayout swipeRefreshLayout = null;
    protected int DURATION = 4000;
    protected Handler handler;
    protected List<String> data = new ArrayList<>();
    protected int dataNum = 18;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i <= dataNum; i++) {
            data.add(String.valueOf(i));
        }
        handler = new Handler(Looper.getMainLooper());
        getActivity().setTitle(this.getClass().getSimpleName());
    }

    public ASwipeRefreshLayout getSwipeRefreshLayout() {
        return isAdded() ? swipeRefreshLayout : null;
    }

    protected void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
