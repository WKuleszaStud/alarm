package pl.wkjj.budzik.Sensor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import pl.wkjj.budzik.Service.AlarmService;



    public class ProximitySensor implements SensorEventListener {

        private SensorManager sensorManager;
        private Sensor sensor;
        private Context context;
        private boolean trouble=false;

        public ProximitySensor(Context context) {
            this.context = context;
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        public boolean isTrouble() {
            return trouble;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.values[0] < 5) {
               trouble = true;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }


        public void close() {
            sensorManager.unregisterListener(this);
        }
    }


