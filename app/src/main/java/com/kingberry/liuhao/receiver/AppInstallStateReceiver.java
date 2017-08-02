package com.kingberry.liuhao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.kingberry.liuhao.MyIterface.IUninstallListener;
import com.kingberry.liuhao.MyIterface.IinstallLinsener;
import com.kingberry.liuhao.MyParamsCls;

/**
 * Created by Administrator on 2017/7/19.
 */

public class AppInstallStateReceiver extends BroadcastReceiver {

//    private final String TAG = this.getClass().getSimpleName();
//    public static SharedPreferences sp;
//    public static SharedPreferences.Editor ed;
//    public static final String mDATA_NAME="mSaveData";
//    public static final String strFirstFlag="isFirstLoad";
//    public static final String strPkgs="PKGS";

    private IUninstallListener mUnstallListener;
    private IinstallLinsener mInstallLins;

    public void setMyInstallListener(IinstallLinsener mInstallLins) {
        // TODO Auto-generated method stub
        this.mInstallLins = mInstallLins;
    }

    public void setMyUninstallListener(IUninstallListener mUnstallListener) {
        // TODO Auto-generated method stub
        this.mUnstallListener = mUnstallListener;
    }

      @Override
    public void onReceive(Context context, Intent intent) {

        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {

            //如果更新应用，也会发出 安装的广播

            String packageName = intent.getData().getSchemeSpecificPart();

            String[] pksArray=MyParamsCls.appPkgs.split(";");

            boolean isNewApp=false;

            for (int i = 0; i < pksArray.length; i++) {
                if (packageName.equals(pksArray[i])){
                    isNewApp=false;
                    Log.e("AppInstallStateReceiver","false*************"+MyParamsCls.appPkgs);
                    return;
                }else{
                    isNewApp=true;
                }
            }

            if (isNewApp==true) {

                //发送广播
                Intent mIntent=new Intent();
                mIntent.setAction(MyParamsCls.mAddAppAction);
                mIntent.putExtra("addAppPkgName",packageName);
                context.sendBroadcast(mIntent);

//                if(mInstallLins!=null&&!TextUtils.isEmpty(packageName)){
//                    mInstallLins.addAppItem(packageName);
//                    Log.e(TAG, packageName + "--------安装成功");
//                }
                Log.e("AppInstallStateReceiver","true*************"+MyParamsCls.appPkgs);
            }
//

        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();

            Log.e("AppInstallStateReceiver", "--------替换成功" + packageName);
            // 发送广播
            Intent mIntent=new Intent();
            mIntent.setAction(MyParamsCls.mReplaceAppAction);
            mIntent.putExtra("replaceAppPkgName",packageName);
            context.sendBroadcast(mIntent);

        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();

            // 发送广播
            Intent mIntent=new Intent();
            mIntent.setAction(MyParamsCls.mRemoveAppAction);
            mIntent.putExtra("removeAppPkgName",packageName);
            context.sendBroadcast(mIntent);

//            if(mUnstallListener!=null&&!TextUtils.isEmpty(packageName)){
//                mUnstallListener.removeApp(packageName);
//                Log.e(TAG, packageName + "--------卸载成功");
//            }
        }
    }

}
