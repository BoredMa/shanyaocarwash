package com.example.titlelayout;

import com.example.shanyaocarwash.R;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TitleLayout extends LinearLayout {

	private Context mContext;
	
	private Activity mActivity;
	
	private Button  titleBack;
	public TitleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = getContext();
		mActivity = (Activity)mContext;
		LayoutInflater.from(context).inflate(R.layout.titlebar, this);
		titleBack = (Button) findViewById(R.id.title_back);
		titleBack.setText("<返回");
		setBackClick();
		setBackTouch();
	}

	private void setBackTouch() {
		titleBack.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					titleBack.setTextColor(getResources().getColor(R.color.gray));
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					titleBack.setTextColor(getResources().getColor(R.color.white));
				}
				return false;
			}
		});
		
	}

	private void setBackClick() {
		titleBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mActivity.finish();
			}
		});
		
	}

}
