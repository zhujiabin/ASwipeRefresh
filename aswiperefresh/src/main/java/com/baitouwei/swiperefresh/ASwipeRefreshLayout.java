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

package com.baitouwei.swiperefresh;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.baitouwei.swiperefresh.internal.DefaultSwipeView;
import com.baitouwei.swiperefresh.internal.LayoutDirection;
import com.baitouwei.swiperefresh.internal.LayoutLayer;
import com.baitouwei.swiperefresh.internal.OffsetOrientation;
import com.baitouwei.swiperefresh.internal.SwipeStatus;
import com.baitouwei.swiperefresh.internal.SwipeView;
import com.baitouwei.swiperefresh.utils.SwipeRefreshHelp;
import com.baitouwei.swiperefresh.utils.Utils;
import com.baitouwei.swiperefresh.utils.ValueAnimatorCompat;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ASwipeRefreshLayout(Android SwipeRefreshLayout),
 * A simple and flexible layout support swipe down refresh and swipe up load more.
 * Features:
 * <ul>
 * <li>1.Easy to custom header or footer</li>
 * <li>2.Easy to custom the way header or footer appear</li>
 * <li>3.Support auto refresh and auto load more(auto load more only work for AbsListView or RecyclerView)</li>
 * </ul>
 *
 * @author baitouwei
 */
public class ASwipeRefreshLayout extends FrameLayout {
    private static final String TAG = ASwipeRefreshLayout.class.getSimpleName();

    ///////////////////////////////////////////////////////////////////////////
    // params can configurate by user
    ///////////////////////////////////////////////////////////////////////////
    /**
     * interpolator user for header animate
     */
    private Interpolator headerInterpolator;

    /**
     * interpolator user for footer animate
     */
    private Interpolator footerInterpolator;

    /**
     * {@link ASwipeRefreshLayout}
     */
    private SwipeLayoutConfigListener swipeLayoutConfigListener;

    /**
     * damp rate for swipe down,
     * range:[0,1],The number more smaller,the drag more different
     */
    private static final float swipeDownDampRate = .5f;

    /**
     * damp rate for swipe up,
     * range:[0,1],The number more smaller,the drag more different
     */
    private static final float swipeUpDampRate = .5f;

    /**
     * if true,Content will auto scroll to {@link #lastVisiblePos},only work if content is AbsListView or RecyclerView
     */
    private boolean autoScrollAfterLoadMore = true;

    //swipe down
    /**
     * {@link SwipeView.Config#offsetRange} for swipe down
     */
    private int contentViewSwipeDownOffsetRange;

    /**
     * {@link SwipeView.Config#startOffset} for swipe down
     */
    private int contentViewSwipeDownStartOffset;

    /**
     * {@link SwipeView.Config#endOffset} for swipe down
     */
    private int contentViewSwipeDownEndOffset;

    /**
     * {@link SwipeView.Config#parallaxFactor} for swipe down
     */
    private float parallaxFactorOfContentSwipeDown = 1f;

    /**
     * is dampable for swipe down
     */
    private boolean dampAbleOfSwipeDown = false;

    private SwipeView headerView;

    /**
     * swipe down refresh listener.If null,the layout can not perform swipe down refresh
     */
    private OnSwipeRefreshListener swipeDownRefreshListener;

    /**
     * whether auto perform swipe down refresh the layout show
     */
    private boolean isAutoSwipeDownRefresh = false;

    //swipe up
    /**
     * {@link SwipeView.Config#offsetRange} for swipe up
     */
    private int contentViewSwipeUpOffsetRange;

    /**
     * {@link SwipeView.Config#startOffset} for swipe up
     */
    private int contentViewSwipeUpStartOffset;

    /**
     * {@link SwipeView.Config#endOffset} for swipe up
     */
    private int contentViewSwipeUpEndOffset;

    /**
     * {@link SwipeView.Config#parallaxFactor} for swipe up
     */
    private float parallaxFactorOfContentSwipeUp = 1;

    /**
     * is dampable for swipe up
     */
    private boolean dampAbleOfSwipeUp = false;

    private SwipeView footerView;

    /**
     * swipe up refresh listener.If null,the layout can not perform swipe up refresh
     */
    private OnSwipeRefreshListener swipeUpRefreshListener;

    /**
     * whether auto perform swipe up refresh the layout show
     */
    private boolean isAutoLoadMore = false;

    /**
     * whether can perform swipe up refresh the layout show.If false it can't swipe up.
     * eg:You can set if true after load more from api and find date have not more
     */
    private boolean canLoadMore = true;

    /**
     * skip {@link #loadMoreSkipNum} item to perform swipe up refresh
     * only work if {@link #contentView} is {@link AbsListView} or {@link RecyclerView}
     */
    private int loadMoreSkipNum = 0;

    /**
     * only use when content view is AbsListView
     */
    private AbsListView.OnScrollListener onScrollListener;

    ///////////////////////////////////////////////////////////////////////////
    // params user inner
    ///////////////////////////////////////////////////////////////////////////
    private static final int INVALID_POINTER = -1;

    private boolean isDealAutoLoadMoreListenerDone = false;

    /**
     * true:current is swipe down;false:current is swipe up
     */
    private boolean isSwipeDown = true;

    private boolean isDragging = false;

    /**
     * the last visible position when begin load more,default is {@linkplain Utils#INVALID_POSITIONS}
     */
    private int lastVisiblePos = Utils.INVALID_POSITIONS;

    /**
     * {@link SwipeView.Config#currentOffset}
     */
    private int currentContentOffset;

    private View contentView;

    private ValueAnimator animatorOfContent;

    /**
     * drag offset percent,equal to dragOffset/{@link #contentViewSwipeDownOffsetRange} or dragOffset/{@link #contentViewSwipeUpOffsetRange}
     */
    private float dragPercent = 0;

    /**
     *
     */
    private float offsetPercent = 0;

    private int touchSlop;
    private int activePointerId = INVALID_POINTER;
    private float initialMotionY;

    //Header
    /**
     * whether have been swiped down refresh
     */
    private boolean autoRefreshed = false;

    private long durationOfSwipeDown = 1400;
    private ValueAnimator animatorOfHeader;

    //Footer
    private long durationOfSwipeUp = 1400;
    private ValueAnimator animatorOfFooter;

    public ASwipeRefreshLayout(Context context) {
        super(context);
        init();
    }

    public ASwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ASwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (headerInterpolator == null) {
            headerInterpolator = new DecelerateInterpolator();
        }
        if (footerInterpolator == null) {
            footerInterpolator = new DecelerateInterpolator();
        }
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        if (headerView == null || footerView == null) {
            headerView = new DefaultSwipeView(getContext());
            footerView = new DefaultSwipeView(getContext());
            SwipeRefreshHelp.buildDrawerSwipeRefreshLayout(this);
        }

        addView(headerView);
        addView(footerView);
        headerView.setVisibility(INVISIBLE);
        footerView.setVisibility(INVISIBLE);

        setWillNotDraw(true);
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        pauseAnimate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (ensureTarget()) {
            int pWidth = getMeasuredWidth();
            int pHeight = getMeasuredHeight();
            int pPaddingL = getPaddingLeft();
            int pPaddingT = getPaddingTop();
            int pPaddingR = getPaddingRight();
            int pPaddingB = getPaddingBottom();

            int l, t, r, b;
            //header
            LayoutDirection layoutModeOfHeader = headerView.getSwipeLayoutDirection();
            MarginLayoutParams headerParams = (MarginLayoutParams) headerView.getLayoutParams();
            //default LayoutDirection is CENTER
            l = pPaddingL + headerParams.leftMargin;
            t = pPaddingT + headerParams.topMargin;
            r = l + headerView.getMeasuredWidth() - pPaddingR - headerParams.rightMargin;
            b = t + headerView.getMeasuredHeight() - pPaddingB - headerParams.bottomMargin;
            if (headerView.getOffsetOrientation() == OffsetOrientation.VERTICAL) {
                t += headerView.getCurrentOffset();
                b += headerView.getCurrentOffset();
            } else {
                l += headerView.getCurrentOffset();
                r += headerView.getCurrentOffset();
            }
            if (layoutModeOfHeader == LayoutDirection.CENTER_OFFSET) {
                t = t - headerView.getMeasuredHeight();
                b = b - headerView.getMeasuredHeight();
            } else if (layoutModeOfHeader == LayoutDirection.LEFT) {
                l = l - headerView.getMeasuredWidth();
                r = r - headerView.getMeasuredWidth();
            } else if (layoutModeOfHeader == LayoutDirection.RIGHT) {
                l = l + pWidth;
                r = r + pWidth;
            }
            headerView.layout(l, t, r, b);
            //if view is out of content,we need to force draw it
            headerView.postInvalidate();

            //footer
            LayoutDirection layoutModeOfFooter = footerView.getSwipeLayoutDirection();
            MarginLayoutParams footerParams = (MarginLayoutParams) footerView.getLayoutParams();
            //default LayoutDirection is CENTER
            l = pPaddingL + footerParams.leftMargin;
            t = pHeight - footerView.getMeasuredHeight() + footerParams.topMargin;
            r = l + footerView.getMeasuredWidth() - pPaddingR - footerParams.rightMargin;
            b = t + footerView.getMeasuredHeight() - pPaddingB - footerParams.bottomMargin;
            if (footerView.getOffsetOrientation() == OffsetOrientation.VERTICAL) {
                t += footerView.getCurrentOffset();
                b += footerView.getCurrentOffset();
            } else {
                l += footerView.getCurrentOffset();
                r += footerView.getCurrentOffset();
            }
            if (layoutModeOfFooter == LayoutDirection.CENTER_OFFSET) {
                t = t + footerView.getMeasuredHeight();
                b = b + footerView.getMeasuredHeight();
            } else if (layoutModeOfFooter == LayoutDirection.LEFT) {
                l = l - footerView.getMeasuredWidth();
                r = r - footerView.getMeasuredWidth();
            } else if (layoutModeOfFooter == LayoutDirection.RIGHT) {
                l = l + pWidth;
                r = r + pWidth;
            }
            //if view is out of content,we need to force draw it
            footerView.layout(l, t, r, b);
            footerView.postInvalidate();

            int currentContentOffset = getCurrentContentOffset();
            contentView.layout(pPaddingL, pPaddingT + currentContentOffset, pPaddingL + contentView.getMeasuredWidth() - pPaddingR, pPaddingT + currentContentOffset + pHeight - pPaddingB);

            dealAutoRefresh();
            if (swipeLayoutConfigListener != null) {
                swipeLayoutConfigListener.onLayout(this, left, top, right, bottom);
                swipeLayoutConfigListener.setUpOffset(this);
            }
            addAutoLoadMoreListener();
            resumeAnimate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() || (canChildScrollUp() && canChildScrollDown())
                || footerView.getSwipeStatus() == SwipeStatus.REFRESHING || headerView.getSwipeStatus() == SwipeStatus.REFRESHING
                || (swipeDownRefreshListener == null && swipeUpRefreshListener == null)
                || isAnimating()) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isDragging = false;

                //reset
                currentContentOffset = 0;
                headerView.setCurrentOffset(0);
                footerView.setCurrentOffset(0);
                headerView.updateStatus(SwipeStatus.NORMAL);
                footerView.updateStatus(SwipeStatus.NORMAL);

                activePointerId = MotionEventCompat.getPointerId(ev, ev.getActionIndex());
                initialMotionY = getMotionEventY(ev, activePointerId);
                break;
            case MotionEvent.ACTION_MOVE:
                if (activePointerId == INVALID_POINTER) {
                    return false;
                }

                final float diffY = getMotionEventY(ev, activePointerId) - initialMotionY;

                //deal border
                if ((diffY > 0 && !canChildScrollUp() || (diffY < 0 && !canChildScrollDown()))) {
                    if (Math.abs(diffY) > touchSlop && !isDragging) {
                        isDragging = true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isDragging = false;
                activePointerId = INVALID_POINTER;
                break;
        }
        return isDragging;
    }

    /**
     * if we ensure current is swipe down or swipe up
     */
    private boolean isSwipeStatusEnsure = false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isDragging) {
            return super.onTouchEvent(ev);
        }

        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final float y = getMotionEventY(ev, activePointerId);
                final float dragOffset = (int) (y - initialMotionY);

                if (isSwipeStatusEnsure) {
                    if ((dragOffset < 0 && isSwipeDown == true) || (dragOffset > 0 && isSwipeDown == false)) {
                        return false;
                    }
                }
                if (dragOffset > 0) {//SwipeDown,PS:dragPercent and dragOffset is positive
                    if (headerView.getVisibility() != VISIBLE) {
                        headerView.setVisibility(VISIBLE);
                    }

                    isSwipeDown = true;
                    dragPercent = dragOffset / (contentViewSwipeDownOffsetRange);
                    if (dampAbleOfSwipeDown) {
                        int targetY = dampCompute(Math.abs(dragOffset * parallaxFactorOfContentSwipeDown), contentViewSwipeDownOffsetRange, swipeDownDampRate);
                        offsetContent(targetY - currentContentOffset, 1);
                    } else {
                        if (dragOffset < contentViewSwipeDownOffsetRange) {
                            offsetContent((int) dragOffset - currentContentOffset, parallaxFactorOfContentSwipeDown);
                        }
                    }
                    offsetHeader((int) (dragPercent * headerView.getOffsetRange() - headerView.getCurrentOffset()));

                    if (swipeLayoutConfigListener.isReadyToRefresh(this, dragOffset)) {
                        headerView.updateStatus(SwipeStatus.READY);
                    } else {
                        headerView.updateStatus(SwipeStatus.NORMAL);
                    }
                    headerView.updatePercent(dragPercent);
                } else {//SwipeUp,PS:dragPercent and dragOffset is negative
                    if (footerView.getVisibility() != VISIBLE) {
                        footerView.setVisibility(VISIBLE);
                    }

                    isSwipeDown = false;
                    dragPercent = dragOffset / (contentViewSwipeUpOffsetRange);

                    if (dampAbleOfSwipeUp) {
                        int targetY = dampCompute(Math.abs(dragOffset * parallaxFactorOfContentSwipeUp), Math.abs(contentViewSwipeUpOffsetRange), swipeUpDampRate);
                        offsetContent(-targetY - currentContentOffset, 1);
                    } else {
                        if (dragOffset > contentViewSwipeUpOffsetRange) {
                            offsetContent((int) dragOffset - currentContentOffset, parallaxFactorOfContentSwipeUp);
                        }
                    }
                    offsetFooter((int) (dragPercent * footerView.getOffsetRange() - footerView.getCurrentOffset()));

                    if (swipeLayoutConfigListener.isReadyToLoadMore(this, dragOffset)) {
                        footerView.updateStatus(SwipeStatus.READY);
                    } else {
                        footerView.updateStatus(SwipeStatus.NORMAL);
                    }
                    footerView.updatePercent(dragPercent);
                }
                if (isSwipeStatusEnsure == false) {
                    isSwipeStatusEnsure = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isSwipeDown) {
                    if (headerView.getSwipeStatus() == SwipeStatus.READY) {
                        refreshStart();
                    } else {
                        animateContentOffsetToPos(true, true, new OnAnimateContentOffsetListener() {
                            @Override
                            public void onOffsetEnd() {
                                headerView.updateStatus(SwipeStatus.NORMAL);
                            }
                        });
                        animateHeaderOffsetToPos(true, new OnAnimateContentOffsetListener() {
                            @Override
                            public void onOffsetEnd() {
                                headerView.updateStatus(SwipeStatus.NORMAL);
                            }
                        });
                    }
                } else {
                    if (footerView.getSwipeStatus() == SwipeStatus.READY) {
                        loadMoreStart();
                    } else {
                        animateContentOffsetToPos(true, false, new OnAnimateContentOffsetListener() {
                            @Override
                            public void onOffsetEnd() {
                                footerView.updateStatus(SwipeStatus.NORMAL);
                            }
                        });
                        animateFooterOffsetToPos(true, new OnAnimateContentOffsetListener() {
                            @Override
                            public void onOffsetEnd() {
                                footerView.updateStatus(SwipeStatus.NORMAL);
                            }
                        });
                    }
                }

                isDragging = false;
                activePointerId = INVALID_POINTER;
                isSwipeStatusEnsure = false;
                break;
        }

        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        swipeDownRefreshListener = null;
        swipeUpRefreshListener = null;
    }

    /**
     * compute damp
     *
     * @param yDiff    Y axis offset
     * @param MAX      max offset
     * @param dampRate damp rate,range:[0,1],The number more smaller,the drag more different
     * @return target Y axis
     */
    private int dampCompute(float yDiff, int MAX, float dampRate) {
        final float scrollTop = yDiff * dampRate;
        dragPercent = scrollTop / MAX;
        float boundedDragPercent = Math.min(1f, (dragPercent));
        float extraOS = Math.abs(scrollTop) - MAX;
        float slingshotDist = MAX;
        float tensionSlingshotPercent = Math.max(0,
                Math.min(extraOS, slingshotDist * 2) / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent / 2;//result is 1/4 * MAX
        int targetY = (int) ((slingshotDist * boundedDragPercent) + extraMove);
        return targetY;
    }

    public void setLayerOfHeader(LayoutLayer layerOfHeader) {
        headerView.setLayoutLayer(layerOfHeader);
    }

    public void setLayerOfFooter(LayoutLayer layerOfFooter) {
        footerView.setLayoutLayer(layerOfFooter);
    }

    //Animate

    /**
     * animate content view specific location
     *
     * @param isToStart   true:move to the start location,false:move to the end location
     * @param isSwipeDown true:current is swipe down,false:current is swipe up
     * @param listener    {@link OnAnimateContentOffsetListener}
     */
    private void animateContentOffsetToPos(final boolean isToStart, final boolean isSwipeDown, final OnAnimateContentOffsetListener listener) {

        if (animatorOfContent != null && animatorOfContent.isRunning()) {
            animatorOfContent.cancel();
        }

        if (isToStart) {
            if (isSwipeDown) {
                animatorOfContent = ObjectAnimator.ofInt((int) currentContentOffset, contentViewSwipeDownStartOffset)
                        .setDuration(durationOfSwipeDown);
                animatorOfContent.setInterpolator(headerInterpolator);
            } else {
                animatorOfContent = ObjectAnimator.ofInt((int) currentContentOffset, contentViewSwipeUpStartOffset)
                        .setDuration(durationOfSwipeUp);
                animatorOfContent.setInterpolator(footerInterpolator);
            }
        } else {
            if (isSwipeDown) {
                animatorOfContent = ObjectAnimator.ofInt((int) currentContentOffset, contentViewSwipeDownEndOffset)
                        .setDuration(durationOfSwipeDown);
                animatorOfContent.setInterpolator(headerInterpolator);
            } else {
                animatorOfContent = ObjectAnimator.ofInt((int) currentContentOffset, contentViewSwipeUpEndOffset)
                        .setDuration(durationOfSwipeUp);
                animatorOfContent.setInterpolator(footerInterpolator);
            }
        }
        final float currentPercent = headerView.getPercent();
        animatorOfContent.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isSwipeDown) {
                    offsetContent((int) animation.getAnimatedValue() - currentContentOffset, parallaxFactorOfContentSwipeDown);
                } else {
                    offsetContent((int) animation.getAnimatedValue() - currentContentOffset, parallaxFactorOfContentSwipeUp);
                }
            }
        });
        animatorOfContent.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.onOffsetEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorOfContent.start();
    }

    /**
     * animate header view specific location
     *
     * @param isToStart true:move to the start location,false:move to the end location
     * @param listener  {@link OnAnimateContentOffsetListener}
     */
    private void animateHeaderOffsetToPos(final boolean isToStart, final OnAnimateContentOffsetListener listener) {

        if (animatorOfHeader != null && animatorOfHeader.isRunning()) {
            animatorOfHeader.cancel();
        }

        if (headerView.getVisibility() != VISIBLE) {
            headerView.setVisibility(VISIBLE);
        }

        if (isToStart) {
            animatorOfHeader = ObjectAnimator.ofInt(headerView.getCurrentOffset(), headerView.getStartOffset())
                    .setDuration(durationOfSwipeDown);
        } else {
            animatorOfHeader = ObjectAnimator.ofInt(headerView.getCurrentOffset(), headerView.getEndOffset())
                    .setDuration(durationOfSwipeDown);
        }
        final float currentPercent = headerView.getPercent();
        animatorOfHeader.setInterpolator(headerInterpolator);
        animatorOfHeader.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetHeader((int) animation.getAnimatedValue() - headerView.getCurrentOffset());
                computeOffsetPercent(currentPercent, isToStart, animation.getAnimatedFraction());
            }
        });
        animatorOfHeader.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.onOffsetEnd();
                }
                if (isToStart) {
                    headerView.setVisibility(INVISIBLE);
                    footerView.setVisibility(INVISIBLE);
                } else {
                    if (!headerView.isRunning()) {
                        headerView.start();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorOfHeader.start();
    }

    /**
     * animate footer view specific location
     *
     * @param isToStart true:move to the start location,false:move to the end location
     * @param listener  {@link OnAnimateContentOffsetListener}
     */
    private void animateFooterOffsetToPos(final boolean isToStart, final OnAnimateContentOffsetListener listener) {

        if (animatorOfFooter != null && animatorOfFooter.isRunning()) {
            animatorOfFooter.cancel();
        }

        if (footerView.getVisibility() != VISIBLE) {
            footerView.setVisibility(VISIBLE);
        }

        if (isToStart) {
            animatorOfFooter = ObjectAnimator.ofInt(footerView.getCurrentOffset(), footerView.getStartOffset())
                    .setDuration(durationOfSwipeUp);
        } else {
            animatorOfFooter = ObjectAnimator.ofInt(footerView.getCurrentOffset(), footerView.getEndOffset())
                    .setDuration(durationOfSwipeUp);
        }
        animatorOfFooter.setInterpolator(footerInterpolator);
        final float currentPercent = footerView.getPercent();
        animatorOfFooter.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetFooter((int) animation.getAnimatedValue() - footerView.getCurrentOffset());
                computeOffsetPercent(currentPercent, isToStart, animation.getAnimatedFraction());
            }
        });
        animatorOfFooter.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.onOffsetEnd();
                }
                if (isToStart) {
                    headerView.setVisibility(INVISIBLE);
                    footerView.setVisibility(INVISIBLE);
                } else {
                    if (!footerView.isRunning()) {
                        footerView.start();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorOfFooter.start();
    }

    private boolean isAnimating() {
        if ((animatorOfContent != null && animatorOfContent.isRunning())
                || (animatorOfHeader != null && animatorOfHeader.isRunning())
                || (animatorOfFooter != null && animatorOfFooter.isRunning())) {
            return true;
        } else {
            return false;
        }
    }

    private void pauseAnimate() {
        if (animatorOfContent != null) {
            ValueAnimatorCompat.pause(animatorOfContent);
        }
        if (animatorOfHeader != null) {
            ValueAnimatorCompat.pause(animatorOfHeader);
        }
        if (animatorOfFooter != null) {
            ValueAnimatorCompat.pause(animatorOfFooter);
        }
    }

    private void resumeAnimate() {
        if (animatorOfContent != null) {
            ValueAnimatorCompat.resume(animatorOfContent);
        }
        if (animatorOfHeader != null) {
            ValueAnimatorCompat.resume(animatorOfHeader);
        }
        if (animatorOfFooter != null) {
            ValueAnimatorCompat.resume(animatorOfFooter);
        }
    }

    private void offsetContent(int offset, float parallaxFactor) {
        offset *= parallaxFactor;
        ViewCompat.offsetTopAndBottom(contentView, offset);
        currentContentOffset += offset;
    }

    private void offsetHeader(int offset) {
        offset = (int) (offset * headerView.getParallaxFactor());
        headerView.setCurrentOffset(offset);
    }

    private void offsetFooter(int offset) {
        offset = (int) (offset * footerView.getParallaxFactor());
        footerView.setCurrentOffset(offset);
    }

    //Utils
    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (contentView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) contentView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return contentView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(contentView, -1);
        }
    }

    public boolean canChildScrollDown() {
        return ViewCompat.canScrollVertically(contentView, 1);
    }

    private boolean ensureTarget() {
        if (contentView != null) {
            return true;
        }

        if (getChildCount() == 3) {
            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                if (childView instanceof SwipeView || childView instanceof SwipeView) {
                    continue;
                } else {
                    contentView = childView;
                }
            }

            LayoutLayer layerOfHeader = headerView.getLayoutLayer();
            LayoutLayer layerOfFooter = footerView.getLayoutLayer();
            if (contentView != null) {
                if (layerOfHeader == LayoutLayer.ABOVE && layerOfFooter == LayoutLayer.ABOVE) {
                    bringChildToFront(headerView);
                    bringChildToFront(footerView);
                } else if (layerOfHeader == LayoutLayer.BOTTOM && layerOfFooter == LayoutLayer.BOTTOM) {
                    bringChildToFront(contentView);
                } else if (layerOfHeader == LayoutLayer.ABOVE && layerOfFooter == LayoutLayer.BOTTOM) {
                    bringChildToFront(contentView);
                    bringChildToFront(headerView);
                } else if (layerOfHeader == LayoutLayer.BOTTOM && layerOfFooter == LayoutLayer.ABOVE) {
                    bringChildToFront(contentView);
                    bringChildToFront(footerView);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * where we can swipe down refresh
     *
     * @return
     */
    private boolean canSwipeDownRefresh() {
        if (ViewCompat.isAttachedToWindow(this) && (headerView.getSwipeStatus() == SwipeStatus.READY || headerView.getSwipeStatus() == SwipeStatus.NORMAL) && swipeDownRefreshListener != null) {
            if (!canChildScrollDown() && !canChildScrollUp()) {
                return true;
            } else if (!canChildScrollUp()) {
                return true;
            }
        }
        return false;
    }

    /**
     * where we can swipe up refresh
     *
     * @return
     */
    private boolean canSwipeUpRefresh() {
        if (ViewCompat.isAttachedToWindow(this) && (footerView.getSwipeStatus() == SwipeStatus.READY || footerView.getSwipeStatus() == SwipeStatus.NORMAL) && canLoadMore && swipeUpRefreshListener != null) {
            if (!canChildScrollDown() && !canChildScrollUp()) {
                return true;
            } else if (loadMoreSkipNum > 0) {
                if (contentView instanceof AbsListView) {
                    if (((AbsListView) contentView).getLastVisiblePosition() >= ((AbsListView) contentView).getAdapter().getCount() - loadMoreSkipNum - 1) {
                        return true;
                    }
                } else if (contentView instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) contentView;
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        if (((LinearLayoutManager) layoutManager).findLastVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - loadMoreSkipNum - 1) {
                            return true;
                        }
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                        if (staggeredGridLayoutManager.getOrientation() == StaggeredGridLayoutManager.VERTICAL) {
                            int[] lastVisiblePos = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
                            int spanNum = staggeredGridLayoutManager.getSpanCount();
                            if (lastVisiblePos[lastVisiblePos.length - 1] >= recyclerView.getAdapter().getItemCount() - loadMoreSkipNum - 1) {
                                return true;
                            }
                        }
                    }
                }
            } else if (!canChildScrollDown()) {
                return true;
            }
        }
        return false;
    }

    /**
     * compute offset percent when animating
     *
     * @param currentPercent
     * @param isToStart
     * @param animatedFraction
     */
    private void computeOffsetPercent(float currentPercent, boolean isToStart, float animatedFraction) {
        float percent = currentPercent;
        if (isSwipeDown) {
            if (isToStart) {
                percent = new BigDecimal(0)
                        .subtract(new BigDecimal(percent))
                        .multiply(new BigDecimal(animatedFraction))
                        .add(new BigDecimal(percent))
                        .setScale(3, RoundingMode.HALF_UP)
                        .floatValue();
                percent = Math.max(0, percent);
            } else {
                percent = new BigDecimal(1)
                        .subtract(new BigDecimal(percent))
                        .multiply(new BigDecimal(animatedFraction))
                        .add(new BigDecimal(percent))
                        .setScale(3, RoundingMode.HALF_UP)
                        .floatValue();
                percent = Math.min(1, percent);
            }
            if (percent != headerView.getPercent()) {
                headerView.updatePercent(percent);
            }
        } else {
            if (isToStart) {
                percent = new BigDecimal(0)
                        .subtract(new BigDecimal(percent))
                        .multiply(new BigDecimal(animatedFraction))
                        .add(new BigDecimal(percent))
                        .setScale(3, RoundingMode.HALF_UP)
                        .floatValue();
                percent = Math.max(0, percent);
            } else {
                percent = new BigDecimal(1)
                        .subtract(new BigDecimal(dragPercent))
                        .multiply(new BigDecimal(animatedFraction))
                        .add(new BigDecimal(percent))
                        .setScale(3, RoundingMode.HALF_UP)
                        .floatValue();
                percent = Math.min(1, percent);
            }
            footerView.updatePercent(percent);
        }
    }

    //Listener

    /**
     * listener call when animate end
     */
    private interface OnAnimateContentOffsetListener {

        /**
         * call when view offset is finish
         */
        public void onOffsetEnd();
    }

    /**
     * swipe refresh listener
     */
    public interface OnSwipeRefreshListener {

        /**
         * begin refresh
         */
        public void onSwipeRefresh(ASwipeRefreshLayout aSwipeRefreshLayout);


        /**
         * begin refresh success
         */
        public void onSwipeSuccess(ASwipeRefreshLayout aSwipeRefreshLayout);


        /**
         * begin refresh fail
         */
        public void onSwipeFail(ASwipeRefreshLayout aSwipeRefreshLayout);
    }

    /**
     * listener use to config {@link ASwipeRefreshLayout}
     */
    public interface SwipeLayoutConfigListener {

        /**
         * setup header and footer offset config,Such as {@link SwipeView.Config#offsetRange},
         * {@link SwipeView.Config#startOffset},{@link SwipeView.Config#endOffset}.
         * this call every time when {@link ASwipeRefreshLayout} layout,at this point,{@link ASwipeRefreshLayout} and it's child views measure ready
         *
         * @param layout
         */
        public void setUpOffset(ASwipeRefreshLayout layout);

        /**
         * you can custom layout {@link #headerView} or {@link #footerView} or {@link #contentView} hear.
         * In general,you do not need to write this
         *
         * @param layout
         */
        public void onLayout(ASwipeRefreshLayout layout, int left, int top, int right, int bottom);

        /**
         * use for decide when to refresh
         *
         * @param layout
         * @param dragOffset offset of finger swipe,it can be positive or negative
         * @return true:ready for refresh;false:can not refresh
         */
        public boolean isReadyToRefresh(ASwipeRefreshLayout layout, float dragOffset);

        /**
         * use for decide when to loadMore
         *
         * @param layout
         * @param dragOffset offset of finger swipe,it can be positive or negative
         * @return true:ready for loadMore;false:can not loadMore
         */
        public boolean isReadyToLoadMore(ASwipeRefreshLayout layout, float dragOffset);
    }

    private void dealAutoLoadMore() {
        if (!isDragging && isAutoLoadMore && !isRefreshingOrLoadingMore() && canSwipeUpRefresh()) {
            if (!canChildScrollDown()) {
                loadMoreStart(true);
            } else {
                loadMoreStart(false);
            }
        }
    }

    private void addAutoLoadMoreListener() {
        if (!isDealAutoLoadMoreListenerDone) {
            if (contentView instanceof AbsListView) {
                AbsListView absListView = (AbsListView) contentView;
                onScrollListener = Utils.getScrollListener(absListView);
                absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        if (onScrollListener != null) {
                            onScrollListener.onScrollStateChanged(view, scrollState);
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        dealAutoLoadMore();
                        if (onScrollListener != null) {
                            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                        }
                    }
                });
            } else if (contentView instanceof RecyclerView) {
                ((RecyclerView) contentView).addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        dealAutoLoadMore();
                    }
                });
            }
            isDealAutoLoadMoreListenerDone = true;
        }
    }

    /**
     * we use delay to implement auto refresh,because we do not know what time the view is ready.
     * the implement is strange,so you'd better not use auto refresh,
     * just call {@link #refreshStart()} when you need or you can sure the view is ready.
     */
    private void dealAutoRefresh() {
        if (isAutoSwipeDownRefresh && !autoRefreshed) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshStart();
                }
            }, 300);
            autoRefreshed = true;
        }
    }

    private boolean isRefreshingOrLoadingMore() {
        return headerView.getSwipeStatus() == SwipeStatus.REFRESHING || footerView.getSwipeStatus() == SwipeStatus.REFRESHING;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Action method
    ///////////////////////////////////////////////////////////////////////////
    public void refreshStart() {
        if (canSwipeDownRefresh()) {
            swipeDownRefreshListener.onSwipeRefresh(this);
            headerView.updateStatus(SwipeStatus.REFRESHING);
            animateContentOffsetToPos(false, true, null);
            animateHeaderOffsetToPos(false, null);
        }
    }

    public void refreshSuccess() {
        if (headerView.getSwipeStatus() == SwipeStatus.REFRESHING && swipeDownRefreshListener != null) {
            headerView.updateStatus(SwipeStatus.SUCCESS);
            swipeDownRefreshListener.onSwipeSuccess(this);
            animateContentOffsetToPos(true, true, new OnAnimateContentOffsetListener() {
                @Override
                public void onOffsetEnd() {
                    headerView.updateStatus(SwipeStatus.NORMAL);
                    dealAutoLoadMore();
                }
            });
            animateHeaderOffsetToPos(true, new OnAnimateContentOffsetListener() {
                @Override
                public void onOffsetEnd() {
                    headerView.updateStatus(SwipeStatus.NORMAL);
                }
            });
        }
    }

    public void refreshFail() {
        if (headerView.getSwipeStatus() == SwipeStatus.REFRESHING && swipeDownRefreshListener != null) {
            swipeDownRefreshListener.onSwipeFail(this);
            headerView.updateStatus(SwipeStatus.FAIL);
            animateContentOffsetToPos(true, true, new OnAnimateContentOffsetListener() {
                @Override
                public void onOffsetEnd() {
                    headerView.updateStatus(SwipeStatus.NORMAL);
                    dealAutoLoadMore();
                }
            });
            animateHeaderOffsetToPos(true, new OnAnimateContentOffsetListener() {
                @Override
                public void onOffsetEnd() {
                    headerView.updateStatus(SwipeStatus.NORMAL);
                }
            });
        }
    }

    public void loadMoreStart() {
        loadMoreStart(true);
    }

    private void loadMoreStart(boolean animateAble) {
        if (canSwipeUpRefresh()) {
            lastVisiblePos = Utils.findLastVisibleItemPositions(contentView);
            swipeUpRefreshListener.onSwipeRefresh(this);
            if (animateAble) {
                animateContentOffsetToPos(false, false, null);
                animateFooterOffsetToPos(false, null);
            }
            footerView.updateStatus(SwipeStatus.REFRESHING);
        }
    }

    public void loadMoreSuccess() {
        if (footerView.getSwipeStatus() == SwipeStatus.REFRESHING && swipeUpRefreshListener != null) {
            footerView.updateStatus(SwipeStatus.SUCCESS);
            swipeUpRefreshListener.onSwipeSuccess(this);

            animateContentOffsetToPos(true, false, new OnAnimateContentOffsetListener() {
                @Override
                public void onOffsetEnd() {
                    footerView.updateStatus(SwipeStatus.NORMAL);
                    if (autoScrollAfterLoadMore) {
                        Utils.scrollToItem(contentView, lastVisiblePos);
                    }
                }
            });
            animateFooterOffsetToPos(true, new OnAnimateContentOffsetListener() {
                @Override
                public void onOffsetEnd() {
                    footerView.updateStatus(SwipeStatus.NORMAL);
                }
            });
        }
    }

    public void loadMoreFail() {
        if (footerView.getSwipeStatus() == SwipeStatus.REFRESHING && swipeUpRefreshListener != null) {
            swipeUpRefreshListener.onSwipeFail(this);
            footerView.updateStatus(SwipeStatus.FAIL);
            animateContentOffsetToPos(true, false, new OnAnimateContentOffsetListener() {
                @Override
                public void onOffsetEnd() {
                    footerView.updateStatus(SwipeStatus.NORMAL);
                }
            });
            animateFooterOffsetToPos(true, new OnAnimateContentOffsetListener() {
                @Override
                public void onOffsetEnd() {
                    footerView.updateStatus(SwipeStatus.NORMAL);
                }
            });
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Config or status method
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return Interpolator for header animate and swipe down
     */
    public Interpolator getHeaderInterpolator() {
        return headerInterpolator;
    }

    /**
     * @return Interpolator for footer animate and swipe up
     */
    public Interpolator getFooterInterpolator() {
        return footerInterpolator;
    }

    /**
     * @return damp rate for when drag
     */
    public static float getSwipeDownDampRate() {
        return swipeDownDampRate;
    }

    /**
     * @return {@link #autoScrollAfterLoadMore}
     */
    public boolean isAutoScrollAfterLoadMore() {
        return autoScrollAfterLoadMore;
    }

    /**
     * @return {@link #dampAbleOfSwipeDown}
     */
    public boolean isDampAbleOfSwipeDown() {
        return dampAbleOfSwipeDown;
    }

    /**
     * @return {@link #headerView}
     */
    public SwipeView getHeaderView() {
        return headerView;
    }

    /**
     * @return {@link #swipeDownRefreshListener}
     */
    public OnSwipeRefreshListener getSwipeDownRefreshListener() {
        return swipeDownRefreshListener;
    }

    /**
     * @return {@link #parallaxFactorOfContentSwipeUp}
     */
    public float getParallaxFactorOfContentSwipeUp() {
        return parallaxFactorOfContentSwipeUp;
    }

    /**
     * @return {@link #dampAbleOfSwipeUp}
     */
    public boolean isDampAbleOfSwipeUp() {
        return dampAbleOfSwipeUp;
    }

    /**
     * @return {@link #footerView}
     */
    public SwipeView getFooterView() {
        return footerView;
    }

    /**
     * @return {@link #swipeUpRefreshListener}
     */
    public OnSwipeRefreshListener getSwipeUpRefreshListener() {
        return swipeUpRefreshListener;
    }

    /**
     * @return {@link #canLoadMore}
     */
    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    public boolean isAutoLoadMore() {
        return isAutoLoadMore;
    }

    public boolean isAutoSwipeDownRefresh() {
        return isAutoSwipeDownRefresh;
    }


    public int getLoadMoreSkipNum() {
        return loadMoreSkipNum;
    }

    public int getCurrentContentOffset() {
        return currentContentOffset;
    }

    public View getContentView() {
        return contentView;
    }

    public float getParallaxFactorOfContentSwipeDown() {
        return parallaxFactorOfContentSwipeDown;
    }

    public int getContentViewSwipeDownOffsetRange() {
        return contentViewSwipeDownOffsetRange;
    }

    public int getContentViewSwipeDownStartOffset() {
        return contentViewSwipeDownStartOffset;
    }

    public int getContentViewSwipeDownEndOffset() {
        return contentViewSwipeDownEndOffset;
    }

    public int getContentViewSwipeUpOffsetRange() {
        return contentViewSwipeUpOffsetRange;
    }

    public int getContentViewSwipeUpStartOffset() {
        return contentViewSwipeUpStartOffset;
    }

    public int getContentViewSwipeUpEndOffset() {
        return contentViewSwipeUpEndOffset;
    }

    public long getDurationOfSwipeUp() {
        return durationOfSwipeUp;
    }

    public ASwipeRefreshLayout setSwipeDownRefreshListener(OnSwipeRefreshListener swipeDownRefreshListener) {
        this.swipeDownRefreshListener = swipeDownRefreshListener;
        return this;
    }

    public ASwipeRefreshLayout setSwipeUpRefreshListener(OnSwipeRefreshListener swipeUpRefreshListener) {
        this.swipeUpRefreshListener = swipeUpRefreshListener;
        return this;
    }

    public ASwipeRefreshLayout setAutoRefresh(boolean isAutoRefresh) {
        this.isAutoSwipeDownRefresh = isAutoRefresh;
        return this;
    }

    public ASwipeRefreshLayout setAutoLoadMore(boolean isAutoLoadMore) {
        this.isAutoLoadMore = isAutoLoadMore;
        return this;
    }

    public ASwipeRefreshLayout setHaveMore(boolean isHaveMore) {
        this.canLoadMore = isHaveMore;
        return this;
    }

    public ASwipeRefreshLayout setSwipeLayoutConfigListener(SwipeLayoutConfigListener swipeLayoutConfigListener) {
        this.swipeLayoutConfigListener = swipeLayoutConfigListener;
        return this;
    }

    public ASwipeRefreshLayout setDampAbleOfSwipeDown(boolean dampAbleOfSwipeDown) {
        this.dampAbleOfSwipeDown = dampAbleOfSwipeDown;
        return this;
    }

    public ASwipeRefreshLayout setDampAbleOfSwipeUp(boolean dampAbleOfSwipeUp) {
        this.dampAbleOfSwipeUp = dampAbleOfSwipeUp;
        return this;
    }

    public ASwipeRefreshLayout setHeaderInterpolator(Interpolator headerInterpolator) {
        this.headerInterpolator = headerInterpolator;
        return this;
    }

    public ASwipeRefreshLayout setFooterInterpolator(Interpolator footerInterpolator) {
        this.footerInterpolator = footerInterpolator;
        return this;
    }

    public ASwipeRefreshLayout setTouchSlop(int touchSlop) {
        this.touchSlop = touchSlop;
        return this;
    }

    public ASwipeRefreshLayout setParallaxFactorOfContentSwipeUp(float parallaxFactorOfContentSwipeUp) {
        this.parallaxFactorOfContentSwipeUp = parallaxFactorOfContentSwipeUp;
        return this;
    }

    public ASwipeRefreshLayout setFooterView(SwipeView footerView) {
        SwipeView.Config config = null;
        try {
            config = this.footerView.getConfig().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = indexOfChild(this.footerView);
        removeView(this.footerView);
        addView(footerView, pos);
        this.footerView = footerView;
        this.footerView.setConfig(config);
        return this;
    }

    public ASwipeRefreshLayout setLoadMoreSkipNum(int loadMoreSkipNum) {
        this.loadMoreSkipNum = loadMoreSkipNum;
        return this;
    }

    public ASwipeRefreshLayout setParallaxFactorOfContentSwipeDown(float parallaxFactorOfContentSwipeDown) {
        this.parallaxFactorOfContentSwipeDown = parallaxFactorOfContentSwipeDown;
        return this;
    }

    public ASwipeRefreshLayout setContentViewSwipeDownOffsetRange(int contentViewSwipeDownOffsetRange) {
        this.contentViewSwipeDownOffsetRange = contentViewSwipeDownOffsetRange;
        return this;
    }

    public ASwipeRefreshLayout setContentViewSwipeDownStartOffset(int contentViewSwipeDownStartOffset) {
        this.contentViewSwipeDownStartOffset = contentViewSwipeDownStartOffset;
        return this;
    }

    public ASwipeRefreshLayout setContentViewSwipeDownEndOffset(int contentViewSwipeDownEndOffset) {
        this.contentViewSwipeDownEndOffset = contentViewSwipeDownEndOffset;
        return this;
    }

    public ASwipeRefreshLayout setContentViewSwipeUpOffsetRange(int contentViewSwipeUpOffsetRange) {
        this.contentViewSwipeUpOffsetRange = contentViewSwipeUpOffsetRange;
        return this;
    }

    public ASwipeRefreshLayout setContentViewSwipeUpStartOffset(int contentViewSwipeUpStartOffset) {
        this.contentViewSwipeUpStartOffset = contentViewSwipeUpStartOffset;
        return this;
    }

    public ASwipeRefreshLayout setContentViewSwipeUpEndOffset(int contentViewSwipeUpEndOffset) {
        this.contentViewSwipeUpEndOffset = contentViewSwipeUpEndOffset;
        return this;
    }

    public ASwipeRefreshLayout setDurationOfSwipeDown(long durationOfSwipeDown) {
        this.durationOfSwipeDown = durationOfSwipeDown;
        return this;
    }

    public ASwipeRefreshLayout setDurationOfSwipeUp(long durationOfSwipeUp) {
        this.durationOfSwipeUp = durationOfSwipeUp;
        return this;
    }

    /**
     * @param autoScrollAfterLoadMore {@link #autoScrollAfterLoadMore}
     */
    public ASwipeRefreshLayout setAutoScrollAfterLoadMore(boolean autoScrollAfterLoadMore) {
        this.autoScrollAfterLoadMore = autoScrollAfterLoadMore;
        return this;
    }

    public ASwipeRefreshLayout setHeaderView(SwipeView headerView) {
        SwipeView.Config config = null;
        try {
            config = this.headerView.getConfig().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = indexOfChild(this.headerView);
        removeView(this.headerView);
        addView(headerView, pos);
        this.headerView = headerView;
        this.headerView.setConfig(config);
        return this;
    }

}
