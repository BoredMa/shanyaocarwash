package com.example.recharge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.bean.LogMessage;
import com.example.bean.Logs;
import com.example.bean.User;
import com.example.clientservice.RechargeService;
import com.example.login.Login;
import com.example.shanyaocarwash.MainActivity;
import com.example.shanyaocarwash.R;
import com.example.web.WebServicePost;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class Recharge extends Activity {

	protected static final int GET_MODIFY_SIG = 0;

	protected String modifyPath = "http://" + WebServicePost.IP + "/WirelessOrder/ModifyDatabase";
	
	private String RequestPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/ExecuteEvents";
	
	private Button back_bt;
	
	private TextView title;
	
	private Button service_bt;
	
	private TextView show_pr;
	
	private GridView mGridView;
	
	private Button recharge_conf;
	//private int[] images = {R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.l,R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.l};
//	private Button[] buttons = new Button[8];
	private String[] prices = {"50","100","150","200"};
//	private String[] descs = {"完善信息","20分","查看状态","(获得积分)","50.00","获得更多积分","改善用户体验","及时解决您的问题"};
	private List<String> mList = new ArrayList<>();
	
	private EditText chargeAccount;
	
	private String accountNum;
	
	private String clickedPrice;
	
	private Logs logs;
	
	private String userPhone;
	
	private String filePath;
	
	Map<String, String> params = new HashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initView();
		initEvent();
		initData();
		buttonClickListener();
		buttonTouchListener();
		mGridView.setAdapter(new GridViewAdapter(this, mList));
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				clickedPrice = mList.get(position);
				show_pr.setText(clickedPrice+"元");
			}
		});
	}

	private void initEvent() {
		title = (TextView) findViewById(R.id.title_text);
		title.setText("充值中心");
		service_bt = (Button) findViewById(R.id.title_service);
		show_pr = (TextView) findViewById(R.id.show_price);
		recharge_conf = (Button) findViewById(R.id.recharge_confirm);
		
		chargeAccount = (EditText) findViewById(R.id.charge_account);
	}

	private void buttonTouchListener() {
		setServiceTouch();	
		setRechargeCofTouch();
	}

	private void setRechargeCofTouch() {
		recharge_conf.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					recharge_conf.setTextColor(getResources().getColor(R.color.gray));
					recharge_conf.setBackgroundResource(R.drawable.recharge_conf);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					recharge_conf.setTextColor(getResources().getColor(R.color.white));
					recharge_conf.setBackgroundResource(R.drawable.recharge_conf_normal);
				}
				return false;
			}
		});	
	}

	private void setServiceTouch() {
		service_bt.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					service_bt.setTextColor(getResources().getColor(R.color.gray));
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					service_bt.setTextColor(getResources().getColor(R.color.white));
				}
				return false;
			}
		});
		
	}



	private void buttonClickListener() {
		initButtonEnable();
		setServiceEvent();
		setConfirmEvent();
	}

	private void initButtonEnable() {
		chargeAccount.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (Login.isMobileNO(chargeAccount.getText().toString())) {
					recharge_conf.setEnabled(true);
				} else {
					recharge_conf.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void setConfirmEvent() {
		recharge_conf.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				accountNum = chargeAccount.getText().toString();
				ensureNum();		
			}
		});		
	}

	protected void ensureNum() {
		//先new出一个监听器，设置好监听  
        DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){  
  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                switch(which){  
                    case Dialog.BUTTON_POSITIVE:  
                    	sendReqRechargeDataBase(); 
                        break;  
                    case Dialog.BUTTON_NEGATIVE:  
                          dialog.dismiss();
                        break;    
                }  
            }  
        };  
        //dialog参数设置  
        AlertDialog.Builder builder=new AlertDialog.Builder(Recharge.this);  //先得到构造器
        builder.setTitle("提示"); //设置标题  
        builder.setMessage("确认给号码"+ accountNum+"充值？"); //设置内容  
        builder.setPositiveButton("确认",dialogOnclicListener);  
        builder.setNegativeButton("取消", dialogOnclicListener);   
        builder.create().show(); 
		
	}

	protected void sendReqRechargeDataBase() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String result;
				try {
//修改这里
					params.put("type", "modifydatabase");
					params.put("method", "recharge");
					params.put("money", clickedPrice);
					params.put("account_num", accountNum);
					result = WebServicePost.sendPOSTRequest(RequestPath, params, "utf-8");
					Message msg = new Message();
	                msg.what = GET_MODIFY_SIG;
	                msg.obj = result;
	                handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();		
	}

	private void setServiceEvent() {
		service_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Recharge.this,RechargeService.class);
				startActivity(intent);
			}
		});
	}


	private void initData() {
		mList.clear();
		for (int i = 0; i < prices.length; i++) {
//			Map<String, Object> map = new HashMap<>();
//			map.put("titles", titles[i]);
//			map.put("descs", descs[i]);
			mList.add(prices[i]);
		}
	}

	private void initView() {
		setContentView(R.layout.prices_grid);
		mGridView = (GridView) findViewById(R.id.gridview);
		
		Intent intent = getIntent();
		userPhone = intent.getStringExtra("phone");
		filePath = userPhone +".xml";
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_MODIFY_SIG:
				if(msg.obj.equals("modify success")){
					finish();
					Toast.makeText(Recharge.this, "充值成功", Toast.LENGTH_SHORT).show();
					logs = LogMessage.packageMessage("recharge",clickedPrice,accountNum,"");					
					Logs.writeLogs(logs, filePath);
				}else if(msg.obj.equals("modify failed")){
					Toast.makeText(Recharge.this, "充值失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
}