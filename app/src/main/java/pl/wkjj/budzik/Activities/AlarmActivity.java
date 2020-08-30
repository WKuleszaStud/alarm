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
    private int randomNum = ThreadLocalRandom.current().nextInt(1, 2);
    private float[] gravity = new float[3];
    private float[] geomag = new float[3];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Map<Integer, String> missions = new HashMap<Integer, String>();
        missions.put(1, "Point me at north!");
        missions.put(2, "Shake me!");
        missions.put(3, "Touch proximity sensor!");
        missions.put(4, "Take me into oblivion!");

        ButterKnife.bind(this);

        mission.setText(missions.get(randomNum));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magno = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor magno2 = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        Sensor orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        Sensor proxy = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Sensor light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(AlarmActivity.this, accelometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(AlarmActivity.this, magno, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(AlarmActivity.this, proxy, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(AlarmActivity.this, light, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(AlarmActivity.this, magno2, SensorManager.SENSOR_DELAY_NORMAL);


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

//        if (sensorType == Sensor.TYPE_MAGNETIC_FIELD && randomNum == 1) {
//            if (event.values[0] > 40) {
//                finishAlarm();
//            }
//        } else
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
        }

        // Point to north!
        if (randomNum == 1) {
            if(sensorType == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {

            }
            if (gravity != null && geomag != null) {
                float[] inR = new float[16];
                float[] orientVals = new float[3];
                SensorManager.getOrientation(inR, orientVals);
//                double azimuth = Math.toDegrees(orientVals[0]);
//                double pitch = Math.toDegrees(orientVals[1]);
//                double roll = Math.toDegrees(orientVals[2]);
                double azimuth = formatDegres(orientVals[0]);
                double pitch = formatDegres(orientVals[1]);
                double roll = formatDegres(orientVals[2]);
                mission.setText(String.format("%.3f %.3f %.3f", orientVals[0], orientVals[1], orientVals[2]));
//                if(Math.abs(orientVals[0]) <= 15)
//                    finishAlarm();
            }
        }
    }

    private double formatDegres(double val) {
        return (val + 360.0) % 360.0;
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

