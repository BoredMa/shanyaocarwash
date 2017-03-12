package com.example.clientservice;

import java.util.ArrayList;
import java.util.List;

import com.example.recharge.Recharge;
import com.example.shanyaocarwash.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class RechargeService extends Activity{

	private List<String> problemList = new ArrayList<String>();
	
	private Button title_service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.problem_list_view);
		initView();
		initEvent();
		initData();
		ProblemAdapter proAdapter = new ProblemAdapter(RechargeService.this,R.layout.problem_item,problemList);
		ListView listView = (ListView) findViewById(R.id.problem_list_view);
		listView.setAdapter(proAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				switch(position){
					case 0:
						Intent intent1 = new Intent(RechargeService.this,RechargeServiceContact.class);
						startActivity(intent1);
						break;
					case 1:
						Intent intent2 = new Intent(RechargeService.this,RechargeUnarrive.class);
						startActivity(intent2);
						break;
					case 2:
						Intent intent3 = new Intent(RechargeService.this,RechargeWrongNum.class);
						startActivity(intent3);
						break;
					default:
						break;
				}
				
			}
		});
	}
	private void initEvent() {
				
	}
	
	private void initView() {
		title_service = (Button) findViewById(R.id.title_service);
		title_service.setVisibility(View.GONE);
	}
	private void initData() {
		String[] problems = new String[3];
		problems[0] = new String("充值客服联系方式");
		problems[1] = new String("充值金额未到账");
		problems[2] = new String("充错账号怎么办?");
		
		for(int i=0;i<problems.length;i++){
			problemList.add(problems[i]);
		}
	}

}
