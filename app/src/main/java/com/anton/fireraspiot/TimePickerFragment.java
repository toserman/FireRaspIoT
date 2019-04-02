package com.anton.fireraspiot;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;



import java.util.Calendar;
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    TextView tvDelayTime;
    int exthour = 0;
    int extminute = 0;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        //int hour = c.get(Calendar.HOUR_OF_DAY);
        //int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, exthour, extminute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        tvDelayTime = (TextView) getActivity().findViewById(R.id.textView1);
        tvDelayTime.setText("Hour: "+ Integer.toString(hourOfDay)+" Minute: "+ Integer.toString(minute));
        exthour = hourOfDay;
        extminute = minute;
    }

    public int getHour() {
        return exthour;
    }
    public int getMinute() {
        return extminute;
    }

}