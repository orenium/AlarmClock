package com.example.obroshi.alarmclock.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.obroshi.alarmclock.model.CalendarEvent;
import com.example.obroshi.alarmclock.R;

import java.util.List;

class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {
    private List<CalendarEvent> mEventsList;
    private Context mContext;

    public EventsAdapter(Context context, List<CalendarEvent> eventsList) {
        this.mContext = context;
        this.mEventsList = eventsList;
    }

    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_single_event, null);
        EventsViewHolder holder = new EventsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(EventsViewHolder holder, int position) {
        CalendarEvent event = mEventsList.get(position);
        holder.monthInYear.setText(event.getMonthShortName());
        holder.dayInMonth.setText(event.getDayInMonth());
        holder.title.setText(event.getTitle());
        holder.mSeparatorView.setBackgroundColor(event.getCalendarColor());
//        holder.layout.setBackgroundColor(event.getCalendarColor());
        holder.startTime.setText(event.getFormattedStartingTime());
        if (event.getEndingTime().isEmpty()) {
            holder.endTime.setVisibility(View.GONE);
        } else {
            holder.endTime.setVisibility(View.VISIBLE);
            holder.endTime.setText(" - " + event.getEndingTime());
        }
        if (event.getLocation().isEmpty()) {
            holder.location.setVisibility(View.GONE);
        } else {
            holder.location.setVisibility(View.VISIBLE);
            holder.location.setText(event.getLocation());
        }
//        holder.calendarName.setText("(" + event.getCalendarName() + ")");
    }

    public void setData(List<CalendarEvent> events) {
        mEventsList = events;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (mEventsList != null ? mEventsList.size() : 0);
    }

    public class EventsViewHolder extends RecyclerView.ViewHolder {

        protected TextView monthInYear;
        protected TextView dayInMonth;
        protected TextView title;
        protected TextView startTime;
        protected TextView endTime;
        protected TextView location;
        protected View mSeparatorView;
//        protected LinearLayout layout;
//        protected TextView calendarName;

        public EventsViewHolder(View view) {
            super(view);
//            layout = (LinearLayout) view.findViewById(R.id.eventInfoLayout);
            monthInYear = (TextView) view.findViewById(R.id.monthInYear);
            dayInMonth = (TextView) view.findViewById(R.id.dayInMonth);
            title = (TextView) view.findViewById(R.id.title);
            startTime = (TextView) view.findViewById(R.id.startTime);
            endTime = (TextView) view.findViewById(R.id.endTime);
            location = (TextView) view.findViewById(R.id.location);
            mSeparatorView = view.findViewById(R.id.separatorView);
//            calendarName = (TextView) view.findViewById(R.id.calendarName);
        }
    }
}
