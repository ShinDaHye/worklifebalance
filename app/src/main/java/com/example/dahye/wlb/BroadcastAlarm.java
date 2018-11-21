package com.example.dahye.wlb;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

public class BroadcastAlarm extends BroadcastReceiver {
    private final String CH_ID = "chan";
    private final int NO_ID = 001;
    int importance = NotificationManager.IMPORTANCE_HIGH;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(CH_ID,"CH_NAME",importance);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            notificationManager.createNotificationChannel(mChannel);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, new Intent(context, MainActivity.class),PendingIntent.FLAG_NO_CREATE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CH_ID);
        builder.setSmallIcon(R.mipmap.ic_icon)
                .setContentTitle("오늘은 어땠나요?")
                .setContentText("오늘의 워라밸을 입력해주세요>_<!")
                .setAutoCancel(true) // 터치 시 자동 삭제
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        notificationManager.notify(NO_ID, builder.build());
    }
}
