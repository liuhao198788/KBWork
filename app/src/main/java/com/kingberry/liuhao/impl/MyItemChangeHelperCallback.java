package com.kingberry.liuhao.impl;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.kingberry.liuhao.MyIterface.iItemChangeCallback;

/**
 * Created by Administrator on 2017/8/23.
 */

public class MyItemChangeHelperCallback extends ItemTouchHelper.Callback {

    private final iItemChangeCallback mAdapter;

    public MyItemChangeHelperCallback(iItemChangeCallback mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
//        return super.isLongPressDragEnabled();
        // 不需要长按拖拽功能  我们手动控制
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
       //return super.isItemViewSwipeEnabled();
        // 不需要滑动功能
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //首先回调的方法 返回int表示是否监听该方向
        //dragFlags 是拖拽标志，swipeFlags是滑动标志，我们把swipeFlags 都设置为0，表示不处理滑动操作。
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        final int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            // 当item的类型不一样的时候不能交换
            return false;
        }

        if (mAdapter instanceof MyItemChangeHelperCallback) {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }

        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemSwipe(viewHolder.getAdapterPosition());
    }

}
