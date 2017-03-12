package com.example.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

	GestureDetector gestureDetector;
	public MyScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void setGestureDetector(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		super.onTouchEvent(ev);
		return gestureDetector.onTouchEvent(ev);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
	     if(gestureDetector.onTouchEvent(event))
	     {
	            event.setAction(MotionEvent.ACTION_CANCEL);
	     }
	     return super.dispatchTouchEvent(event);
	}
	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev){
////		gestureDetector.onTouchEvent(ev);
////		super.dispatchTouchEvent(ev);
////		return true;
//		 if(gestureDetector.onTouchEvent(ev))
//	     {
//	            ev.setAction(MotionEvent.ACTION_CANCEL);
//	     }
//	     return super.dispatchTouchEvent(ev);
//	} 
	
}
