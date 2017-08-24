package com.kingberry.liuhao.impl;

import android.view.MotionEvent;
import android.view.View;

import com.kingberry.liuhao.MyIterface.ViewClickEffect;

/**
 * Created by Administrator on 2017/8/22.
 */

public class OnClickEffectTouchListener implements View.OnTouchListener {

    ViewClickEffect mViewClickEffect = new DefaultClickEffectScaleAnimate();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mViewClickEffect.onPressedEffect(v);
                v.setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                boolean isInside = (x > 0 && x < v.getWidth() && y > 0 && y < v.getHeight());
                if (v.isPressed() != isInside) {
                    v.setPressed(isInside);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mViewClickEffect.onUnPressedEffect(v);
                v.setPressed(false);
                break;
            case MotionEvent.ACTION_UP:
                mViewClickEffect.onUnPressedEffect(v);
                if (v.isPressed()) {
                    v.performClick();
                    v.setPressed(false);
                }
                break;
        }
        return true;
    }
}
