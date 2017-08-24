package com.kingberry.liuhao.MyIterface;

/**
 * Created by Administrator on 2017/8/23.
 */

public interface iItemChangeCallback {
    /**
     * @param fromPosition 起始位置
     * @param toPosition 移动的位置
     */
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemSwipe(int position);
}
