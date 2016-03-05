package com.example.obroshi.alarmclock.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.obroshi.alarmclock.R;
import com.example.obroshi.alarmclock.controller.Controller;
import com.example.obroshi.alarmclock.model.CalendarEvent;
import com.example.obroshi.alarmclock.model.Constants;

import java.util.ArrayList;
import java.util.List;

public class EventsListFragment extends Fragment {

    private static final String TAG = EventsListFragment.class.getSimpleName();
    //    private static final int LOADER_ID = 1;
    private List<String> mAllCalendarsList = null;
    private List<String> mSelectedCalendars = null;
    private List<CalendarEvent> mEventsList;
    private EventsAdapter mAdapter;
    private EditText mOriginMinutes;
    private EditText mDestinationMinutes;
    private ContentResolver mContentResolver;
    private RecyclerView mRecyclerView;
    private Controller.onEventSelectedListener mSelectedListener;

    private Controller.CalendarCallback mCalendarCallback;

    public EventsListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        mSelectedListener = (Controller.onEventSelectedListener) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
//        setEmptyText("No Events");
//        setListAdapter(mAdapter);
//        setListShown(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final LinearLayout emptyView = (LinearLayout) view.findViewById(R.id.emptyViewNoEvents);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.eventsRecyclerView);
        mOriginMinutes = (EditText) view.findViewById(R.id.originTime);
        mDestinationMinutes = (EditText) view.findViewById(R.id.destinationTime);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        });
        mRecyclerView.hasFixedSize();
        mAdapter = new EventsAdapter(mEventsList);
        mRecyclerView.setAdapter(mAdapter);

        // Check if the user already entered custom time settings
        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains(Constants.ORIGIN_TIME))
            mOriginMinutes.setText(String.valueOf(sharedPref.getInt(Constants.ORIGIN_TIME, 0)));
        if (sharedPref.contains(Constants.DESTINATION_TIME))
            mDestinationMinutes.setText(String.valueOf(sharedPref.getInt(Constants.DESTINATION_TIME, 0)));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // input validation (make sure its a number)
                if ((TextUtils.isDigitsOnly(mDestinationMinutes.getText().toString()))
                        && (TextUtils.isDigitsOnly(mDestinationMinutes.getText().toString()))) {
                    int originTime = mOriginMinutes.getText().length() > 0 ? Integer.parseInt(mOriginMinutes.getText().toString()) : 0;
                    int destinationTime = mDestinationMinutes.getText().length() > 0 ? Integer.parseInt(mDestinationMinutes.getText().toString()) : 0;
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.ORIGIN_TIME, originTime);
                    editor.putInt(Constants.DESTINATION_TIME, destinationTime);
                    editor.commit();
                    Log.d(TAG, "Time in origin is " + mOriginMinutes.getText() + " minutes and time in destination is " + mDestinationMinutes.getText() + " minutes");
                    Log.d(TAG, "Item " + position + " was clicked");
                    CalendarEvent event = mEventsList.get(position);
                    if (mSelectedListener != null) {
                        mSelectedListener.onEventSelected(event);
                    }
                } else {
                    Log.d(TAG, "Invalid input");
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d(TAG, "Item " + position + " long Clicked");
            }
        }));

        mCalendarCallback = new Controller.CalendarCallback() {
            @Override
            public void onDataReceived(List<CalendarEvent> events) {
                mEventsList = events;
                if (mEventsList != null && mEventsList.size() > 0) {
                    emptyView.setVisibility(View.GONE);
                    mAdapter.setData(mEventsList);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCalendarsListReceived(List<String> calendars) {
                if (mSelectedCalendars != null)
                    Controller.getInstance().getEventsFromCalendars(getContext(), mSelectedCalendars, mCalendarCallback);
                else
                    showCalendarSelectionDialog(calendars);
            }
        };
        Controller.getInstance().getCalendarsList(getContext(), mCalendarCallback);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Controller.getInstance().getEventsFromCalendars(getContext(), mSelectedCalendars, mCalendarCallback);
                return true;

            case R.id.action_select_calenders:
                showCalendarSelectionDialog(mAllCalendarsList);
                return true;

//            case R.id.action_settings:
//                // TODO: need to implement settings
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCalendarSelectionDialog(final List<String> calendars) {
        mAllCalendarsList = calendars;
        final CharSequence[] calendarsList = mAllCalendarsList.toArray(new CharSequence[mAllCalendarsList.size()]);
        final boolean[] selectedItems = new boolean[calendarsList.length];   // selected items only
        mSelectedCalendars = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Calendars")
                .setMultiChoiceItems(calendarsList, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        selectedItems[position] = isChecked;
                        if (selectedItems[position]) {
                            mSelectedCalendars.add(calendarsList[position].toString());
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mSelectedCalendars.size() == 0) { // none of the calendar selected
                            Toast.makeText(getActivity(), " At least 1 calender should be selected", Toast.LENGTH_SHORT).show();
                            showCalendarSelectionDialog(mAllCalendarsList);
                        } else {
                            Log.d(TAG, mSelectedCalendars.size() + " calendars selected");
                            Controller.getInstance().getEventsFromCalendars(getActivity(), mSelectedCalendars, mCalendarCallback);
                            dialog.dismiss();
                        }
                    }
                })
                .create().show();
    }


//    @Override
//    public Loader<List<CalendarEvent>> onCreateLoader(int id, Bundle args) {
//        notify = getActivity().getContentResolver();
//        return new EventsListLoader(getActivity(), CalendarContract.Events.CONTENT_URI, mContentResolver, mSelectedCalendars);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<List<CalendarEvent>> loader , List<CalendarEvent> events) {
//        mAdapter.setData(events);
//        mEventsList = events;
//        if (isResumed()){
//            setListShown(true);
//        } else {
//            setListShownNoAnimation(true);
//        }
//    }
//
//
//    @Override
//    public void onLoaderReset(Loader<List<CalendarEvent>> loader) {
//        mAdapter.setData(null);
//    }
}
