package com.example.dahye.wlb;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Set;

public class SetAlarm extends Activity {
    TimePicker mtimePicker;
    int hour, minute;
    Button set_alarm, cancel_alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setalarm);
        set_alarm = (Button) findViewById(R.id.setAlarm);
        cancel_alarm = (Button) findViewById(R.id.cancelAlarm);
        mtimePicker = (TimePicker)findViewById(R.id.timePicker);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = mtimePicker.getHour();
            minute = mtimePicker.getHour();
        }else{
            hour = mtimePicker.getCurrentHour();
            minute = mtimePicker.getCurrentMinute();
        }
        set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlarmHatt(getApplicationContext()).Alarm(hour, minute);
                Toast.makeText(SetAlarm.this, "알람이 설정되었습니다!",Toast.LENGTH_SHORT).show();
            }
        });
        cancel_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlarmHatt(getApplicationContext()).cancel();
                Toast.makeText(SetAlarm.this, "알람이 삭제되었습니다!",Toast.LENGTH_SHORT).show();
            }
        });

    }
    public class AlarmHatt{
        private Context context;
        public AlarmHatt(Context context){
            this.context = context;
        }
        public void Alarm(int hour, int minute){
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(SetAlarm.this, BroadcastAlarm.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(SetAlarm.this,0,intent,0);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,hour);
            calendar.set(Calendar.MINUTE,minute);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        }
        public void cancel(){
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(SetAlarm.this, BroadcastAlarm.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(SetAlarm.this,0,intent,0);
            alarmManager.cancel(pendingIntent);
        }
    }
}
