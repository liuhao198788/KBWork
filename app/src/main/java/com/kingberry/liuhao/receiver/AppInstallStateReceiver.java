package com.kingberry.liuhao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import com.kingberry.liuhao.AppItem;
import com.kingberry.liuhao.AppUtils;
import com.kingberry.liuhao.MyIterface.IUninstallLinster;
import com.kingberry.liuhao.MyParamsCls;

import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */

public class AppInstallStateReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();

    private IUninstallLinster mUnstallLinster;

    public void setMyUninstallListener(IUninstallLinster mUnstallLinster) {
        // TODO Auto-generated method stub
        this.mUnstallLinster = mUnstallLinster;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        PackageManager pm = context.getPackageManager();

        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {

            //如果更新应用，也会发出 安装的广播
//            if (MyParamsCls.isUpdateApp==true){
//                return;
//            }

            String packageName = intent.getData().getSchemeSpecificPart();

            ResolveInfo resolveInfo = AppUtils.findAppByPackageName(context, packageName);

            AppItem appInfo = new AppItem();
            appInfo.setAppIcon(resolveInfo.activityInfo.loadIcon(pm));
            appInfo.setAppName((String) resolveInfo.activityInfo.loadLabel(pm));
            appInfo.setPkgName(resolveInfo.activityInfo.packageName);
            appInfo.setAppMainAty(resolveInfo.activityInfo.name);

            appInfo.itemPos = MyParamsCls.mAppList.size();
            MyParamsCls.mAppList.add(appInfo);

            AppUtils.saveData(context);

            MyParamsCls.isUpdateApp=true;

            Log.e(TAG, packageName + "--------安装成功 itemPos" + appInfo.itemPos + " count :" + MyParamsCls.mAppList.size());

        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
//            MyParamsCls.isUpdateApp=true;
//
            String packageName = intent.getData().getSchemeSpecificPart();

            if(mUnstallLinster!=null&&!TextUtils.isEmpty(packageName)){
                mUnstallLinster.replaceApp(packageName);
                Log.e(TAG, packageName + "--------替换成功");
                MyParamsCls.mAppList.clear();
                List<ResolveInfo> apps=AppUtils.getAllApps(context);
                int  i=0;
                for (ResolveInfo pkg : apps){

                    AppItem appInfo=new AppItem();
                    appInfo.setAppIcon(pkg.activityInfo.loadIcon(pm));
                    appInfo.setAppName((String) pkg.activityInfo.loadLabel(pm));
                    appInfo.setPkgName(pkg.activityInfo.packageName);
                    appInfo.setAppMainAty(pkg.activityInfo.name);
                    appInfo.itemPos=i;
                    MyParamsCls.mAppList.add(appInfo);
                    i++;
                }

                AppUtils.saveData(context);

            }

//            ResolveInfo resolveInfo = AppUtils.findAppByPackageName(context, packageName);
//
//            AppItem appInfo = new AppItem();
//            appInfo.setAppIcon(resolveInfo.activityInfo.loadIcon(pm));
//            appInfo.setAppName((String) resolveInfo.activityInfo.loadLabel(pm));
//            appInfo.setPkgName(resolveInfo.activityInfo.packageName);
//            appInfo.setAppMainAty(resolveInfo.activityInfo.name);
//
//            for(AppItem item:MyParamsCls.mAppList){
//                if (item.getAppName().equals(packageName)){
//                    Collections.replaceAll(MyParamsCls.mAppList,item,appInfo);
//                }
//            }
//
//            AppUtils.saveData(context);
            Log.e(TAG, "--------替换成功" + packageName);

        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if(mUnstallLinster!=null&&!TextUtils.isEmpty(packageName)){
                mUnstallLinster.removeApp(packageName);
                Log.e(TAG, packageName + "--------卸载成功");
            }
        }
    }

}
