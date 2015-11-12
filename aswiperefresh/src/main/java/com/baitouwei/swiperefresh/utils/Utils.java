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

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.AbsListView;

import java.lang.reflect.Field;

/**
 * @author baitowwei
 */
public class Utils {
    public static final int INVALID_DIMENSION = -1;
    public static final int INVALID_POSITIONS = -1;
    public static final int INVALID_COUNT = -1;

    public static final boolean isDimensionInValid(Number dimension) {
        return dimension.doubleValue() <= INVALID_DIMENSION;
    }

    public static final boolean isDimensionInValid(Number... dimensions) {
        for (Number n : dimensions) {
            if (n.doubleValue() <= INVALID_DIMENSION) {
                return true;
            }
        }
        return false;
    }

    /**
     * find last visible item positions,only work if contentView is AbsListView or RecyclerView
     *
     * @param contentView
     * @return last visible item positions or {@link #INVALID_POSITIONS }
     */
    public static final int findLastVisibleItemPositions(View contentView) {
        int lastVisiblePos = INVALID_POSITIONS;

        if (contentView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) contentView;
            lastVisiblePos = absListView.getLastVisiblePosition();
        } else if (contentView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) contentView;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                    lastVisiblePos = linearLayoutManager.findLastVisibleItemPosition();
                }
            } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (staggeredGridLayoutManager.getOrientation() == StaggeredGridLayoutManager.VERTICAL) {
                    int[] lastVisiblePosArray = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
                    int spanNum = staggeredGridLayoutManager.getSpanCount();
                    lastVisiblePos = lastVisiblePosArray[lastVisiblePosArray.length - 1];
                }
            }
        }
        return lastVisiblePos;
    }

    /**
     * get item count,only work if contentView is AbsListView or RecyclerView
     *
     * @param contentView
     * @return item count or {@link #INVALID_COUNT }
     */
    public static final int getItemCount(View contentView) {
        int itemCount = INVALID_COUNT;

        if (contentView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) contentView;
            itemCount = absListView.getAdapter().getCount();
        } else if (contentView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) contentView;
            itemCount = recyclerView.getAdapter().getItemCount();
        }
        return itemCount;
    }

    /**
     * @param contentView    only work if contentView is AbsListView or RecyclerView
     * @param lastVisiblePos item position
     * @return true:scroll success;false:scroll fail
     */
    public static final boolean scrollToItem(View contentView, final int lastVisiblePos) {
        boolean result = false;
        int itemCount = INVALID_COUNT;

        if (lastVisiblePos != INVALID_POSITIONS) {
            if (contentView instanceof AbsListView) {
                AbsListView absListView = (AbsListView) contentView;
                itemCount = absListView.getAdapter().getCount();
                if (itemCount != INVALID_COUNT && itemCount > lastVisiblePos + 1) {
                    absListView.smoothScrollToPosition(lastVisiblePos + 1);
                    result = true;
                }
            } else if (contentView instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) contentView;
                itemCount = recyclerView.getAdapter().getItemCount();
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                        if (itemCount > lastVisiblePos + 1) {
                            linearLayoutManager.scrollToPosition(lastVisiblePos + 1);
                            result = true;
                        }
                    }
                } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                    if (staggeredGridLayoutManager.getOrientation() == StaggeredGridLayoutManager.VERTICAL) {
                        if (itemCount > lastVisiblePos + 1) {
                            //FixMe sometime dislocation
                            staggeredGridLayoutManager.scrollToPosition(lastVisiblePos + 1);
                            result = true;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * @param absListView
     * @return
     */
    @Nullable
    public static AbsListView.OnScrollListener getScrollListener(AbsListView absListView) {
        AbsListView.OnScrollListener onScrollListener = null;
        Class cls = AbsListView.class;
        try {
            Field field = cls.getDeclaredField("mOnScrollListener");
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            onScrollListener = ((AbsListView.OnScrollListener) field.get(absListView));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return onScrollListener;
    }
}
