package pl.wkjj.budzik.Activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pl.wkjj.budzik.R;
import pl.wkjj.budzik.Model.Alarm;
import pl.wkjj.budzik.Service.AlarmService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmActivity extends AppCompatActivity implements SensorEventListener {
    @BindView(R.id.activity_ring_dismiss)
    Button dismiss;
    @BindView(R.id.activity_ring_snooze)
    Button snooze;
    @BindView(R.id.activity_ring_clock)
    ImageView clock;
    @BindView(R.id.activity_alarm_textview)
    TextView mission;

    private SensorManager sensorManager;
    private final int MISSIONS_COUNT = 5;
    private int randomNum = ThreadLocalRandom.current().nextInt(5, MISSIONS_COUNT + 1);
    private float[] gravity = new float[3];
    private float[] geomag = new float[3];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Map<Integer, String> missions = new HashMap<Integer, String>();
        missions.put(1, "Skieruj mnie na północ!");
        missions.put(2, "Potrząśnij mną!");
        missions.put(3, "Ukryj mój czujnik zbliżeniowy!");
        missions.put(4, "Niech nastanie ciemność!");
        missions.put(5, "Pora pożegnać się ze światem... upuść mnie (albo podrzuć)!");

        ButterKnife.bind(this);

        mission.setText(missions.get(randomNum));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magno = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        Sensor proxy = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Sensor light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(AlarmActivity.this, accelometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(AlarmActivity.this, magno, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(AlarmActivity.this, proxy, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(AlarmActivity.this, light, SensorManager.SENSOR_DELAY_NORMAL);


        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
                finish();
            }
        });

        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.MINUTE, 10);

                Alarm alarm = new Alarm(
                        new Random().nextInt(Integer.MAX_VALUE),
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE) + 5, // Snooze is set to 5 minutes
                        "Drzemka",
                        System.currentTimeMillis(),
                        true
                );

                alarm.schedule(getApplicationContext());

                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
                finish();
            }
        });

        animateClock();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        int sensorType = sensor.getType();

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                gravity = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomag = event.values.clone();
                break;
        }

        if (sensorType == Sensor.TYPE_ACCELEROMETER && randomNum == 2) {
            if (event.values[0] > 15) {
                finishAlarm();
            }
        } else if (sensorType == Sensor.TYPE_PROXIMITY && randomNum == 3) {
            if (event.values[0] < 2) {
                finishAlarm();
            }
        } else if (sensorType == Sensor.TYPE_LIGHT && randomNum == 4) {
            if (event.values[0] < 4) {
                finishAlarm();
            }
        } else if (sensorType == Sensor.TYPE_ACCELEROMETER && randomNum == 5) {
            double freeFallThreshold = 1;
            Log.w("myApp", String.format("%.2f %.2f %.2f numbers ", event.values[0], event.values[1], event.values[2]));
            if (Math.abs(event.values[0]) <= freeFallThreshold && Math.abs(event.values[1]) <= freeFallThreshold && Math.abs(event.values[2]) <= freeFallThreshold) {
                finishAlarm();
            }
        } else if (randomNum == 1) {
            // Point to north!
            if (gravity != null && geomag != null) {
                float[] inR = new float[16];
                float[] orientVals = new float[3];
                SensorManager.getOrientation(inR, orientVals);
                double azimuth = Math.toDegrees(orientVals[0]);
                double pitch = Math.toDegrees(orientVals[1]);
                double roll = Math.toDegrees(orientVals[2]);
                // Pointing North or near to it
                if (Math.abs(azimuth) <= 15)
                    finishAlarm();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void finishAlarm() {
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        finish();
    }

    private void animateClock() {
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(clock, "rotation", 0f, 20f, 0f, -20f, 0f);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimation.setDuration(800);
        rotateAnimation.start();
    }
}

