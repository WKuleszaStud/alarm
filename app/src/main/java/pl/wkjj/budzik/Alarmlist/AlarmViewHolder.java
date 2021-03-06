package pl.wkjj.budzik.Alarmlist;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.wkjj.budzik.R;
import pl.wkjj.budzik.Model.Alarm;

public class AlarmViewHolder extends RecyclerView.ViewHolder {
    private TextView alarmInfo;


    Switch alarmStarted;

    private OnToggleAlarmListener listener;

    public AlarmViewHolder(@NonNull View itemView, OnToggleAlarmListener listener) {
        super(itemView);

        alarmInfo = itemView.findViewById(R.id.item_alarm_info);
        alarmStarted = itemView.findViewById(R.id.item_alarm_started);


        this.listener = listener;
    }

    public void bind(Alarm alarm) {
        String alarmText = String.format("%02d:%02d", alarm.getHour(), alarm.getMinute());


        alarmStarted.setChecked(alarm.isStarted());


        if (alarm.getTitle().length() != 0) {
            alarmInfo.setText(alarmText.concat((", " + alarm.getTitle())));
        } else {
            alarmInfo.setText(alarmText.concat(", Alarm"));
        }


      /*  if (alarm.getTitle().length() != 0) {
            alarmTitle.setText(String.format("%s | %d | %d", alarm.getTitle(), alarm.getAlarmId(), alarm.getCreated()));
        } else {
            alarmTitle.setText(String.format("%s | %d | %d", "Alarm", alarm.getAlarmId(), alarm.getCreated()));
        }
*/
        alarmStarted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.onToggle(alarm);
            }
        });
    }
}
