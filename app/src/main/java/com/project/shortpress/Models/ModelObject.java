package com.project.shortpress.Models;

import com.project.shortpress.R;

public enum ModelObject {

    First(R.string.first_time_act_first, R.layout.fragment_first_time1),
    Second(R.string.first_time_act_second, R.layout.fragment_first_time2),
    Third(R.string.first_time_act_third, R.layout.fragment_first_time3);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}

