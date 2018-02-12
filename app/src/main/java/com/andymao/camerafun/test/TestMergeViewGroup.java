package com.andymao.camerafun.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.andymao.camerafun.R;

/**
 * Created by hongyun
 * on 22/12/2017
 */

public class TestMergeViewGroup extends FrameLayout {
    public TestMergeViewGroup(@NonNull Context context) {
        super(context);
        initView();
    }

    private void initView() {
//        View view = View.inflate(getContext(), R.layout.viewstub_layout_merge, this);

        LayoutInflater.from(getContext()).inflate(R.layout.viewstub_layout_merge,this,true);
    }

    public TestMergeViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TestMergeViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
}
