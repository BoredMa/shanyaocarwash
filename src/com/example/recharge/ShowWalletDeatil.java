package com.example.recharge;

import com.example.shanyaocarwash.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class ShowWalletDeatil extends Activity{

	private TextView washedNum;
	private TextView remainingNum;
	
	private int washed_num = 0;
	private int remaining_num = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.showwalletdetail);
		initAccount();
		initView();
	}

	private void initAccount() {
		washed_num = getIntent().getIntExtra("washed_num", 0);
		remaining_num = getIntent().getIntExtra("remaining_num", 0);
	}

	private void initView() {
		washedNum = (TextView) findViewById(R.id.washed_num);
		remainingNum = (TextView) findViewById(R.id.remaining_num); 
		
		washedNum.setText(""+washed_num);
		remainingNum.setText(""+remaining_num);
	}
	
}
