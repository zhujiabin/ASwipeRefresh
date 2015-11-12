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

package com.baitouwei.aswiperefresh.sample.ui.custom;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.baiouwei.example.R;
import com.baitouwei.swiperefresh.internal.SwipeStatus;
import com.baitouwei.swiperefresh.internal.SwipeView;

/**
 * @author baitouwei
 */
public class RightInSwipeView extends SwipeView {

    private TextView textView;

    public RightInSwipeView(Context context) {
        super(context);
    }

    @Override
    public void updateStatus(SwipeStatus swipeStatus) {
        super.updateStatus(swipeStatus);
        switch (swipeStatus) {
            case NORMAL:
                textView.setText(getContext().getResources().getString(R.string.status_swipe_refresh));
                break;
            case READY:
                textView.setText(getContext().getResources().getString(R.string.status_release_refresh));
                break;
            case REFRESHING:
                textView.setText(getContext().getResources().getString(R.string.status_loading));
                break;
            case SUCCESS:
                textView.setText(getContext().getResources().getString(R.string.status_loading));
                break;
            case FAIL:
                textView.setText(getContext().getResources().getString(R.string.status_loading));
                break;
        }
    }

    @Override
    public void updatePercent(float percent) {
        super.updatePercent(percent);
        setAlpha(percent);
    }

    @Override
    protected View buildContentView() {
        View v = inflate(getContext(), R.layout.layout_right_in_swipe, this);
        textView = (TextView) v.findViewById(R.id.text);
        return v;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }


    @Override
    public boolean isRunning() {
        return false;
    }
}
