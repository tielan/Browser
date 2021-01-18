package com.chinacreator.browser.detector;

import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.Calendar;

public class MultiTouchDetector implements GestureDetector.OnGestureListener {

    boolean[] hasBegun = new boolean[3];
    FingerThread[] fingerThread = new FingerThread[3];
    int fingersDown = 0;
    TapThread tThread;
    GestureDetector gestureDetector;
    MultiTouchListener listener;

    @SuppressWarnings("deprecation")
    public MultiTouchDetector(MultiTouchListener listener) {
        gestureDetector = new GestureDetector(this);
        this.listener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int numFingers = event.getPointerCount();
        int fingerIndex = numFingers - 1;
        switch (event.getActionIndex()) {
            case (MotionEvent.ACTION_UP):

            case (MotionEvent.ACTION_DOWN):
                FingerThread currentThread = fingerThread[fingerIndex];
                if (currentThread != null) {
                    currentThread.setStartTime(Calendar.getInstance().getTimeInMillis());
                }
                if (!hasFingersDown()) {
                    scrollIndex = 0;
                    tThread = new TapThread(numFingers);
                    tThread.start();
                } else if (tThread != null) {
                    if (numFingers > tThread.getNumFingers()) {
                        tThread.setNumFingers(numFingers);
                    }
                }

                if (!hasBegun[fingerIndex]) {
                    hasBegun[fingerIndex] = true;
                    if (numFingers > fingersDown) {
                        fingersDown = numFingers;
                    }
                    currentThread = new FingerThread(numFingers);
                    currentThread.start();
                }

                //UNIMPLEMENTED
            case (MotionEvent.ACTION_MOVE):
            case (MotionEvent.ACTION_OUTSIDE):
            case (MotionEvent.ACTION_POINTER_DOWN):
            case (MotionEvent.ACTION_POINTER_UP):
            case (MotionEvent.ACTION_SCROLL):

        }
       return gestureDetector.onTouchEvent(event);

    }

    public synchronized boolean hasFingersDown() {
        for (boolean b : hasBegun) {
            if (b) return true;
        }
        return false;
    }


    private class FingerThread extends Thread {
        private final long TIMEOUT = 500L;
        private long startTime;
        private int numFingers;

        public FingerThread(int numFingers) {
            scrollIndex = 0;
            this.numFingers = numFingers;
            startTime = Calendar.getInstance().getTimeInMillis();
        }

        public void setStartTime(long newTime) {
            startTime = newTime;
        }

        @Override
        synchronized public void run() {

            while (Calendar.getInstance().getTimeInMillis() - startTime < TIMEOUT) {
                //SPIN
            }

            int fingersIndex = numFingers - 1;
            hasBegun[fingersIndex] = false;

            for (int i = 0; i < 3; i++) {
                if (hasBegun[2 - i]) {
                    fingersDown = 2 - i + 1;
                    break;
                }

                fingersDown = 0;
            }

        }
    }

    boolean notScrolling = true;

    private class TapThread extends Thread {

        private final long TIMEOUT = 700L;
        private final long NOFINGERTIMEOUT = 300L;
        private long startTime;
        private int numFingers;
        private long timeRunning = 0;

        public TapThread(int numFingers) {
            this.numFingers = numFingers;
            startTime = Calendar.getInstance().getTimeInMillis();
            notScrolling = true;
        }

        public void setNumFingers(int newNumFingers) {
            this.numFingers = newNumFingers;
        }

        public int getNumFingers() {
            return numFingers;
        }


        @Override
        synchronized public void run() {
            //WHILE THERE ARE FINGERS AND WE HAVEN'T TIMED OUT
            while (hasFingersDown() && Calendar.getInstance().getTimeInMillis() - startTime < this.TIMEOUT) {
                timeRunning = Calendar.getInstance().getTimeInMillis() - startTime; //SPIN
            }
            //IF THE FINGERS ARE LIFTED BEFORE TIMEOUT, TAP
            if (!hasFingersDown()) {
                long noFingersStart = Calendar.getInstance().getTimeInMillis();
                while (Calendar.getInstance().getTimeInMillis() - noFingersStart < NOFINGERTIMEOUT) {
                    if (hasFingersDown()) {
                        return;
                    }

                }
            }
            //MAKE SURE FINGERS STAY OFF FOR A FEW MILIS
            if (!hasFingersDown()) {
                if (notScrolling) {
                    listener.onTapUp(numFingers);
                }
                scrollIndex = 0;
            }


        }
    }


    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    int scrollIndex = 0;

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        final float THRESHOLD = 15;
        final int TIMESTOSCROLL = 5;

        if (Math.abs(distanceX) > THRESHOLD) {
            scrollIndex++;
            if (scrollIndex > TIMESTOSCROLL) {
                notScrolling = false;
                listener.onScroll(e1, e2, distanceX, distanceY, fingersDown);
            }
        }

        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }
}
