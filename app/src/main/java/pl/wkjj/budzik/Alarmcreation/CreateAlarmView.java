package pl.wkjj.budzik.Alarmcreation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import pl.wkjj.budzik.Model.Alarm;
import pl.wkjj.budzik.Dao.AlarmRepository;

public class CreateAlarmView extends AndroidViewModel {
    private AlarmRepository alarmRepository;

    public CreateAlarmView(@NonNull Application application) {
        super(application);

        alarmRepository = new AlarmRepository(application);
    }

    public void insert(Alarm alarm) {
        alarmRepository.insert(alarm);
    }
}
