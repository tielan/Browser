package com.chinacreator.browser.detector;

import android.view.MotionEvent;

public interface MultiTouchListener {
	
	public void onTapUp(int numFingers);
	
	
	public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
						 float distanceY, int numFingers);
	
	/**
	 * USEFUL FOR GLASS TRACKPAD 
	 * if (distanceX > 0){
			Log.i("myGesture", "scrolling back with "+ fingersDown);
				}
		else{
			Log.i("myGesture", "scrolling forward with "+ fingersDown);
				}
		
	 */
	
}