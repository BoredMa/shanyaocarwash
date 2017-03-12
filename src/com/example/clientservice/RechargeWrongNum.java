package com.example.clientservice;

import com.example.shanyaocarwash.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class RechargeWrongNum extends Activity{

	private Button service_bt;
	
	private TextView title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.recharge_unarrive);
		init();
	}

	private void init() {
		service_bt = (Button) findViewById(R.id.title_service);
		service_bt.setVisibility(View.GONE);
		
		title = (TextView) findViewById(R.id.title_text);
		title.setText("充值客服联系...");
	}

}
