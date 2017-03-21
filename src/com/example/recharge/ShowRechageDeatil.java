package com.example.recharge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.addmycar.Const;
import com.example.bean.LogMessage;
import com.example.bean.Logs;
import com.example.shanyaocarwash.R;
import com.example.web.WebServicePost;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowRechageDeatil extends Activity{

	private TextView cardPrice;
	private TextView remainingMon;
	
	private int card_price = 0;
	private int remainings = 0;
	
	private Button recharge;
	private Button rechargeCard;
	
	private Logs logs;
	
	private String filePath;
	
	private Context that;
	
	protected static final int GET_MODIFY_SIG = 0;
	
	Map<String, String> params = new HashMap<String, String>();
	
	private String userPhone;
	
	private GridView mGridView;
	
	private String[] prices = {"500","1000","1500","2000"};
	
	private List<String> priceList = new ArrayList<String>();
	
	private RelativeLayout cardPriceLayout;
	
	private int currentLocation = -1;
	private int cardprice;
	private int cardnum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.showrechargedetail);
		initAccount();
		initData();
		initView();		
		reCharge();
		reChargeCard();
	}


	private void initData() {
		priceList.clear();
		for (int i = 0; i < prices.length; i++) {
			priceList.add(prices[i]);
		}
	}


	private void initAccount() {
		userPhone = getIntent().getStringExtra("phone");
		card_price = getIntent().getIntExtra("card_price", 0);
		remainings = getIntent().getIntExtra("remainings", remainings);
		filePath = getIntent().getStringExtra("filepath");
	}


	private void initView() {
		that = ShowRechageDeatil.this;

		remainingMon = (TextView) findViewById(R.id.remainings);
		
		remainingMon.setText(""+remainings);
		
		mGridView = (GridView) findViewById(R.id.price_gridview);		
		mGridView.setAdapter(new PriceGridViewAdapter(this, priceList));
		
		
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.e("test", priceList.get(position));
				 for(int i=0;i<parent.getCount();i++){
		               View v=parent.getChildAt(i);
		               if (position == i) {//当前选中的Item改变背景颜色
		                view.setBackgroundResource(R.drawable.gridselectedstyle);
		                switch(position){
		                case 0:
		                	cardprice = 500;
		                	cardnum = 20;		                	
		                	break;
		                case 1:
		                	cardprice = 1000;
		                	cardnum = 40;		                	
		                	break;
		                case 2:
		                	cardprice = 1500;
		                	cardnum = 60;		                	
		                	break;
		                case 3:
		                	cardprice = 2000;
		                	cardnum = 80;		                	
		                	break;
		                }
		               } else {
		                  v.setBackgroundResource(R.color.white);
		               }
		           }	

			}
		});
	}
	


	private void reCharge() {
		recharge = (Button) findViewById(R.id.recharge);
		recharge.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					recharge.setBackgroundResource(R.drawable.rc_btn_voice_hover);
					recharge.setTextColor(getResources().getColor(R.color.lightgreen));
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					recharge.setBackgroundResource(R.drawable.rc_btn_voice_normal);
					recharge.setTextColor(getResources().getColor(R.color.green));
				}
				return false;
			}
		});
		recharge.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(that,Recharge.class);
				intent.putExtra("phone", userPhone);
				startActivity(intent);	
			}
		});
	}
	
	
	private void reChargeCard() {
		rechargeCard = (Button) findViewById(R.id.recharge_card);
		rechargeCard.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					rechargeCard.setBackgroundResource(R.drawable.rc_btn_voice_hover);
					rechargeCard.setTextColor(getResources().getColor(R.color.lightgreen));
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					rechargeCard.setBackgroundResource(R.drawable.rc_btn_voice_normal);
					rechargeCard.setTextColor(getResources().getColor(R.color.green));
				}
				return false;
			}
		});
		rechargeCard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ensuReCarddialog();		
			}

			private void ensuReCarddialog() {
				
		        //先new出一个dialog监听
		        DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){  
		  
		            @Override  
		            public void onClick(DialogInterface dialog, int which) {  
		                switch(which){  
		                    case Dialog.BUTTON_POSITIVE:   
		                        sendReqRechargeCardDataBase();
		                        break;  
		                    case Dialog.BUTTON_NEGATIVE:  
		                          dialog.dismiss();
		                        break;    
		                }  
		            }

					private void sendReqRechargeCardDataBase() {
						new Thread(new Runnable() {
							@Override
							public void run() {
								String result;
								try {
									params.put("type", "modifydatabase");
									params.put("method", "rechargecard");
									params.put("phone", userPhone);
									params.put("cardprice", cardprice+"");
									params.put("cardnum", cardnum+"");
									result = WebServicePost.sendPOSTRequest(Const.REQUEST_URL, params, "utf-8");
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
		        };  
		        //dialog属性设置
		        AlertDialog.Builder builder=new AlertDialog.Builder(that);
		        builder.setTitle("提示"); //设置标题  
		        builder.setMessage("是否确认冲卡?"); //设置信息  
		        builder.setPositiveButton("确认",dialogOnclicListener);  
		        builder.setNegativeButton("取消", dialogOnclicListener);   
		        builder.create().show();  
		    }  

	});
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_MODIFY_SIG:
				if(msg.obj.equals("modify success")){
					Toast.makeText(that, "操作成功", Toast.LENGTH_SHORT).show();
					logs = LogMessage.packageMessage("rechargeCard","","","");					
					Logs.writeLogs(logs, filePath);
					
				}else if(msg.obj.equals("modify failed")){
					Toast.makeText(that, "操作失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
	
}
