package com.kingberry.liuhao;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/18.
 */

public class MyParamsCls {

    public static final int MORE = 99999;

    public static int Width;
    public static int Height;

    //应用列表
    public static ArrayList<AppItem> mAppList = new ArrayList<AppItem>();

    //记录appPkg 包调整后的顺序集合
     public static String appPkgs="";

    //记录MainSty 包调整后的顺序集合
    public static String mainAty="";

    //记录应用是否更新包
    public static boolean isUpdateApp =false;

    //记录监听安装的广播action
    public static String  mAddAppAction="KingBerry.InstallApp.RecevierAction";
    public static String  mRemoveAppAction="KingBerry.RemoveApp.RecevierAction";
    public static String  mReplaceAppAction="KingBerry.ReplaceApp.RecevierAction";

//    public static boolean isAnimationFlag = false ;

}
