package com.example.obroshi.alarmclock.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.obroshi.alarmclock.R;
import com.example.obroshi.alarmclock.model.MyAlarm;

import java.util.List;

public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.AlarmViewHolder> {
    private final String TAG = AlarmsAdapter.class.getSimpleName();

    private List<MyAlarm> mAlarmsList;
    private Context mContext;

    public AlarmsAdapter(Context context, List<MyAlarm> alarmsList) {
        this.mContext = context;
        this.mAlarmsList = alarmsList;
    }


    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_single_item, null);
        AlarmViewHolder holder = new AlarmViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        MyAlarm alarm = mAlarmsList.get(position);
        holder.alarm.setText(alarm.getFormattedAlarmTime());
        holder.label.setText(alarm.getAlarmLabel());
        holder.alarmSwitch.setChecked(true);
    }

    @Override
    public int getItemCount() {
        return (mAlarmsList != null ? mAlarmsList.size(): 0);
    }


    public class AlarmViewHolder extends RecyclerView.ViewHolder{

        protected TextView alarm;
        protected TextView label;
        protected SwitchCompat alarmSwitch;

        public AlarmViewHolder(View view) {
            super(view);
            alarm = (TextView) view.findViewById(R.id.alarmTime);
            label = (TextView) view.findViewById(R.id.alarmLabel);
            alarmSwitch = (SwitchCompat) view.findViewById(R.id.alarmSwitch);

        }
    }

}
