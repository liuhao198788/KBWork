package com.kingberry.liuhao.drag;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingberry.liuhao.AppItem;
import com.kingberry.liuhao.MyIterface.DragListener;
import com.kingberry.liuhao.MyIterface.DragSource;
import com.kingberry.liuhao.MyIterface.DropTarget;

/**
 * Created by Administrator on 2017/7/17.
 */

public class DraggableLayout extends LinearLayout implements DropTarget,DragSource {

    private ImageView image;
    private TextView text;
    private int cellNumber;
    private AppItem gridItem;
    private DragListener listener;
    private boolean isDelete;

    private LinearLayout layoutLL;

    private static final int ANIMATION_DURATION = 800;
    private Animation alphaAnima = new AlphaAnimation(0.38f,0.08f);

    public DraggableLayout(Context context, AttributeSet attrs,
                           int defStyle) {
        super(context, attrs, defStyle);
    }

    public DraggableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableLayout(Context context) {
        super(context);
    }

    @Override
    public void onDrop(DragSource source, int x, int y, int xOffset,
                       int yOffset, DragView dragView, Object dragInfo) {
    }

    @Override
    public void onDragEnter(DragSource source, int x, int y, int xOffset,
                            int yOffset, DragView dragView, Object dragInfo) {
        //add by liuhao 0828
        mDragEnterAnimation(true);
    }

    @Override
    public void onDragOver(DragSource source, int x, int y, int xOffset,
                           int yOffset, DragView dragView, Object dragInfo) {
    }

    @Override
    public void onDragExit(DragSource source, int x, int y, int xOffset,
                           int yOffset, DragView dragView, Object dragInfo) {
        //add by liuhao 0828
        mDragEnterAnimation(false);
    }

    @Override
    public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
                              int yOffset, DragView dragView, Object dragInfo) {
//        return (cellNumber >= 0) && source != this;
        return  source!=this;
    }

    @Override
    public Rect estimateDropLocation(DragSource source, int x, int y,
                                     int xOffset, int yOffset, DragView dragView, Object dragInfo,
                                     Rect recycle) {
        return null;
    }

    @Override
    public void setDragController(DragController dragger) {
    }

    @Override
    public void onDropCompleted(View target, boolean success) {
//        MyParamsCls.isAnimationFlag=false;
        if (listener != null) {
            listener.onDropCompleted(this, target, success);
        }
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public ImageView getImage() {
        return image;
    }


    public void setText(TextView text) {
        this.text = text;
    }

    public TextView getText() {
        return text;
    }

    public void setItem(AppItem gridItem) {
        this.gridItem = gridItem;
    }

    public AppItem getItem() {
        return this.gridItem;
    }

    public void setDragListener(DragListener listener) {
        this.listener = listener;
    }

    public void setAnimaView(LinearLayout layoutLL) {
        this.layoutLL=layoutLL;
    }


    @Override
    public boolean isDelete() {
        return isDelete;
    }

    public void canDelete(boolean b) {
        this.isDelete = b;
    }


    //add by liuhao 0828
    public void mDragEnterAnimation(final boolean isNeedAnima) {

        if(layoutLL==null){
            return ;
        }

        alphaAnima.setDuration(ANIMATION_DURATION);

        alphaAnima.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (isNeedAnima==true) {
                    alphaAnima.reset();
                    layoutLL.startAnimation(alphaAnima);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
            @Override
            public void onAnimationStart(Animation animation) {

            }
        });
        layoutLL.startAnimation(alphaAnima);
    }

    
}
