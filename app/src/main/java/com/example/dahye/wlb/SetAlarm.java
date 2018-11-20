package com.example.dahye.wlb;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Set;

public class SetAlarm extends Activity {
    TimePicker mtimePicker;
    int hour, minute;
    Button set_alarm, cancel_alarm;
    TextView malarmTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setalarm);
        set_alarm = (Button) findViewById(R.id.setAlarm);
        cancel_alarm = (Button) findViewById(R.id.cancelAlarm);
        cancel_alarm.setVisibility(View.INVISIBLE);
        mtimePicker = (TimePicker)findViewById(R.id.timePicker);
        malarmTime = (TextView)findViewById(R.id.Alarmtime);

        SharedPreferences preferences = getSharedPreferences("pref",MODE_PRIVATE);
        malarmTime.setText(preferences.getString("AlarmTime",""));
        if(malarmTime.getText().toString() != ""){ // 알람이 이미 설정되어 있으면
            set_alarm.setVisibility(View.INVISIBLE);
            cancel_alarm.setVisibility(View.VISIBLE);
        }else{
            set_alarm.setVisibility(View.VISIBLE);
            cancel_alarm.setVisibility(View.INVISIBLE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = mtimePicker.getHour();
            minute = mtimePicker.getMinute();
        }else{
            hour = mtimePicker.getCurrentHour();
            minute = mtimePicker.getCurrentMinute();
        }
        mtimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                hour = i;
                minute = i1;
            }
        });
        set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlarmHatt(getApplicationContext()).Alarm(hour, minute);
                Toast.makeText(SetAlarm.this, "알람이 설정되었습니다!",Toast.LENGTH_SHORT).show();
                set_alarm.setVisibility(View.INVISIBLE);
                cancel_alarm.setVisibility(View.VISIBLE);
                malarmTime.setText(hour + "시" + minute + "분에 알람이 설정되었습니다.");
            }
        });
        cancel_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlarmHatt(getApplicationContext()).cancel();
                Toast.makeText(SetAlarm.this, "알람이 삭제되었습니다!",Toast.LENGTH_SHORT).show();

                set_alarm.setVisibility(View.VISIBLE);
                cancel_alarm.setVisibility(View.INVISIBLE);
                malarmTime.setText("");
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
            calendar.setTimeInMillis(System.currentTimeMillis());
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent =null;
        if(item.getItemId()==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            intent = new Intent(getApplicationContext(), login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        }
        switch (item.getItemId()){
            case R.id.redirect_main:
                this.finish();
            case R.id.redirect_addcategory:
                intent = new Intent(this,addcategory.class);
                startActivity(intent);
                this.finish();
            case R.id.redirect_alarm:
                /*intent = new Intent(this,SetAlarm.class);
                startActivity(intent);
                return true;*/
                return super.onOptionsItemSelected(item);
            case R.id.redirect_diary:
                intent = new Intent(this,diarylist.class);
                startActivity(intent);
                this.finish();
            case R.id.redirect_test:
                intent = new Intent(this,Testview.class);
                startActivity(intent);
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void onStop(){
        super.onStop();
        TextView malarmTime = (TextView)findViewById(R.id.Alarmtime);
        String stralarmTime = malarmTime.getText().toString();
        SharedPreferences preferences = getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("AlarmTime", stralarmTime);
        editor.commit();
    }
}

