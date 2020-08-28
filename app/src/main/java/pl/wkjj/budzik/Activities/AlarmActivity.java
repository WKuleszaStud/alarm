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
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pl.wkjj.budzik.R;
import pl.wkjj.budzik.Model.Alarm;
import pl.wkjj.budzik.Sensor.ProximitySensor;
import pl.wkjj.budzik.Service.AlarmService;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.security.AccessController.getContext;

public class AlarmActivity extends AppCompatActivity implements SensorEventListener{
    @BindView(R.id.activity_ring_dismiss) Button dismiss;
    @BindView(R.id.activity_ring_snooze) Button snooze;
    @BindView(R.id.activity_ring_clock) ImageView clock;
    @BindView(R.id.activity_alarm_textview) TextView mission;
    private SensorManager sensorManager;
    private Sensor accelometer, magno, light, proxy;
    private int randomNum = ThreadLocalRandom.current().nextInt(2,  5);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        ButterKnife.bind(this);

        if(randomNum == 1 ) {
        mission.setText("Shake me!");
        }
        else if ( randomNum == 2){
            mission.setText("Point me at north!");
        }
        else if( randomNum == 3){
            mission.setText("Touch proximity sensor!");
        }
        else if (randomNum == 4){
            mission.setText("Take me into oblivion!");
        }
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magno = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        proxy = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
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
                        calendar.get(Calendar.MINUTE),
                        "Snooze",
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
      if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && randomNum == 1 ) {
        if (event.values[0] > 40) {
            end();
        }
      }
      else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER && randomNum == 2){
          if (event.values[0] > 15) {
              end();
          }
      }
      else if(sensor.getType() == Sensor.TYPE_PROXIMITY && randomNum == 3){
          if (event.values[0] < 2) {
              end();
          }
      }
      else if (sensor.getType() == Sensor.TYPE_LIGHT && randomNum == 4){
          if (event.values[0] < 4) {
              end();
          }
      }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void end(){
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

