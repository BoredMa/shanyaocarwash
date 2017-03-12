package com.example.bottommenu;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.addmycar.AddCarActivity;
import com.example.shanyaocarwash.R;

public class ShowBottomMenu {
	
	private BottomMenu menuWindow;
	
	private  OnClickListener  clickListener;
	
	private Activity mActivity;
	
	public void showButtonMenu(Context context,String event) {
		mActivity = (Activity) context;
		menuWindow = new BottomMenu(mActivity, clickListener,event);  
	    menuWindow.show(); 	
	}
}
