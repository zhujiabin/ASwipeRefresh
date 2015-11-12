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

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.baiouwei.example.R;
import com.baitouwei.swiperefresh.internal.SwipeStatus;
import com.baitouwei.swiperefresh.internal.SwipeView;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * @author baitouwei
 */
public class DebugSwipeView extends SwipeView {
    private TextView contentView;
    private TextView percentView;
    private ValueAnimator animator;

    public DebugSwipeView(final Context context) {
        super(context);
        animator = ValueAnimator.ofInt(Color.RED, Color.GREEN, Color.BLUE)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (contentView != null) {
                    contentView.setTextColor((int) animation.getAnimatedValue());
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                contentView.setTextColor(getResources().getColor(android.R.color.black));
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected View buildContentView() {
        View v = inflate(getContext(), R.layout.layout_debug_swipe_view, this);
        contentView = (TextView) v.findViewById(R.id.content_txt);
        percentView = (TextView) v.findViewById(R.id.percent_txt);
        return v;
    }

    @Override
    public void updatePercent(float percent) {
        super.updatePercent(percent);
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(4);
        nf.setMinimumFractionDigits(4);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setGroupingUsed(false);
        percentView.setText("Percent:" + nf.format(percent).toLowerCase());
    }

    @Override
    public void updateStatus(SwipeStatus swipeStatus) {
        super.updateStatus(swipeStatus);
        String s = "";
        switch (swipeStatus) {
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
        contentView.setText(s);
    }

    @Override
    public void start() {
        animator.start();
    }

    @Override
    public void stop() {
        animator.cancel();
    }

    @Override
    public boolean isRunning() {
        return animator.isRunning();
    }
}
