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

package com.baitouwei.swiperefresh.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.baitouwei.swiperefresh.R;


/**
 * @author baitouwei
 */
public class DefaultSwipeView extends SwipeView {
    private TextView textView;

    public DefaultSwipeView(Context context) {
        super(context);
    }

    public DefaultSwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View buildContentView() {
        View v = inflate(getContext(), R.layout.layout_default_swipe, this);
        textView = (TextView) v.findViewById(R.id.content_txt);
        return v;
    }

    @Override
    public void updatePercent(float percent) {
        super.updatePercent(percent);
        updateView();
    }

    @Override
    public void updateStatus(SwipeStatus swipeStatus) {
        super.updateStatus(swipeStatus);
        updateView();
    }

    private void updateView() {
        String s = "";
        switch (getSwipeStatus()) {
            case NORMAL:
                s = "swipe to refresh";
                break;
            case READY:
                s = "ready for refresh";
                break;
            case REFRESHING:
                s = "refreshing";
                break;
            case SUCCESS:
                s = "refresh success";
                break;
            case FAIL:
                s = "refresh fail";
                break;
        }
        textView.setText(s);
    }

    @Override
    public void start() {
        textView.setText("refreshing");
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
