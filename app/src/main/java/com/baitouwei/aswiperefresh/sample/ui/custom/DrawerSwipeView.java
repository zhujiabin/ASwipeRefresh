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
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baiouwei.example.R;
import com.baitouwei.swiperefresh.internal.SwipeStatus;
import com.baitouwei.swiperefresh.internal.SwipeView;

/**
 * @author baitouwei
 */
public class DrawerSwipeView extends SwipeView {
    private final static int DURATION = 2000;

    private TextView leftIcon, rightIcon, contentView;
    private int heightOfLeftIcon, heightOfRightIcon, bottomOfContentView;

    private int offsetRange;
    private ValueAnimator waveAnimator;
    private WaveInterpolator waveInterpolator = new WaveInterpolator();

    public DrawerSwipeView(Context context) {
        super(context);
    }

    public DrawerSwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        heightOfLeftIcon = leftIcon.getHeight();
        heightOfRightIcon = rightIcon.getHeight();
        bottomOfContentView = contentView.getBottom();
        offsetRange = ((RelativeLayout.LayoutParams) leftIcon.getLayoutParams()).topMargin;
    }

    @Override
    protected View buildContentView() {
        View v = inflate(getContext(), R.layout.layout_drawer_swipe_view, this);
        contentView = (TextView) v.findViewById(R.id.content_txt);
        leftIcon = (TextView) v.findViewById(R.id.left_icon);
        rightIcon = (TextView) v.findViewById(R.id.right_icon);

        waveAnimator = ValueAnimator.ofFloat(0, 2);
        waveAnimator.setRepeatMode(ValueAnimator.RESTART);
        waveAnimator.setDuration(DURATION);
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.setInterpolator(new LinearInterpolator());
        waveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float factor = waveInterpolator.getInterpolation((Float) animation.getAnimatedValue());
                leftIcon.setTranslationY(offsetRange * factor);
                rightIcon.setTranslationY(offsetRange * -factor);
            }
        });
        waveAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return v;
    }

    @Override
    public void updatePercent(float percent) {
        super.updatePercent(percent);
        setAlpha(percent);
        percent = Math.min(percent, 1);
        leftIcon.setTranslationY(heightOfLeftIcon * (1 - percent));
        rightIcon.setTranslationY(heightOfRightIcon * (1 - percent));
        contentView.setTranslationY(-bottomOfContentView * (1 - percent));
    }

    @Override
    public void updateStatus(SwipeStatus swipeStatus) {
        super.updateStatus(swipeStatus);
        switch (swipeStatus) {
            case NORMAL:
                contentView.setText(getContext().getResources().getString(R.string.status_swipe_refresh));
                break;
            case READY:
                contentView.setText(getContext().getResources().getString(R.string.status_release_refresh));
                break;
            case REFRESHING:
                contentView.setText(getContext().getResources().getString(R.string.status_loading));
                break;
            case SUCCESS:
                contentView.setText(getContext().getResources().getString(R.string.status_loading));
                break;
            case FAIL:
                contentView.setText(getContext().getResources().getString(R.string.status_loading));
                break;
        }
    }

    @Override
    public void start() {
        waveAnimator.start();
    }

    @Override
    public void stop() {
        waveAnimator.cancel();
    }

    @Override
    public boolean isRunning() {
        return waveAnimator.isRunning();
    }
}
