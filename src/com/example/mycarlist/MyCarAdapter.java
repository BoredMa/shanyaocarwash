package com.example.mycarlist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;

import com.example.addmycar.Const;
import com.example.bean.Car;
import com.example.bean.LogMessage;
import com.example.bean.Logs;
import com.example.shanyaocarwash.R;
import com.example.web.WebServicePost;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyCarAdapter extends ArrayAdapter<Car>{

	private int resourceId;
	
	private Context mContext;
	
	private Button ensure_bt;
	
	private String phoneNum;
	
	private String filePath;
	
	private String plateNum;
	
	private Logs logs;
	
	Map<String, String> params = new HashMap<String, String>();
	
	protected static final int GET_MODIFY_SIG = 0;
	
	FinalBitmap fb=null;
	
	public MyCarAdapter(Context context, int resource, List<Car> objects,String phone) {
		super(context, resource,objects);
		mContext = context;
		resourceId = resource;
		fb=FinalBitmap.create(context);
		phoneNum = phone;
		filePath = phoneNum + ".xml";
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Car car = getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			
			//添加确认洗车按钮
			ensureWashButton(view);
			
			viewHolder = new ViewHolder();
			viewHolder.image = (ImageView) view.findViewById(R.id.mycar_photo);
			viewHolder.brand = (TextView) view.findViewById(R.id.car_brand);
			viewHolder.plateNum = (TextView) view.findViewById(R.id.plate_num);
			viewHolder.color = (TextView) view.findViewById(R.id.car_color);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
//		viewHolder.image.setImageResource(car.getImageId());
		fb.display(viewHolder.image, car.getImageUrl());
		viewHolder.brand.setText(car.getBrand());
		plateNum = car.getPlateNum();
		viewHolder.plateNum.setText(plateNum);
		viewHolder.color.setText(car.getColor());

		return view;
	}

	private void ensureWashButton(final View view) {
		ensure_bt = (Button) view.findViewById(R.id.ensure_wash);
		
		ensure_bt.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					ensure_bt.setBackgroundResource(R.drawable.rc_btn_voice_hover);
					ensure_bt.setTextColor(v.getResources().getColor(R.color.lightgreen));
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					ensure_bt.setBackgroundResource(R.drawable.rc_btn_voice_normal);
					ensure_bt.setTextColor(v.getResources().getColor(R.color.green));
				}
				return false;
			}
		});
		ensure_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//先new出一个监听器，设置好监听  
		        DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){  
		  
		            @Override  
		            public void onClick(DialogInterface dialog, int which) {  
		                switch(which){  
		                    case Dialog.BUTTON_POSITIVE:  
		                    	sendReqWashMyCarDataBase();
		                    	break;		                    	
		                    case Dialog.BUTTON_NEGATIVE:  
		                          dialog.dismiss();
		                        break;    
		                }  
		            }

					private void sendReqWashMyCarDataBase() {
						new Thread(new Runnable() {
							@Override
							public void run() {
								String result;
								try {
				//修改这里  因为这里和帮朋友买单的网络请求一样，因此method设为helpfr
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
		        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);  //先得到构造器
		        builder.setTitle("提示"); //设置标题  
		        builder.setMessage("是否确认洗车?"); //设置内容  
		        builder.setPositiveButton("确定",dialogOnclicListener);  
		        builder.setNegativeButton("取消", dialogOnclicListener);   
		        builder.create().show(); 
			}
		});
	}

	class ViewHolder{
		ImageView image;
		TextView brand;
		TextView plateNum;
		TextView color;
		Button ensure;
	}
	
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {			
			case GET_MODIFY_SIG:
				if(msg.obj.equals("modify success")){
					Toast.makeText(mContext, "付账成功", Toast.LENGTH_SHORT).show();
					logs = LogMessage.packageMessage("washmycar","","",plateNum);					
					Logs.writeLogs(logs, filePath);
				}else if(msg.obj.equals("modify failed")){
					Toast.makeText(mContext, "付账失败", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
			
		}
	};
}
