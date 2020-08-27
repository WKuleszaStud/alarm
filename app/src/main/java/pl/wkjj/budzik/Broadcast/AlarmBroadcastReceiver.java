package pl.wkjj.budzik.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import pl.wkjj.budzik.Service.AlarmService;
import pl.wkjj.budzik.Service.RescheduleAlarmsService;

import java.util.Calendar;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static final String TITLE = "TITLE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String toastText = String.format("Alarm Reboot");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            startRescheduleAlarmsService(context);
        } else {
            String toastText = String.format("Alarm Received");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            startAlarmService(context, intent);

        }
    }


    private void startAlarmService(Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra(TITLE, intent.getStringExtra(TITLE));
        context.startForegroundService(intentService);
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
        context.startForegroundService(intentService);
    }
}
