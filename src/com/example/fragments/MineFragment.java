package com.example.fragments;

import java.io.BufferedWriter;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;

import org.xmlpull.v1.XmlSerializer;

import com.example.fragments.MyScrollView;
import com.example.addmycar.Const;
import com.example.bean.LogMessage;
import com.example.bean.Logs;
import com.example.bean.User;
import com.example.recharge.Recharge;
import com.example.shanyaocarwash.MainActivity;
import com.example.shanyaocarwash.R;
import com.example.web.WebServicePost;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MineFragment extends Fragment implements OnTouchListener, OnGestureListener{

	private Button recharge;
	
	private Button rechargeCard;
	
	private int card_price = 0;
	private int washed_num = 0;
	private int remaining_num = 0;
	private int remainings = 0;
	private String userPhone;
	
	private TextView cardPrice;
	private TextView washedNum;
	private TextView remainingNum;
	private TextView remainingMon;
	
	private ImageView show_pic1;
	private ImageView show_pic2;
	private ImageView show_pic3;
	private ImageView show_pic4;
	
	Map<String, String> params = new HashMap<String, String>();
	
	Map<String, String> logMap = new HashMap<String, String>();
	
	private String RequestPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/ExecuteEvents";
	
	private String filePath;
	private String path;
	
	private Logs logs;
	
	private ViewFlipper viewFlipper;
	private boolean showNext = true;
	private boolean isRun = true;
	private int currentPage = 0;
	private final int SHOW_NEXT = 0011;
	private static final int FLING_MIN_DISTANCE = 50;
	private static final int FLING_MIN_VELOCITY = 0;
	private GestureDetector mGestureDetector;
	
	private float x;
	private float y;
	
	private View mView;
	
	private String fragmentExist = null;
	
	FinalBitmap fb=null;
	
	protected static final int GET_MODIFY_SIG = 0;
	
	public static TextView mLocation;
	
	
	
	private String provider;
	
    public MineFragment(User user) {
    	card_price = user.getCard_price();
    	washed_num = user.getWashed_num();
    	remaining_num = user.getRemaining_num();
    	remainings = user.getRemainings();
    	userPhone = user.getPhone();
    	filePath = userPhone+".xml";
    	
    	
    	fragmentExist = "exist";
	}

	public MineFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1, container, false);
        initView(view);
        initAccount();
        reCharge(view);
        reChargeCard(view);
        return view;
    }

	private void initAccount() {
		cardPrice.setText(""+card_price+"元");
		washedNum.setText(""+washed_num);
		remainingNum.setText(""+remaining_num);
		remainingMon.setText(""+remainings);
	}

	@SuppressWarnings("deprecation")
	private void initView(View v) {
		cardPrice = (TextView) v.findViewById(R.id.card_price); 
		washedNum = (TextView) v.findViewById(R.id.washed_num);
		remainingNum = (TextView) v.findViewById(R.id.remaining_num); 
		remainingMon = (TextView) v.findViewById(R.id.remainings);
		
		
		//设置滑动图片
		 	viewFlipper = (ViewFlipper) v.findViewById(R.id.mViewFliper_vf);
	        mGestureDetector = new GestureDetector(this);	   
	        viewFlipper.setOnTouchListener(onTouchListener);
	        viewFlipper.setLongClickable(true);
	        viewFlipper.setOnClickListener(clickListener);	        
	        mView = v;
	        displayRatio_selelct(mView,currentPage);
	        
	        MyScrollView myScrollView = (MyScrollView) v.findViewById(R.id.viewflipper_scrollview);
	        myScrollView.setOnTouchListener(onTouchListener);
	        myScrollView.setGestureDetector(mGestureDetector);
 
//	        Timer timer = new Timer();
//			new Thread(timer).start();
	        //此处动态修改viewflipper的图片
	        fb=FinalBitmap.create(getActivity());
	        show_pic1 = (ImageView) v.findViewById(R.id.show_pic_1);
	        show_pic2 = (ImageView) v.findViewById(R.id.show_pic_2);
	        show_pic3 = (ImageView) v.findViewById(R.id.show_pic_3);
	        show_pic4 = (ImageView) v.findViewById(R.id.show_pic_4);
	        fb.display(show_pic1, Const.URL_PREFIX+"/pushPic/1.png");
	        fb.display(show_pic2, Const.URL_PREFIX+"/pushPic/2.png");
	        fb.display(show_pic3, Const.URL_PREFIX+"/pushPic/3.png");
	        fb.display(show_pic4, Const.URL_PREFIX+"/pushPic/4.png");
	        
	        
	        //此处显示天气情况，目前是先显示位置信息
	        mLocation = (TextView) v.findViewById(R.id.show_location);
	        
	        //在线程中更新天气
	        Weather weather = new Weather();
			new Thread(weather).start();
	        
	       
	}

	 private void displayRatio_selelct(View v,int id){
		int[] ratioId = {R.id.home_ratio_img_04, R.id.home_ratio_img_03, R.id.home_ratio_img_02, R.id.home_ratio_img_01};
		ImageView img = (ImageView)v.findViewById(ratioId[id]);
		img.setSelected(true);
	}

	 private void displayRatio_normal(View v,int id){
			int[] ratioId = {R.id.home_ratio_img_04, R.id.home_ratio_img_03, R.id.home_ratio_img_02, R.id.home_ratio_img_01};
			ImageView img = (ImageView)v.findViewById(ratioId[id]);
			img.setSelected(false);
		}
	 
	private OnClickListener clickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "点击", Toast.LENGTH_SHORT).show();
			}
		};
	 private OnTouchListener onTouchListener = new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = MotionEventCompat.getActionMasked(event);
				return mGestureDetector.onTouchEvent(event);
			}
		};	
//    private OnTouchListener onTouchListener = new OnTouchListener() {
//		
//		@Override
//		public boolean onTouch(View v, MotionEvent event) {
//			switch(event.getAction())
//			{
//				case MotionEvent.ACTION_DOWN:// 手指按下
//				{
//					x = event.getX();// 全局变量，接收按下是的手指坐标
//				}
//					break;
//				case MotionEvent.ACTION_UP:// 手指松开
//				{
//					y = event.getX();// 全局变量，接收松开是的手指坐标
//					// 下面就是简单的逻辑判断，从而区分向左滑、向右滑以及不滑（也就是点击事件）
//					if (y > x) {
//						Log.v(null, "result:y>x");
//						showPreviousView();
//	
//					} else if(y < x){
//						Log.v(null, "result:x>y");
//						showNextView();
//					}
//				}
//					break;
//				}              
//	            return true;
//			}
//	};
	
	
	private void reCharge(View view) {
		recharge = (Button) view.findViewById(R.id.recharge);
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
				Intent intent = new Intent(getActivity(),Recharge.class);
				intent.putExtra("phone", userPhone);
				startActivity(intent);	
			}
		});
	}
	
	private void reChargeCard(View view) {
		rechargeCard = (Button) view.findViewById(R.id.recharge_card);
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
		        };  
		        //dialog属性设置
		        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
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
					Toast.makeText(getActivity(), "操作成功", Toast.LENGTH_SHORT).show();
					logs = LogMessage.packageMessage("rechargeCard","","","");					
					Logs.writeLogs(logs, filePath);
					
				}else if(msg.obj.equals("modify failed")){
					Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
	
	
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		Log.e("view", "onFling");
		if (e1.getX() - e2.getX()> FLING_MIN_DISTANCE  
                && Math.abs(velocityX) > FLING_MIN_VELOCITY ) {
			Log.e("fling", "left");
			showNextView();
			showNext = true;
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE  
                && Math.abs(velocityX) > FLING_MIN_VELOCITY){
			Log.e("fling", "right");
			showPreviousView();
			showNext = false;
		}
		return true;
	}
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
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
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return mGestureDetector.onTouchEvent(event);
	}

	class Timer implements Runnable {
		
		@Override
		public void run() {
			while(fragmentExist != null){
				try {
					Thread.sleep(1000 * 8);
					Message msg = new Message();
					msg.what = SHOW_NEXT;
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	private void showNextView(){

		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out));		
		viewFlipper.showNext();
		currentPage ++;
		if (currentPage == viewFlipper.getChildCount()) {
			displayRatio_normal(mView,currentPage - 1);
			currentPage = 0;
			displayRatio_selelct(mView,currentPage);
		} else {
			displayRatio_selelct(mView,currentPage);
			displayRatio_normal(mView,currentPage - 1);
		}
		Log.e("currentPage", currentPage + "");		
		
	}
	private void showPreviousView(){
		displayRatio_selelct(mView,currentPage);
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_out));
		viewFlipper.showPrevious();
		currentPage --;
		if (currentPage == -1) {
			displayRatio_normal(mView,currentPage + 1);
			currentPage = viewFlipper.getChildCount() - 1;
			displayRatio_selelct(mView,currentPage);
		} else {
			displayRatio_selelct(mView,currentPage);
			displayRatio_normal(mView,currentPage + 1);
		}
		Log.e("currentPage", currentPage + "");		
	}
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SHOW_NEXT:
				if (showNext) {
					showNextView();
				} else {
					showPreviousView();
				}
				break;

			default:
				break;
			}
		}
    	
    };

    class Weather implements Runnable {
		
		@Override
		public void run() {
				try {
					List<String> providerList = MainActivity.locationManager.getProviders(true);
			        if(providerList.contains(LocationManager.GPS_PROVIDER)){
			        	provider = LocationManager.GPS_PROVIDER;
			        }else if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
			        	provider = LocationManager.NETWORK_PROVIDER;
			        }else{
			        	Toast.makeText(getActivity(), "当前不可定位", Toast.LENGTH_SHORT).show();	        	
			        }
			        if(provider != null){
			        	Location location = MainActivity.locationManager.getLastKnownLocation(provider);
			        	if(location != null){
			        		MainActivity.showLocation(location);
			        	}
			        	MainActivity.locationManager.requestLocationUpdates(provider, 5000, 3, MainActivity.locationListener);
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}		
	}
    
}
