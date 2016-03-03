package com.example.obroshi.alarmclock.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Lital Noa on 29/01/2016.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private final static String TAG = RecyclerItemClickListener.class.getSimpleName();

    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;

    public static interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onItemLongClick(View view, int position);
    }

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public boolean onSingleTapUp(MotionEvent e) {
                View chileView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (chileView != null && mListener != null) {
                    mListener.onItemClick(chileView, recyclerView.getChildAdapterPosition(chileView));
                }
                return true;
            }

            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress was detected");
                View chileView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (chileView != null && mListener != null) {
                    mListener.onItemLongClick(chileView, recyclerView.getChildAdapterPosition(chileView));
                }
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        View chileView = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (chileView != null && mListener != null && mGestureDetector.onTouchEvent(e))
            if (e.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                mListener.onItemClick(chileView, recyclerView.getChildAdapterPosition(chileView));
            }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
