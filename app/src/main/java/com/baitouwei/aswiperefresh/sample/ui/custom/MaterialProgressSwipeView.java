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

import com.baiouwei.example.R;
import com.baitouwei.swiperefresh.internal.SwipeStatus;
import com.baitouwei.swiperefresh.internal.SwipeView;
import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * @author baitouwei
 */
public class MaterialProgressSwipeView extends SwipeView {
    private ProgressWheel progressWheel;

    public MaterialProgressSwipeView(Context context) {
        super(context);
    }

    @Override
    protected View buildContentView() {
        View v = inflate(getContext(), R.layout.layout_materal_progress, this);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        progressWheel.setLinearProgress(true);
        progressWheel.stopSpinning();
        return v;
    }


    @Override
    public void updatePercent(float percent) {
        super.updatePercent(percent);
        if (!progressWheel.isSpinning()) {
            progressWheel.setInstantProgress(Math.min(percent, 1));
        }
        setAlpha(percent);
    }

    @Override
    public void updateStatus(SwipeStatus swipeStatus) {
        super.updateStatus(swipeStatus);
    }

    @Override
    public void start() {
        progressWheel.spin();
    }

    @Override
    public void stop() {
        progressWheel.stopSpinning();
        progressWheel.setInstantProgress(1);
    }

    @Override
    public boolean isRunning() {
        return progressWheel.isSpinning();
    }
}
