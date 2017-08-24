package com.kingberry.liuhao;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.kingberry.liuhao.MyIterface.DeleteItemInterface;
import com.kingberry.liuhao.MyIterface.DragSource;
import com.kingberry.liuhao.drag.DeleteZone;
import com.kingberry.liuhao.drag.DragController;
import com.kingberry.liuhao.drag.DragLayer;
import com.kingberry.liuhao.drag.DraggableLayout;
import com.kingberry.liuhao.drag.ScrollController;
import com.kingberry.liuhao.impl.MyItemChangeHelperCallback;

import java.util.Collections;
import java.util.List;

import static com.kingberry.liuhao.AppUtils.MAIN_ATY;
import static com.kingberry.liuhao.AppUtils.mDATA_NAME;
import static com.kingberry.liuhao.AppUtils.strFirstFlag;
import static com.kingberry.liuhao.AppUtils.strPkgs;

/**
 * description: 
 * autour: liuhao
 * date: 2017/7/17 12:10
 * update: 2017/7/17
 * version: a
 * */
public class MainActivity extends Activity implements ScrollController.OnPageChangeListener, DragController.DraggingListener, DeleteItemInterface, View.OnLongClickListener, DemoAdapter.ItemDragListener{

    private static final String TAG="MainActicity";
    private static final String MY_KB_PKG_NAME ="com.kingberry.liuhao";
    private static final String INPUT_PKG="com.android.inputmethod.latin";
    private static  final int lineWidth = 12; //网格线的宽度

    //private AppInstallStateReceiver mAppStateReceiver;
    private AddAppReceiver addAppReceiver;
    private RemoveAppReceiver removeAppReceiver;
    private ReplaceAppReceiver replaceAppReceiver;


    //抖动 add by liuhao 0721
    private boolean mNeedShake = false;
    private boolean mStartShake = false;
    private static final int ICON_WIDTH = 80;
    private static final int ICON_HEIGHT = 94;
    private static final float DEGREE_0 = 1.8f;
    private static final float DEGREE_1 = -2.0f;
    private static final float DEGREE_2 = 2.0f;
    private static final float DEGREE_3 = -1.5f;
    private static final float DEGREE_4 = 1.5f;
    private static final int ANIMATION_DURATION = 80;
    private int mCount = 0;
    private float mDensity;
    //抖动 动画 end

    /*判断是否为第一次登陆*/
    private boolean isFirstLoad=true;

    RecyclerView mRecyclerView = null;
    CircleIndicator mIndicator = null;

    //ArrayList<ResolveInfo> mList = new ArrayList<>();

    private PackageManager pm=null;

    private HorizontalPageLayoutManager horizhontalPageLayoutManager;
    ScrollController mScrollController = new ScrollController();

    private DemoAdapter mAdapter = null;
    private int indicatorNumber;

    private DragLayer mDragLayer;
    private DeleteZone mDeleteZone;
    private DragController mDragController;
    //private LinearLayout btnLayout = null;

    //行
    public static int mRow = 0;
    //列
    public static int mColumn = 0;

    //每页显示的最大条目总数
    public int pageSize = 0;

    //是否可拖拽
    private boolean isEnableDrag = true;

    /** 拖动完成时，需要更新数据的页面 */
    private int needUpdateDataPageIndex=-1;

    /** 点击后，重新返回时，需要刷新的页面   */
    private int backUpdatePageIndex=-1;

    ItemTouchHelper.Callback callback ;
    /**
     * 实例化ItemTouchHelper对象,然后添加到RecyclerView
     */
    ItemTouchHelper helper ;


    /**
     * @Title: getDpiInfo
     * @Description: 获取手机的屏幕密度DPI
     * @param
     * @return void
     */

    private void getDpiInfo() {
        // TODO Auto-generated method stub
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        MyParamsCls.Width = metrics.widthPixels;
        MyParamsCls.Height = metrics.heightPixels;
    }


    private void updateIncatorNum() {
        int oldNum = indicatorNumber;

        int endPageIndex = oldNum -1;
        boolean isEnd = mScrollController.getCurrentPageIndex() == endPageIndex ? true : false;

        //refresh indicatorNumber
        indicatorNumber = (MyParamsCls.mAppList.size() / pageSize) + (MyParamsCls.mAppList.size() % pageSize == 0 ? 0 : 1);
        mIndicator.setNumber(indicatorNumber);

        if(indicatorNumber == oldNum + 1 && isEnd){
            mScrollController.arrowScroll(false);
        }else if(indicatorNumber == oldNum - 1 && isEnd){
            mScrollController.arrowScroll(true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_demo);

        Log.e(TAG,"onCreate.....................");

        pm = MainActivity.this.getPackageManager();

        initData();

        initDrag();

        initView();

        //btnLayout = (LinearLayout) findViewById(R.id.layout_btn);
        //btnLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        refreshItemList();
//        mAdapter.notifyDataSetChanged();

//        mAdapter.setOnItemClickPosListener(new DemoAdapter.IOnItemClickPos() {
//            @Override
//            public void getItemPos(int pos) {
//                Log.e(TAG, "onResume: list -> "+ mRecyclerView.getChildCount() );
//                backUpdatePageIndex = pos/pageSize;
//                Log.e(TAG,"OnClick Pos : "+pos + " -- backUpdatePageIndex :" + backUpdatePageIndex);
//                mAdapter.notifyItemRangeChanged(backUpdatePageIndex*pageSize,backUpdatePageIndex*(pageSize+1)-1);
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //add by liuhao 0824
        mAdapter.notifyItemRangeChanged(0,MyParamsCls.mAppList.size());
    }

    public void onBackPressed() {

        int curPage= mScrollController.getCurrentPageIndex();
        if(curPage!=0){
            mScrollController.smoothScrollToPage(0);
        }else {
            return ;
        }

//        mDeleteZone.setVisibility(View.GONE);
//
//        if (!mNeedShake) {
//            super.onBackPressed();
//        } else {
//            mNeedShake = false;
//            mCount = 0;
//            mStartShake = false;
//        }
    }



    @Override
    protected void onPause() {
        super.onPause();

        //Log.e(TAG,"onPause get pks="+sp.getString(strPkgs,""));
        AppUtils.saveDataOrder(MainActivity.this);

        //add by liuhao 0718 end ********************************
    }

    @Override
    protected void onDestroy() {
        if(removeAppReceiver!=null){
            unregisterReceiver(removeAppReceiver);
        }
        if(addAppReceiver!=null){
            unregisterReceiver(addAppReceiver);
        }
        if(replaceAppReceiver!=null){
            unregisterReceiver(replaceAppReceiver);
        }

        super.onDestroy();
    }

    private void initView() {

        //Log.e(TAG,"mList size = "+MyParamsCls.mAppList.size());

        /*
        *************************************************************************************
        ****************************动态设置recyleView的列数 START****************************
         *************************************************************************************/
        //获取屏幕长宽像素信息
        getDpiInfo();

        mColumn = (int) Math.floor(MyParamsCls.Width / 300) ;
        mRow = (int) Math.floor(MyParamsCls.Height / 300) ;

        pageSize = mRow * mColumn ;

        //Log.e(TAG,"initView -> mColumn ="+mColumn+"  mRow = "+mRow +"  pageSize = "+pageSize);
         /*
        *************************************************************************************
        ****************************动态设置recyleView的列数  END ****************************
         *************************************************************************************/

        mRecyclerView = (RecyclerView) findViewById(R.id.demo_listview);

        //为recyclerView添加间距
        SpacesItemDecoration mItemDecoration=new SpacesItemDecoration(MainActivity.this,mRow,mColumn,lineWidth);
        mRecyclerView.addItemDecoration(mItemDecoration);

        mIndicator = (CircleIndicator) findViewById(R.id.demo_indicator);
        mAdapter = new DemoAdapter(MyParamsCls.mAppList, this);
        mAdapter.setLongClickListener(this);
        mAdapter.setDragListener(this);
        mRecyclerView.setAdapter(mAdapter);

        horizhontalPageLayoutManager = new HorizontalPageLayoutManager(mRow, mColumn, lineWidth ,this);
        horizhontalPageLayoutManager.setDragLayer(mDragLayer);
        indicatorNumber = (MyParamsCls.mAppList.size() / pageSize) + (MyParamsCls.mAppList.size() % pageSize == 0 ? 0 : 1);

        mRecyclerView.setLayoutManager(horizhontalPageLayoutManager);

        //添加分页
        mScrollController.setUpRecycleView(mRecyclerView);
        mScrollController.setOnPageChangeListener(this);

        //添加分页指示器--圆形
        mIndicator.setNumber(indicatorNumber);

        mDragLayer.setDragView(mRecyclerView);

        callback = new MyItemChangeHelperCallback(mAdapter);
        /**
         * 实例化ItemTouchHelper对象,然后添加到RecyclerView
         */
        helper = new ItemTouchHelper(callback);

        helper.attachToRecyclerView(mRecyclerView);

//        mDragController.setMyDragState(new MyDragState() {
//            @Override
//            public void isDraging(boolean isDraging) {
//                if (isDraging==true){
//                    helper.attachToRecyclerView(mRecyclerView);
//                }
//            }
//        });

        helper.attachToRecyclerView(mRecyclerView);

        addAppReceiver=new AddAppReceiver();
        IntentFilter addFilter = new IntentFilter();
        addFilter.addAction(MyParamsCls.mAddAppAction);
        this.registerReceiver(addAppReceiver, addFilter);

        removeAppReceiver=new RemoveAppReceiver();
        IntentFilter removeFilter = new IntentFilter();
        removeFilter.addAction(MyParamsCls.mRemoveAppAction);
        this.registerReceiver(removeAppReceiver, removeFilter);

        replaceAppReceiver=new ReplaceAppReceiver();
        IntentFilter replaceFilter = new IntentFilter();
        replaceFilter.addAction(MyParamsCls.mReplaceAppAction);
        this.registerReceiver(replaceAppReceiver, replaceFilter);

    }

    private void initData(){

        SharedPreferences sp=getSharedPreferences(mDATA_NAME, Activity.MODE_PRIVATE);
        isFirstLoad=sp.getBoolean(strFirstFlag,true);

        if(isFirstLoad){
            List<ResolveInfo> apps=AppUtils.getAllApps(MainActivity.this);
            int  i=0;
            for (ResolveInfo pkg : apps){
                    //ActivityInfo atyInfo = pm.getActivityInfo(getComponentName(),PackageManager.GET_META_DATA);
                    if(pkg.activityInfo.packageName.equals(MY_KB_PKG_NAME)){
                        continue;
                    }
                    if(pkg.activityInfo.packageName.equals(INPUT_PKG)){
                        continue;
                    }

                    AppItem appInfo=new AppItem();
                    appInfo.setAppIcon(pkg.activityInfo.loadIcon(pm));
                    appInfo.setAppName((String) pkg.activityInfo.loadLabel(pm));
                    appInfo.setPkgName(pkg.activityInfo.packageName);
                    appInfo.setAppMainAty(pkg.activityInfo.name);
                    appInfo.itemPos=i;
                    MyParamsCls.mAppList.add(appInfo);
                    i++;
            }
        }
        else{
            //Log.e(TAG,"*********NO FIRST*********");
            MyParamsCls.mAppList.clear();
            //MyParamsCls.mAppList.removeAll(MyParamsCls.mAppList);

            MyParamsCls.appPkgs=sp.getString(strPkgs,"");
            MyParamsCls.mainAty=sp.getString(MAIN_ATY,"");

            //Log.e(TAG,"initData get pks="+sp.getString(strPkgs,""));
            Log.e(TAG,"initData get mainAty="+sp.getString(MAIN_ATY,""));

            String[] pksArray=MyParamsCls.appPkgs.split(";");
            String[] mainAtyArray=MyParamsCls.mainAty.split(";");

//            List<ResolveInfo> resolveInfos=AppUtils.getAllApps(MainActivity.this);

            for (int i = 0; i < pksArray.length; i++) {
//                ResolveInfo resolveInfo=new ResolveInfo();
//                for (ResolveInfo tmpResolveInfo : resolveInfos) {
//                    if (tmpResolveInfo.activityInfo.packageName.equals(pksArray[i])) {
//                        resolveInfo=tmpResolveInfo;
//                        break;
//                    }
//                }
                Log.e(TAG,"mainAtyArray["+i+"] = "+mainAtyArray[i]);
                //根据包名取得应用全部信息ResolveInfo
                ResolveInfo resolveInfo = AppUtils.findAppByPackageName(MainActivity.this, pksArray[i]);

                ComponentName  cn= new ComponentName(resolveInfo.activityInfo.packageName, mainAtyArray[i]);

                try {
                    AppItem appInfo = new AppItem();
                    appInfo.setAppIcon(pm.getActivityIcon(cn));
                    appInfo.setAppName(pm.getActivityInfo(cn,0).loadLabel(pm)+"");
                    appInfo.setPkgName(resolveInfo.activityInfo.packageName);
                    appInfo.setAppMainAty(mainAtyArray[i]);
                    appInfo.itemPos = i;
                    MyParamsCls.mAppList.add(i, appInfo);

//                    Log.e(TAG, "appName:" + pm.getActivityInfo(cn,0).loadLabel(pm)+"");
//                    Log.e(TAG, "pkgName:" + resolveInfo.activityInfo.packageName);
//                    Log.e(TAG, "mainAty:" + mainAtyArray[i]);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initDrag() {
        mDragLayer = (DragLayer) findViewById(R.id.demo_draglayer);
        mDeleteZone = (DeleteZone) findViewById(R.id.demo_del_zone);
        mDragController = new DragController(this);

        //是为了把dragLayer里面的触摸、拦截事件传给dragController
        //把很多能力交给dragController处理
        mDragLayer.setDragController(mDragController);
        //设置监听
        mDragLayer.setDraggingListener(MainActivity.this);

        if (mDeleteZone != null) {
            mDeleteZone.setOnItemDeleted(MainActivity.this);
            mDeleteZone.setEnabled(true);
            mDragLayer.setDeleteZoneId(mDeleteZone.getId());
        }

        mDragController.setDraggingListener(mDragLayer);
        mDragController.setScrollController(mScrollController);

    }

    public void onPageChange(int index) {
//        indexPage=index;

        mIndicator.setOffset(index);

        //add by liuhao 0808
        if(needUpdateDataPageIndex!=-1&&index==needUpdateDataPageIndex){
            mAdapter.notifyDataSetChanged();
            needUpdateDataPageIndex=-1;
        }

    }

    @Override
    public boolean onLongClick(View view) {

        if (!view.isInTouchMode() && isEnableDrag) {
            return false;
        }

        //add by liuhao for animation 0721
        //modify by liuhao 0822
//        if (!mStartShake) {
//            mStartShake = true;
//            mNeedShake = true;
//            DragShakeAnimation(view);
//        }

        Lg.d("onLongClick ********************* Drag started");
        DragSource dragSource = (DragSource) view;
        mDragController.startDragBitmap(view, dragSource, dragSource, DragController.DRAG_ACTION_MOVE);

        return true;
    }

    @Override
    public void onDragStart(DragSource source, Object info, int dragAction) {

        AppItem sourceItem=((DraggableLayout)source).getItem();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(sourceItem.getPkgName(), PackageManager.MATCH_UNINSTALLED_PACKAGES);
            Log.e(TAG, "appInfo.flags :" + appInfo.flags);
            //系统应用
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                mDeleteZone.setVisibility(View.GONE);
//                Toast.makeText(MainActivity.this, "系统应用，不能卸载 ！", Toast.LENGTH_SHORT).show();
//                return;
            } else {
                if (appInfo.packageName.contains(MY_KB_PKG_NAME)) {
                    mDeleteZone.setVisibility(View.GONE);
//                    Toast.makeText(MainActivity.this, "应用 ：" + appInfo.loadLabel(pm) + " 不能被卸载！", Toast.LENGTH_SHORT).show();
//                    return;
                }else {
                    mDeleteZone.setVisibility(View.VISIBLE);
                }
            }
        }catch (PackageManager.NameNotFoundException e) {
           // Toast.makeText(MainActivity.this,"找不到该应用~",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
      //  mDeleteZone.setVisibility(View.VISIBLE);
        //btnLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDragEnd() {
        mDeleteZone.setVisibility(View.GONE);

        //add by liuhao for animation 0721
        mNeedShake = false;
        mCount = 0;
        mStartShake = false;
       // btnLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void itemDeleted(DragSource source) {

    }


    @Override
    public void onDragStarted(View source) {
        Log.e( TAG,"onDragStarted soure="+source);
    }

    @Override
    public void onDropCompleted(View source, View target, boolean success) {
        Log.e(TAG,"========onDropCompleted success : " + success);

        if (success && (source != target)) {
            final AppItem sourceItem = ((DraggableLayout) source).getItem();
            //删除操作
            if (target instanceof DeleteZone) {
                if(sourceItem == null){
                    Log.e(TAG,"sourceItem is null in delete action !!!");
                    return;
                }
                if (sourceItem.isDeletable()) {
                    if(MyParamsCls.mAppList.contains(sourceItem)){
                        unstallApp(sourceItem.getPkgName());
                    }
                } else {
                    Toast.makeText(MainActivity.this, "no delete action", Toast.LENGTH_SHORT).show();
                }
            }
            //item之间的替换操作
            else {
                if(sourceItem == null){
                    Lg.e("sourceItem is null in replace action !!!");
                    return;
                }

                AppItem targetItem = ((DraggableLayout) target).getItem();
                if(targetItem == null){
                    //add by liuhao 0802
                    mDeleteZone.setVisibility(View.GONE);
                    mNeedShake = false;
                    mCount = 0;
                    mStartShake = false;
                    //end

                    Lg.e("targetItem is null in replace action !!!");
                    return;
                }
                executeItemReplaceAction(sourceItem, targetItem);
            }
        }

        if(mDragLayer.getDraggingListener() != null){
            mDragLayer.getDraggingListener().onDragEnd();
        }
    }

    /*
       item 位置交换
     */
    private void executeItemReplaceAction(AppItem sourceItem, AppItem targetItem) {

        //来源item信息
        int sourcePos = sourceItem.itemPos;

        //目标item位置
        int targetPos = targetItem.itemPos;

        Lg.d("sourcePos: " + sourcePos + " targetPos: " + targetPos);

        //add by liuhao 0808
        if(sourcePos/pageSize!=targetPos/pageSize){
            needUpdateDataPageIndex=sourcePos/pageSize;
        }
        //位置交换
        //mAdapter.notifyItemMoved(sourcePos,targetPos);
        Collections.swap(MyParamsCls.mAppList, sourcePos, targetPos);
        //modify by liuhao 0803 for Change itemPositon
//        exChangePosition(sourcePos,targetPos);
        refreshItemList();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * @Title: exChangePositon @Description: 拖动变更排序 @param @param
     * dragPostion @param @param dropPostion 参数 @return void 返回类型 @throws
     */
    public void exChangePosition(int dragPostion, int dropPostion) {
        //holdPosition = dropPostion;
        AppItem item = MyParamsCls.mAppList.get(dragPostion);
        Log.e("liuhao Adapter", "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
        if (dragPostion < dropPostion) {
            MyParamsCls.mAppList.add(dropPostion + 1, item);
            MyParamsCls.mAppList.remove(dragPostion);
        } else {
            MyParamsCls.mAppList.add(dropPostion, item);
            MyParamsCls.mAppList.remove(dragPostion + 1);
        }
    }


    private void refreshItemList(){
        for(int i = 0; i < MyParamsCls.mAppList.size(); i++){
            MyParamsCls.mAppList.get(i).itemPos = i;
        }
    }

    //卸载应用程序
    public void unstallApp(String packageName){
        Intent uninstall_intent = new Intent();
        uninstall_intent.setAction(Intent.ACTION_DELETE);
        uninstall_intent.setData(Uri.parse("package:"+packageName));
        MainActivity.this.startActivity(uninstall_intent);
    }


    /**
     * 晃动动画 把需要晃动的View传入即可
     *
     * @param v
     */
    public void DragShakeAnimation(final View v) {
        float rotate = 0;
        int c = mCount++ % 5;
        switch (c) {
            case 0:
                rotate = DEGREE_0;
                break;
            case 1:
                rotate = DEGREE_1;
                break;
            case 2:
                rotate = DEGREE_2;
                break;
            case 3:
                rotate = DEGREE_3;
                break;
            default:
                rotate = DEGREE_4;
                break;

        }

        final RotateAnimation mra = new RotateAnimation(rotate, -rotate, ICON_WIDTH * mDensity / 2, ICON_HEIGHT * mDensity / 2);
        final RotateAnimation mrb = new RotateAnimation(-rotate, rotate, ICON_WIDTH * mDensity / 2, ICON_HEIGHT * mDensity / 2);

        mra.setDuration(ANIMATION_DURATION);
        mrb.setDuration(ANIMATION_DURATION);

        mra.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (mNeedShake) {
                    mra.reset();
                    v.startAnimation(mrb);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });

        mrb.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (mNeedShake) {
                    mrb.reset();
                    v.startAnimation(mra);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });
        v.startAnimation(mra);
    }




    class AddAppReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
           String pkg = intent.getStringExtra("addAppPkgName");
            Log.e(TAG,"mAppStateReceiver.setMyInstallListener addAPP:"+pkg);
//            Log.e(TAG,"mAppStateReceiver.setMyInstallListener size:"+MyParamsCls.mAppList.size());

            //根据包名取得应用全部信息ResolveInfo
            ResolveInfo resolveInfo = AppUtils.findAppByPackageName(MainActivity.this,pkg);

            AppItem appInfo=new AppItem();
            appInfo.setAppIcon(resolveInfo.activityInfo.loadIcon(MainActivity.this.getPackageManager()));
            appInfo.setAppName((String) resolveInfo.activityInfo.loadLabel(MainActivity.this.getPackageManager()));
            appInfo.setPkgName(resolveInfo.activityInfo.packageName);
            appInfo.setAppMainAty(resolveInfo.activityInfo.name);
            appInfo.itemPos=MyParamsCls.mAppList.size();

            MyParamsCls.mAppList.add(MyParamsCls.mAppList.size(),appInfo);

//            refreshItemList();
//            mAdapter.notifyDataSetChanged();
            mAdapter.notifyItemInserted(appInfo.itemPos);
            updateIncatorNum();

            AppUtils.saveDataOrder(MainActivity.this);
        }
    }

    class RemoveAppReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String pkg = intent.getStringExtra("removeAppPkgName");
            Log.e(TAG,"remove:"+" -> "+pkg);

            for (int i = 0; i < MyParamsCls.mAppList.size(); i++) {
                    AppItem item = MyParamsCls.mAppList.get(i);
                if(item.getPkgName().equals(pkg)){
                        Log.e(TAG,"REMOVE:"+i+" -> "+item.getAppName());
                        MyParamsCls.mAppList.remove(item);

//                        refreshItemList();
//                        mAdapter.notifyDataSetChanged();
//                        updateIncatorNum();
                        mAdapter.notifyItemRemoved(item.itemPos);
                        updateIncatorNum();

                    break;
                    }
                }
                AppUtils.saveDataOrder(MainActivity.this);
        }
    }

    class ReplaceAppReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String pkg = intent.getStringExtra("replaceAppPkgName");
            Log.e(TAG,"ReplaceAppReceiver:"+" -> "+pkg);

            //重启自身，以便刷新界面
            refreshItemList();
            mAdapter.notifyDataSetChanged();
            updateIncatorNum();
            AppUtils.saveDataOrder(MainActivity.this);

            Intent restartIntent=pm.getLaunchIntentForPackage("com.kingberry.liuhao");
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(restartIntent);
        }
    }
}
