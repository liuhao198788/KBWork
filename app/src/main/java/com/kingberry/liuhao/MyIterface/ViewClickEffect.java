package com.kingberry.liuhao.MyIterface;

import android.view.View;

/**
 * Created by Administrator on 2017/8/22.
 *
 * View点击效果接口
 */

public interface ViewClickEffect {

    /**
     * 按下去的效果
     * @param view
     */
    void onPressedEffect(View view);

    /**
     * 释放的效果
     * @param view
     */
    void onUnPressedEffect(View view);
}
