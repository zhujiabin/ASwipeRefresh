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
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


/**
 * @author baitouwei
 */
public abstract class SwipeView extends FrameLayout {
    private Config config = new Config();

    public SwipeView(Context context) {
        super(context);
        initView();
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

    }

    public SwipeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        buildContentView();
        if (getChildCount() > 0) {
            ViewGroup.LayoutParams params = getChildAt(0).getLayoutParams();
            setLayoutParams(new LayoutParams(params.width, params.height));
        }
    }

    /**
     * build content view,child class need to implement.
     * recommend to use {@link #inflate(Context, int, ViewGroup)} to inflate view,and viewGroup can not null
     *
     * @return
     */
    protected abstract View buildContentView();

    /**
     * {@link Config#percent}
     *
     * @return
     */
    public float getPercent() {
        return config.percent;
    }

    /**
     * {@link Config#percent}
     */
    public void updatePercent(float percent) {
        if (percent == config.percent) {
            return;
        }
        config.percent = percent;
    }

    /**
     * {@link Config#swipeStatus}
     */
    public void updateStatus(SwipeStatus swipeStatus) {
        config.swipeStatus = swipeStatus;
        switch (swipeStatus) {
            case REFRESHING:
                break;
            case SUCCESS:
            case FAIL:
                if (isRunning()) {
                    stop();
                }
                break;
        }
    }

    /**
     * {@link Config#swipeStatus}
     *
     * @return
     */
    public SwipeStatus getSwipeStatus() {
        return config.swipeStatus;
    }

    /**
     * start refresh animate
     */
    public abstract void start();

    /**
     * stop refresh animate
     */
    public abstract void stop();

    /**
     * is animating
     *
     * @return true:animating;false:not animating
     */
    public abstract boolean isRunning();

    public void setConfig(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    /**
     * {@link Config#startOffset}
     *
     * @return
     */
    public int getStartOffset() {
        return config.startOffset;
    }

    /**
     * {@link Config#startOffset}
     *
     * @return
     */
    public SwipeView setStartOffset(int startOffset) {
        config.startOffset = startOffset;
        return this;
    }

    /**
     * {@link Config#endOffset}
     *
     * @return
     */
    public int getEndOffset() {
        return config.endOffset;
    }

    /**
     * {@link Config#endOffset}
     *
     * @return
     */
    public SwipeView setEndOffset(int endOffset) {
        config.endOffset = endOffset;
        return this;
    }

    /**
     * {@link Config#offsetRange}
     *
     * @return
     */
    public int getOffsetRange() {
        return config.offsetRange;
    }

    /**
     * {@link Config#offsetRange}
     *
     * @return
     */
    public SwipeView setOffsetRange(int offsetRange) {
        config.offsetRange = offsetRange;
        return this;
    }

    /**
     * {@link Config#currentOffset}
     *
     * @return
     */
    public int getCurrentOffset() {
        return config.currentOffset;
    }

    /**
     * {@link Config#currentOffset}
     *
     * @return
     */
    public SwipeView setCurrentOffset(int offset) {
        if (config.offsetRange > 0) {
            offset = (config.currentOffset + offset) > config.offsetRange ? config.endOffset - config.currentOffset : offset;
        } else {
            offset = (config.currentOffset + offset) < config.offsetRange ? config.endOffset - config.currentOffset : offset;
        }
        config.currentOffset += offset;
        if (config.offsetOrientation == OffsetOrientation.VERTICAL) {
            ViewCompat.offsetTopAndBottom(this, offset);
        } else {
            ViewCompat.offsetLeftAndRight(this, offset);
        }
        return this;
    }

    /**
     * {@link Config#parallaxFactor}
     *
     * @return
     */
    public float getParallaxFactor() {
        return config.parallaxFactor;
    }

    /**
     * {@link Config#parallaxFactor}
     *
     * @return
     */
    public SwipeView setParallaxFactor(float parallaxFactor) {
        config.parallaxFactor = parallaxFactor;
        return this;
    }

    /**
     * {@link Config#layoutDirection}
     *
     * @return
     */
    public LayoutDirection getSwipeLayoutDirection() {
        return config.layoutDirection;
    }

    /**
     * {@link Config#layoutDirection}
     *
     * @return
     */
    public SwipeView setSwipeLayoutDirection(LayoutDirection layoutDirection) {
        config.layoutDirection = layoutDirection;
        return this;
    }

    /**
     * {@link Config#layoutLayer}
     *
     * @return
     */
    public LayoutLayer getLayoutLayer() {
        return config.layoutLayer;
    }

    /**
     * {@link Config#layoutLayer}
     *
     * @return
     */
    public SwipeView setLayoutLayer(LayoutLayer layoutLayer) {
        config.layoutLayer = layoutLayer;
        return this;
    }

    /**
     * {@link Config#offsetOrientation}
     *
     * @return
     */
    public OffsetOrientation getOffsetOrientation() {
        return config.offsetOrientation;
    }

    /**
     * {@link Config#offsetOrientation}
     *
     * @return
     */
    public SwipeView setOffsetOrientation(OffsetOrientation offsetOrientation) {
        config.offsetOrientation = offsetOrientation;
        return this;
    }

    /**
     * common config param for {@link SwipeView}
     */
    public class Config implements Cloneable {
        /**
         * current offset of view,it can be positive or negative.
         * {@code currentOffset >= startOffset} and {@code(currentOffset  - startOffset) <= offsetRange}
         */
        public int currentOffset;

        /**
         * initialize offset of view,it can be positive or negative.
         * It mean the offset of view when drag have not been happen
         */
        public int startOffset;

        /**
         * end offset of view,it can be positive or negative.
         * It mean the offset of view when view is refreshing
         * {@code (endOffset - startOffset) <= offsetRange}
         */
        public int endOffset;

        /**
         * offset range of view,it must be positive.
         */
        public int offsetRange;

        /**
         * parallax Factor(Range:[0,1]), {@code realOffset = dragOffset * parallaxFactor},
         * you can set it 0 to make the view not offset when drag or set it 1 to make the view offset when drag.
         */
        public float parallaxFactor;

        /**
         * {@link LayoutDirection}
         */
        public LayoutDirection layoutDirection = LayoutDirection.CENTER;

        /**
         * {@link LayoutLayer}
         */
        public LayoutLayer layoutLayer = LayoutLayer.BOTTOM;

        /**
         * {@link OffsetOrientation}
         */
        public OffsetOrientation offsetOrientation = OffsetOrientation.VERTICAL;

        /**
         * {@link SwipeStatus}
         */
        public SwipeStatus swipeStatus = SwipeStatus.NORMAL;

        /**
         * percent of swipe.if not damped,range is [0,1];if damped,range is [0,+∞]
         */
        public float percent = 0;

        @Override
        public Config clone() throws CloneNotSupportedException {
            return (Config) super.clone();
        }
    }
}
