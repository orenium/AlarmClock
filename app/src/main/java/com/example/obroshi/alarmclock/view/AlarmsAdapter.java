package com.example.obroshi.alarmclock.view;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public void onBindViewHolder(AlarmViewHolder holder, final int position) {
        final MyAlarm alarm = mAlarmsList.get(position);
        holder.alarm.setText(alarm.getFormattedAlarmTime());
        holder.label.setText(alarm.getAlarmLabel());
        holder.alarmSwitch.setChecked(true);
        holder.deleteAlarmImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlarm(v, alarm, position);
            }
        });
    }

    private boolean deleteAlarm(View view, MyAlarm alarm, int position) {
        try {
            MyAlarm.delete(alarm);
            mAlarmsList.remove(position);
            notifyItemRemoved(position);
            showUndoSnackBar(view, alarm);
            updateList();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateList() {
        mAlarmsList.clear();
        long count = MyAlarm.count(MyAlarm.class);
        List<MyAlarm> list = MyAlarm.listAll(MyAlarm.class);
        for (int i = 0; i < count; i++) {
            mAlarmsList.add(list.get(i));
        }
        notifyDataSetChanged();
    }

    private void showUndoSnackBar(View view, final MyAlarm alarm) {
        Snackbar snackbar = Snackbar
                .make(view, "Alarm deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alarm.save();
                        mAlarmsList.add(alarm);
                        notifyDataSetChanged();
                    }
                });
        snackbar.show();
    }

    @Override
    public int getItemCount() {
        return (mAlarmsList != null ? mAlarmsList.size() : 0);
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        protected TextView alarm;
        protected TextView label;
        protected SwitchCompat alarmSwitch;
        protected ImageView deleteAlarmImg;

        public AlarmViewHolder(View view) {
            super(view);
            alarm = (TextView) view.findViewById(R.id.alarmTime);
            label = (TextView) view.findViewById(R.id.alarmLabel);
            alarmSwitch = (SwitchCompat) view.findViewById(R.id.alarmSwitch);
            deleteAlarmImg = (ImageView) view.findViewById(R.id.deleteAlarmBtn);

        }
    }

}
