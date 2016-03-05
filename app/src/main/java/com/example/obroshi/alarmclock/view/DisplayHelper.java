package com.example.obroshi.alarmclock.view;

import android.content.Context;
import android.view.View;

import com.example.obroshi.alarmclock.R;

/**
 * Created by Lital Noa on 05/03/2016.
 */
public final class DisplayHelper {
    private DisplayHelper() {
    }

    public static String getMonthShortName(View view, int month) {
        return getMonthShortName(view.getContext(), month);
    }

    public static String getMonthShortName(Context context, int month) {
        return context.getResources().getStringArray(R.array.months)[month - 1];
    }

    public static String getDayInMonth(int day){
        return String.format("%02d", day);
    }
}
