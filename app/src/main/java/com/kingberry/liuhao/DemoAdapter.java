package com.kingberry.liuhao;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingberry.liuhao.MyIterface.DragListener;
import com.kingberry.liuhao.MyIterface.iItemChangeCallback;
import com.kingberry.liuhao.drag.DraggableLayout;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 2017/7/17.
 */

public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.mBaseViewHolder> implements DragListener ,iItemChangeCallback{

    public static final String TAG="DemoAdapter";

    private ArrayList<AppItem> mList = new ArrayList<>();
    private Context mContext;

    public DemoAdapter(ArrayList<AppItem> list, Context context){
        this.mList = list;
        this.mContext = context;

    }

    @Override
    public mBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        return new mBaseViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(mBaseViewHolder holder, final int position) {
        AppItem appItem = mList.get(position);
        holder.tv_title.setText(appItem.getAppName());
        holder.icon.setImageDrawable(appItem.getAppIcon());

        if(longClickListener != null){
            holder.layout.setOnLongClickListener(longClickListener);
        }
        if(clickListener != null){
            holder.layout.setOnClickListener(clickListener);
        }

        DraggableLayout layout = holder.layout;
        AppItem item = mList.get(position);
        layout.setItem(item);
        layout.setImage(holder.icon);
        layout.setText(holder.tv_title);
        layout.canDelete(item.isDeletable());
        layout.setDragListener(this);

        //add by liuhao 0828
        layout.setAnimaView(holder.layout);

//        holder.girdItemLayout.setOnTouchListener(new OnClickEffectTouchListener());
//        layout.setOnTouchListener(new OnClickEffectTouchListener());

        holder.layoutLL.setOnClickListener(new mItemOnclick(position));
        layout.setOnClickListener(new mItemOnclick(position));

        //Log.e("DemoAdapter","appName = "+holder.tv_title.getText());
}

    class mItemOnclick implements View.OnClickListener{
        int pos;
        mItemOnclick(int pos){
            mItemOnclick.this.pos=pos;

//            onItemClickPos.getItemPos(pos);
        }
        @Override
        public void onClick(View v) {

            Animation myAnimation_Scale = new ScaleAnimation(0.0f, 1.5f, 0.0f, 1.5f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            //设置时间持续时间为 200毫秒
            myAnimation_Scale.setDuration(100);
            v.startAnimation(myAnimation_Scale);

            AppItem appItem = mList.get(pos);
            // info = (DragView)parent.getItemAtPosition(position);

            // 应用的包名
            String pkg = appItem.getPkgName();

            // Log.e(TAG,"pkg = "+pkg);
            //应用的主Activity
            String cls = appItem.getAppMainAty();
            ComponentName componentName = new ComponentName(pkg, cls);
            Intent intent = new Intent();
            //添加到任务栈 0821 add by liuhao
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(componentName);
            mContext.startActivity(intent);
        }
    }


    private ItemDragListener dragListener = null;
    public interface ItemDragListener{
        void onDragStarted(View source);
        void onDropCompleted(View source, View target, boolean success);
    }

    public void setDragListener(ItemDragListener listener){
        dragListener = listener;
    }

    @Override
    public void onDragStarted(View source) {
        if(dragListener != null){
            dragListener.onDragStarted(source);
        }
    }

    @Override
    public void onDropCompleted(View source, View target, boolean success) {
        if(dragListener != null){
            dragListener.onDropCompleted(source, target, success);
        }
    }

    private View.OnLongClickListener longClickListener = null;
    public void setLongClickListener(View.OnLongClickListener listener){
        longClickListener = listener;
    }

    private View.OnClickListener clickListener = null;
    public void setClickListener(View.OnClickListener listener){
        clickListener = listener;
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class mBaseViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView tv_title;
        LinearLayout layoutLL;
        DraggableLayout layout;

        public mBaseViewHolder(View itemView) {
            super(itemView);
            layoutLL= (LinearLayout) itemView.findViewById(R.id.layoutLL);
            tv_title = (TextView) layoutLL.findViewById(R.id.tv_title);
            icon = (ImageView) layoutLL.findViewById(R.id.icon);
            layout = (DraggableLayout) layoutLL.findViewById(R.id.layout);
        }
    }

    //define interface
    public static interface OnAdapterItemClickListener {
        void onAdapterItemClick(View view , int position);
    }


    public boolean onItemMove(int fromPosition, int toPosition) {
        //得到当拖拽的viewHolder的Position
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;

    }

    @Override
    public void onItemSwipe(int position) {

        /**
         * 原数据移除数据
         */
        mList.remove(position);
        /**
         * 通知移除
         */
        notifyItemRemoved(position);
    }

}
