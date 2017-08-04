package com.kingberry.liuhao.drag;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.kingberry.liuhao.MyIterface.DeleteItemInterface;
import com.kingberry.liuhao.MyIterface.DragSource;
import com.kingberry.liuhao.MyIterface.DropTarget;

/**
 * Created by Administrator on 2017/7/17.
 */

public class DeleteZone extends LinearLayout implements DropTarget {

    private DragController mDragController;
    private boolean mEnabled = true;
    private DeleteItemInterface deleteItemInterface;

    public DeleteZone(Context context) {
        super(context);
    }

    public DeleteZone(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DeleteZone(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }

    /**
     * Get the value of the DragController property.
     *
     * @return DragController
     */
    public DragController getDragController() {
        return mDragController;
    }

    /**
     * Set the value of the DragController property.
     *
     * @param newValue DragController
     */
    public void setDragController(DragController newValue) {
        mDragController = newValue;
    }


    /**
     * Handle an object being dropped on the DropTarget.
     * For a DeleteZone, we don't really do anything because we want the view being dragged to vanish.
     *
     * @param source   DragSource where the drag started
     * @param x        X coordinate of the drop location
     * @param y        Y coordinate of the drop location
     * @param xOffset  Horizontal offset with the object being dragged where the original
     *                 touch happened
     * @param yOffset  Vertical offset with the object being dragged where the original
     *                 touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     */
    public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset,
                       DragView dragView, Object dragInfo) {
//    if (isEnabled () && source.canDelete())
//    {
//    	this.deleteHandler.itemDeleted(source);
//    }

    }

    /**
     * React to a dragged object entering the area of this DeleteZone.
     * Provide the user with some visual feedback.
     */
    public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset,
                            DragView dragView, Object dragInfo) {
        // Set the image level so the image is highlighted;
        if (isEnabled() && getBackground() != null) getBackground().setLevel(2);
    }

    /**
     * React to something being dragged over the drop target.
     */
    public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset,
                           DragView dragView, Object dragInfo) {
    }

    /**
     * React to a dragged object leaving the area of this DeleteZone.
     * Provide the user with some visual feedback.
     */
    public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset,
                           DragView dragView, Object dragInfo) {
        if (isEnabled() && getBackground() != null) getBackground().setLevel(1);
    }

    /**
     * Check if a drop action can occur at, or near, the requested location.
     * This may be called repeatedly during a drag, so any calls should return
     * quickly.
     *
     * @param source   DragSource where the drag started
     * @param x        X coordinate of the drop location
     * @param y        Y coordinate of the drop location
     * @param xOffset  Horizontal offset with the object being dragged where the
     *                 original touch happened
     * @param yOffset  Vertical offset with the object being dragged where the
     *                 original touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     * @return True if the drop will be accepted, false otherwise.
     */
    public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset,
                              DragView dragView, Object dragInfo) {
        return isEnabled() && source.isDelete();
    }

    /**
     * Estimate the surface area where this object would land if dropped at the
     * given location.
     *
     * @param source   DragSource where the drag started
     * @param x        X coordinate of the drop location
     * @param y        Y coordinate of the drop location
     * @param xOffset  Horizontal offset with the object being dragged where the
     *                 original touch happened
     * @param yOffset  Vertical offset with the object being dragged where the
     *                 original touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     * @param recycle  {@link Rect} object to be possibly recycled.
     * @return Estimated area that would be occupied if object was dropped at
     * the given location. Should return null if no estimate is found,
     * or if this target doesn't provide estimations.
     */
    public Rect estimateDropLocation(DragSource source, int x, int y, int xOffset, int yOffset,
                                     DragView dragView, Object dragInfo, Rect recycle) {
        return null;
    }

    /**
     * Return true if this DeleteZone is enabled.
     * If it is, it means that it will accept dropped views.
     *
     * @return boolean
     */
    public boolean isEnabled() {
        return mEnabled && (getVisibility() == View.VISIBLE);
    }

    /**
     * Set up the drop spot by connecting it to a drag controller.
     * When this method completes, the drop spot is listed as one of the drop targets of the controller.
     *
     * @param controller DragController
     */
    public void setup(DragController controller) {
        mDragController = controller;

        if (controller != null) {
            controller.addDropTarget(this);
        }
    }

    public void setOnItemDeleted(DeleteItemInterface itemInterface) {
        this.deleteItemInterface = itemInterface;
    }
}
