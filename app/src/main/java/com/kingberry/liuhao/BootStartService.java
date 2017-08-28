package com.kingberry.liuhao;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.kingberry.liuhao.receiver.BootReceiver;

/**
 * Created by Administrator on 2017/8/26.
 */

public class BootStartService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        onCreate();
    }

    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);

        AlarmManager am=(AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent=new Intent(this,BootReceiver.class);
        PendingIntent pIntent=PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        am.setRepeating(0, 1*1000 + System.currentTimeMillis(), 1*1000, pIntent);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = Service.START_FLAG_RETRY;//Intent.START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }
}
