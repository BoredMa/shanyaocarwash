package com.example.fragments;

import java.util.HashMap;
import java.util.Map;

import com.example.addmycar.AddCarActivity;
import com.example.addmycar.Const;
import com.example.bean.LogMessage;
import com.example.bean.Logs;
import com.example.bean.User;
import com.example.shanyaocarwash.R;
import com.example.web.WebServicePost;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FrcarFragment extends Fragment{

	private Button help_fr_bt;
	
	private EditText plateNum;
	
	private String phoneNum;
	
	private Logs logs;
	
	private String filePath;
	
	Map<String, String> params = new HashMap<String, String>();
	
	protected static final int GET_MODIFY_SIG = 0;
	
    public FrcarFragment(User user) {
		phoneNum = user.getPhone();
		filePath = phoneNum + ".xml";
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3, container, false);
        initView(view);
        setOnBtTouch();
        setOnBtClick();
        return view;
    }

	private void setOnBtClick() {
		help_fr_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//先new出一个监听器，设置好监听  
		        DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){  
		  
		            @Override  
		            public void onClick(DialogInterface dialog, int which) {  
		                switch(which){  
		                    case Dialog.BUTTON_POSITIVE:  
		                    	//确认车牌不为空
		                    	String pn = plateNum.getText().toString();
		                    	if(pn.length() <= 0){
		                    		Toast.makeText(getActivity(), "车牌不能为空", Toast.LENGTH_SHORT).show();
		                    		break;
		                    	}
		                    	sendReqHelpFrDataBase(); 
		                        break;  
		                    case Dialog.BUTTON_NEGATIVE:  
		                          dialog.dismiss();
		                        break;    
		                }  
		            }

					private void sendReqHelpFrDataBase() {
						new Thread(new Runnable() {
							@Override
							public void run() {
								String result;
								try {
				//修改这里
									params.put("type", "modifydatabase");
									params.put("method", "helpfr");
									params.put("phone", phoneNum);
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
		        //dialog参数设置  
		        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());  //先得到构造器
		        builder.setTitle("提示"); //设置标题  
		        builder.setMessage("是否确认帮朋友买单?"); //设置内容  
		        builder.setPositiveButton("确定",dialogOnclicListener);  
		        builder.setNegativeButton("取消", dialogOnclicListener);   
		        builder.create().show();  
			}
		});
		
	}

	private void setOnBtTouch() {
		help_fr_bt.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					help_fr_bt.setBackgroundResource(R.drawable.recharge_conf);
					help_fr_bt.setTextColor(getResources().getColor(R.color.lightgreen));
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					help_fr_bt.setBackgroundResource(R.drawable.recharge_conf_normal);
					help_fr_bt.setTextColor(getResources().getColor(R.color.white));
				}
				return false;
			}
		});	
		
	}

	private void initView(View view) {
		help_fr_bt = (Button) view.findViewById(R.id.ensure_helpfr);
		plateNum = (EditText) view.findViewById(R.id.fr_content1_edit);
	}
	
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {			
			case GET_MODIFY_SIG:
				if(msg.obj.equals("modify success")){
					Toast.makeText(getActivity(), "买单成功", Toast.LENGTH_SHORT).show();
					logs = LogMessage.packageMessage("helpfr","","",plateNum.getText().toString());					
					Logs.writeLogs(logs, filePath);
				}else if(msg.obj.equals("modify failed")){
					Toast.makeText(getActivity(), "买单失败", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
			
		}
	};
}
