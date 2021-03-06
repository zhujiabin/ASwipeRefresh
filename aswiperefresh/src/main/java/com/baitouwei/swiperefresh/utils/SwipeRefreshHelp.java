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

package com.baitouwei.swiperefresh.utils;


import com.baitouwei.swiperefresh.ASwipeRefreshLayout;
import com.baitouwei.swiperefresh.internal.LayoutDirection;
import com.baitouwei.swiperefresh.internal.LayoutLayer;
import com.baitouwei.swiperefresh.internal.SwipeView;

/**
 * @author baitouwei
 */
public class SwipeRefreshHelp {
    /**
     * drawer refresh(default refresh)
     *
     * @param layout
     * @return
     */
    public static ASwipeRefreshLayout buildDrawerSwipeRefreshLayout(ASwipeRefreshLayout layout) {
        final SwipeView headerView = layout.getHeaderView();
        final SwipeView footerView = layout.getFooterView();
        headerView.setParallaxFactor(0)
                .setLayoutLayer(LayoutLayer.BOTTOM)
                .setSwipeLayoutDirection(LayoutDirection.CENTER);

        footerView.setParallaxFactor(0)
                .setLayoutLayer(LayoutLayer.BOTTOM)
                .setSwipeLayoutDirection(LayoutDirection.CENTER);

        layout.setDampAbleOfSwipeDown(true)
                .setDampAbleOfSwipeUp(true)
                .setParallaxFactorOfContentSwipeDown(1)
                .setParallaxFactorOfContentSwipeUp(1);

        layout.setSwipeLayoutConfigListener(new ASwipeRefreshLayout.SwipeLayoutConfigListener() {

            @Override
            public void setUpOffset(ASwipeRefreshLayout layout) {
                final SwipeView headerView = layout.getHeaderView();
                final SwipeView footerView = layout.getFooterView();

                int heightOfHeader = headerView.getMeasuredHeight();
                int heightOfFooter = footerView.getMeasuredHeight();

                headerView.setOffsetRange(heightOfHeader)
                        .setStartOffset(0)
                        .setEndOffset(heightOfHeader);

                footerView.setOffsetRange(-heightOfFooter)
                        .setStartOffset(0)
                        .setEndOffset(-heightOfFooter);

                if (layout.isDampAbleOfSwipeDown()) {
                    //damp extraMove is 1/4,cut off it
                    layout.setContentViewSwipeDownOffsetRange((Math.round(heightOfHeader) * 3) / 4);
                } else {
                    layout.setContentViewSwipeDownOffsetRange(heightOfHeader);
                }
                layout.setContentViewSwipeDownStartOffset(0);
                layout.setContentViewSwipeDownEndOffset(layout.getContentViewSwipeDownOffsetRange());
                if (layout.isDampAbleOfSwipeUp()) {
                    //damp extraMove is 1/4,cut off it
                    layout.setContentViewSwipeUpOffsetRange(-(Math.round(heightOfFooter) * 3) / 4);
                } else {
                    layout.setContentViewSwipeUpOffsetRange(-heightOfFooter);
                }
                layout.setContentViewSwipeUpStartOffset(0);
                layout.setContentViewSwipeUpEndOffset(layout.getContentViewSwipeUpOffsetRange());

            }

            @Override
            public void onLayout(ASwipeRefreshLayout layout, int left, int top, int right, int bottom) {
            }

            @Override
            public boolean isReadyToRefresh(ASwipeRefreshLayout layout, float dragOffset) {
                return dragOffset >= (layout.getMeasuredHeight() * 1 / 3);
            }

            @Override
            public boolean isReadyToLoadMore(ASwipeRefreshLayout layout, float dragOffset) {
                return dragOffset <= -(layout.getMeasuredHeight() * 1 / 3);
            }
        });

        return layout;
    }
}
