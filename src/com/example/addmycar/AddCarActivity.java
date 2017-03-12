package com.example.addmycar;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.bottommenu.BottomMenu;
import com.example.bottommenu.ShowBottomMenu;
import com.example.recharge.Recharge;
import com.example.shanyaocarwash.R;
import com.example.web.WebServicePost;
import com.wyw.smsdemo.JsonReqClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.DocumentsContract.Document;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.SyncStateContract.Constants;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddCarActivity extends Activity {


	private Button service_bt;
	
	private Button ensure_addcar;
	
	private ImageView take_photo;
	
	private TextView titleText;
	
	private EditText plateNum;
	private EditText brand;
	private EditText color;
	
	private static String picName = "";
	private String imagePath;
	
	private ProgressDialog progressDialog=null;

	
	private static final int UPLOAD_SUCCESS = 1;
	private static final int UPLOAD_FAILED = 2;
	
	protected static final int GET_MODIFY_SIG = 0;
	
	private String imageUrl = "";
	private String path = "";
	private String RequestPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/ExecuteEvents";
	
	public static String phoneNum;
	private String carid;
	
	Map<String, String> params = new HashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addmycar);
		initView();
		initData();
		EnableButton();
		setEnsureOnTouch();
		setOnClick();
	}
	
	private void initData() {
		Intent intent = getIntent();
		phoneNum = intent.getStringExtra("phone");
		carid = intent.getStringExtra("carid");
	}

	private void EnableButton() {
		//监听车牌Edittext,如果车辆信息都填了，把确认按钮激活
		plateNum.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(plateNum.getText().toString().length() >0 && brand.getText().toString().length()>0
						&& color.getText().toString().length() >0){
					ensure_addcar.setEnabled(true);
					ensure_addcar.setBackgroundResource(R.drawable.recharge_conf_normal);
				}else{
					ensure_addcar.setEnabled(false);
					ensure_addcar.setBackgroundResource(R.drawable.recharge_conf);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		brand.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(plateNum.getText().toString().length() >0 && brand.getText().toString().length()>0
						&& color.getText().toString().length() >0){
					ensure_addcar.setEnabled(true);
					ensure_addcar.setBackgroundResource(R.drawable.recharge_conf_normal);
				}else{
					ensure_addcar.setEnabled(false);
					ensure_addcar.setBackgroundResource(R.drawable.recharge_conf);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		color.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(plateNum.getText().toString().length() >0 && brand.getText().toString().length()>0
						&& color.getText().toString().length() >0){
					ensure_addcar.setEnabled(true);
					ensure_addcar.setBackgroundResource(R.drawable.recharge_conf_normal);
				}else{
					ensure_addcar.setEnabled(false);
					ensure_addcar.setBackgroundResource(R.drawable.recharge_conf);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case BottomMenu.TAKE_PHOTO:
			if(resultCode == RESULT_OK){
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(BottomMenu.imageUri, "image/*");
				intent.putExtra("scale", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, BottomMenu.imageUri);
				startActivityForResult(intent, BottomMenu.CROP_PHOTO);
			}
			break;
			
		case BottomMenu.CROP_PHOTO:
			if(resultCode == RESULT_OK){
				try{
					imagePath = BottomMenu.imageUri.toString().substring("file://".length());
					Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(BottomMenu.imageUri));
					BottomMenu.picture.setImageBitmap(bitmap);
				}catch(FileNotFoundException e){
					e.printStackTrace();
				}
			}
			break;
			
		case BottomMenu.CHOOSE_PHOTO:
			if(resultCode == RESULT_OK){
				//判断手机版本号
				if(Build.VERSION.SDK_INT >= 19){
					handleImageOnKitKat(data);
				}else{
					handleImageBeforeKitKat(data);
				}
			}
			break;	
			
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void handleImageBeforeKitKat(Intent data) {
		Uri uri = data.getData();
		imagePath = getImagePath(uri,null);
		displayImage(imagePath);
		
	}

	private void displayImage(String imagePath) {
		if(imagePath != null){
			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
			BottomMenu.picture.setImageBitmap(bitmap);
		}else{
			Toast.makeText(this, "failed to get image", Toast.LENGTH_LONG);
		}
		
	}

	private String getImagePath(Uri uri, String selection) {
		String path = null;
		Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
		if(cursor != null){
			if(cursor.moveToFirst()){
				path = cursor.getString(cursor.getColumnIndex(Media.DATA));
			}
			cursor.close();
		}
		return path;
	}

	@SuppressLint("NewApi")
	private void handleImageOnKitKat(Intent data) {
		imagePath = null;
		Uri uri = data.getData();
		if(DocumentsContract.isDocumentUri(this, uri)){
			String docId = DocumentsContract.getDocumentId(uri);
			if("com.android.providers.media.documents".equals(uri.getAuthority())){
				String id = docId.split(":")[1];
				String selection = MediaStore.Images.Media._ID+"="+id;
				imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
			}else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
				imagePath = getImagePath(contentUri,null);
			}
		}else if("content".equalsIgnoreCase(uri.getScheme())){
			imagePath = getImagePath(uri,null);
		}
		displayImage(imagePath);
	}

	private void setOnClick() {
		ensureBtOnclick();
		takePhotoOnclick();
	}

	private void takePhotoOnclick() {
		take_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowBottomMenu showBM = new ShowBottomMenu();
				showBM.showButtonMenu(AddCarActivity.this,"addcar");
			}
		});
		
	}

	

	private void ensureBtOnclick() {
		ensure_addcar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View av) {
				
				picName = plateNum.getText().toString();
				
				//先new出一个监听器，设置好监听  
		        DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){  
		  		           

					@Override  
		            public void onClick(DialogInterface dialog, int which) {  
		                switch(which){  
		                    case Dialog.BUTTON_POSITIVE:  
		                    	//确认车牌不为空
		                    	String pn = plateNum.getText().toString();
		                    	if(pn.length() <= 0){
		                    		Toast.makeText(AddCarActivity.this, "车牌不能为空", Toast.LENGTH_SHORT).show();
		                    		break;
		                    	}		                    	                    			                    	                    	
		                    	uploadPic();		                    	
		                        break;  
		                    case Dialog.BUTTON_NEGATIVE:  
		                          dialog.dismiss();
		                        break;    
		                }  
		            }


					private void uploadPic() {
						progressDialog = new ProgressDialog(getApplicationContext());
						progressDialog.setTitle("正在上传图片，请稍后...");
						new Thread(new Runnable() {							
							String result = "";
							@Override
							public void run() {
									result = ServerUtils.formUpload(Const.UPLOAD_URL, imagePath);
									Log.e("jj", "result:"+result);
									Message message = new Message();
									if(result.contains("success")){										
										message.what = UPLOAD_SUCCESS;
										message.obj = result;
										handler.sendMessage(message);
									}else{
										message.what = UPLOAD_FAILED;
										message.obj = result;
										handler.sendMessage(message);
									}
							}
						}).start();
					}  
		        };  
		        //dialog参数设置  
		        AlertDialog.Builder builder=new AlertDialog.Builder(AddCarActivity.this);  //先得到构造器
		        builder.setTitle("提示"); //设置标题  
		        builder.setMessage("是否确认添加车辆?"); //设置内容  
		        builder.setPositiveButton("确定",dialogOnclicListener);  
		        builder.setNegativeButton("取消", dialogOnclicListener);   
		        builder.create().show();  
			}
		});	
	}

	
  

	

	private void setEnsureOnTouch() {
		ensure_addcar.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					ensure_addcar.setBackgroundResource(R.drawable.rc_btn_voice_hover);
					ensure_addcar.setTextColor(getResources().getColor(R.color.lightgreen));
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					ensure_addcar.setBackgroundResource(R.drawable.recharge_conf_normal);
					ensure_addcar.setTextColor(getResources().getColor(R.color.white));
				}
				return false;
			}
		});	
	}

	private void initView() {
		service_bt = (Button) findViewById(R.id.title_service);
		service_bt.setVisibility(View.INVISIBLE);
		ensure_addcar = (Button) findViewById(R.id.ensure_addcar);
		take_photo = (ImageView) findViewById(R.id.addcar_image);
		
		titleText = (TextView) findViewById(R.id.title_text);
		titleText.setText("添加我的爱车");
		
		plateNum = (EditText) findViewById(R.id.addcar_content1_edit);
		brand = (EditText) findViewById(R.id.addcar_content2_edit);
		color = (EditText) findViewById(R.id.addcar_content3_edit);
	}

	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressDialog.dismiss();
			switch (msg.what) {
			case UPLOAD_SUCCESS:
				Toast.makeText(getApplicationContext(), "上传图片成功", Toast.LENGTH_SHORT).show();
//				imageUrl = "'"+Const.DOWNLOAD_URL+((String)msg.obj).substring("success".length())+"'"; //这里把字符串变为绝对字符串
				imageUrl = Const.DOWNLOAD_URL+((String)msg.obj).substring("success".length());
				sendReqAddCarDataBase();
				Log.e("jj",imageUrl);
				break;
			case UPLOAD_FAILED:
				Toast.makeText(getApplicationContext(), "上传图片失败,请检查图片名称格式是否正确，不能包含特殊字符", Toast.LENGTH_LONG).show();
				break;
			case GET_MODIFY_SIG:
				if(msg.obj.equals("modify success")){
					finish();
					Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
				}else if(msg.obj.equals("modify failed")){
					Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
			
		}
	};

		private void sendReqAddCarDataBase() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String result;
					try {
	//修改这里
						params.put("type", "modifydatabase");
						params.put("method", "addcar");
						params.put("carid", carid);
						params.put("imageurl", imageUrl);
						params.put("platenum", plateNum.getText().toString());
						params.put("brand", brand.getText().toString());
						params.put("color", color.getText().toString());
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
	
}
