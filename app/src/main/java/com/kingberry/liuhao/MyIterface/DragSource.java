package com.kingberry.liuhao.MyIterface;

import android.view.View;

import com.kingberry.liuhao.drag.DragController;

/**
 * Created by Administrator on 2017/7/17.
 */

public interface DragSource {
    void setDragController(DragController dragger);
    void onDropCompleted(View target, boolean success);
    boolean isDelete();
}