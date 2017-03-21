package com.example.addmycar;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;

import com.example.bottommenu.BottomMenu;
import com.example.bottommenu.ShowBottomMenu;
import com.example.login.Login;
import com.example.shanyaocarwash.R;
import com.example.web.WebServicePost;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewDebug.IntToString;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditMyCar extends Activity {


	private Button service_bt;
	
	private Button ensure_bt;
	
	private Button delete_bt;
	
	private ImageView take_photo;
	
	private EditText plateNum;
	
	private EditText brand;
	
	private EditText color;
	
	private TextView titleText;
	
	private FinalBitmap fb = null;
	
	private String imagePath;
	
	private String newPath;
	
	private ProgressDialog progressDialog=null;
	
	String id;
	String carId;
	String iU ;
	String pn ;
	String br ;
	String co ;

	
	Map<String, String> params = new HashMap<String, String>();
	
	private String RequestPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/ExecuteEvents";
	
	protected static final int GET_MODIFY_SIG = 0;
	protected static final int GET_EDIT_SIG = 1;
	
	private static final int UPLOAD_SUCCESS = 2;
	private static final int UPLOAD_FAILED = 3;
	
	private String imageUrl = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.editmycar);
		initView();
		setEnsureOnTouch();
		setOnClick();
		setDefaultText();
	}
	
	private void setDefaultText() {
		//用FinalBitmap把图片直接加载出来
		fb = FinalBitmap.create(this);
		
		
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		carId = intent.getStringExtra("car_id");
		iU = intent.getStringExtra("car_image");
		pn = intent.getStringExtra("car_platenum");
	    br = intent.getStringExtra("car_brand");
		co = intent.getStringExtra("car_color");
		fb.display(take_photo, iU);
		plateNum.setText(pn);
		brand.setText(br);
		color.setText(co);
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
		deleteBtOnclick();
		takePhotoOnclick();
	}

	private void deleteBtOnclick() {
		delete_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View av) {
				
				//先new出一个监听器，设置好监听  
		        DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){  
		  
		            @Override  
		            public void onClick(DialogInterface dialog, int which) {  
		                switch(which){  
		                    case Dialog.BUTTON_POSITIVE:  
		                        deleteCar();
		                       
		                        break;  
		                    case Dialog.BUTTON_NEGATIVE:  
		                          dialog.dismiss();
		                        break;    
		                }  
		            }

					private void deleteCar() {
						progressDialog = new ProgressDialog(getApplicationContext());
						progressDialog.setTitle("正在删除车辆，请稍后...");
						new Thread(new Runnable() {
							@Override
							public void run() {
								String result;
								try {
				//修改这里
									params.put("type", "modifydatabase");
									params.put("method", "deletecar");
									params.put("id", id);
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
		        //dialog参数设置  
		        AlertDialog.Builder builder=new AlertDialog.Builder(EditMyCar.this);  //先得到构造器
		        builder.setTitle("提示"); //设置标题  
		        builder.setMessage("是否确认删除车辆?"); //设置内容  
		        builder.setPositiveButton("确定",dialogOnclicListener);  
		        builder.setNegativeButton("取消", dialogOnclicListener);   
		        builder.create().show();  
		    }
		});    
		
	}

	private void takePhotoOnclick() {
		take_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowBottomMenu showBM = new ShowBottomMenu();
				showBM.showButtonMenu(EditMyCar.this,"editcar");
			}
		});
		
	}

	

	private void ensureBtOnclick() {
		
		ensure_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View av) {
				
				//先new出一个监听器，设置好监听  
		        DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){  
		  
		            @Override  
		            public void onClick(DialogInterface dialog, int which) {  
		                switch(which){  
		                    case Dialog.BUTTON_POSITIVE:  
		                       if(isPicModified()){
		                    	   editCar();
		                       }else if(isDataModified() && Login.isPlateNo(pn)){
		                    	   editCarData();
		                       }else{
		                    	   Toast.makeText(EditMyCar.this, "修改失败，检查车牌等格式是否正确", Toast.LENGTH_SHORT).show();
		                       }  
		                        break;  
		                    case Dialog.BUTTON_NEGATIVE:  
		                          dialog.dismiss();
		                        break;    
		                }  
		            }
  

					protected void editCarData() {
						progressDialog = new ProgressDialog(getApplicationContext());
						progressDialog.setTitle("正在修改信息，请稍后...");
						new Thread(new Runnable() {
							@Override
							public void run() {
								String result;
								try {
				//修改这里
									params.put("type", "modifydatabase");
									params.put("method", "editcardata");
									params.put("id", id);
									params.put("platenum", pn);
									params.put("brand", br);
									params.put("color", co);
									result = WebServicePost.sendPOSTRequest(RequestPath, params, "utf-8");
									Message msg = new Message();
					                msg.what = GET_EDIT_SIG;
					                msg.obj = result;
					                handler.sendMessage(msg);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();	
					}


					private boolean isDataModified() {
						if(!plateNum.getText().toString().equals(pn) || !brand.getText().toString().equals(br) || !color.getText().toString().equals(co)){
							pn = plateNum.getText().toString();
							br = brand.getText().toString();
							co = color.getText().toString();
							return true;
						}else{
							return false;
						}
					}


					private boolean isPicModified() {
						newPath = iU;
						String[] oldPaths = iU.split("/");
						//如果车辆图片被修改了
						if(imagePath !=null){
							String[] newPaths = imagePath.split("/");
							newPath = Const.URL_PREFIX+"/upload/"+newPaths[newPaths.length-1];
							pn = plateNum.getText().toString();
							br = brand.getText().toString();
							co = color.getText().toString();
							return true;
						}else{
							return false;
						}						
						
					}

					private void editCar() {
						progressDialog = new ProgressDialog(getApplicationContext());
						progressDialog.setTitle("正在修改信息，请稍后...");
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
										handler.sendEmptyMessage(UPLOAD_FAILED);
									}
							}
						}).start();
					}  
		        };  
		        //dialog参数设置  
		        AlertDialog.Builder builder=new AlertDialog.Builder(EditMyCar.this);  //先得到构造器
		        builder.setTitle("提示"); //设置标题  
		        builder.setMessage("是否确认修改车辆信息?"); //设置内容  
		        builder.setPositiveButton("确定",dialogOnclicListener);  
		        builder.setNegativeButton("取消", dialogOnclicListener);   
		        builder.create().show();  
		    }
		});  

}
			
	


	private void setEnsureOnTouch() {
		ensure_bt.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					ensure_bt.setBackgroundResource(R.drawable.rc_btn_voice_hover);
					ensure_bt.setTextColor(getResources().getColor(R.color.lightgreen));
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					ensure_bt.setBackgroundResource(R.drawable.recharge_conf_normal);
					ensure_bt.setTextColor(getResources().getColor(R.color.white));
				}
				return false;
			}
		});	
		
		delete_bt.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					delete_bt.setBackgroundResource(R.drawable.rc_btn_voice_hover);
					delete_bt.setTextColor(getResources().getColor(R.color.white));
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					delete_bt.setBackgroundResource(R.color.line_gray_tran);
					delete_bt.setTextColor(getResources().getColor(R.color.black));
				}
				return false;
			}
		});	
	}

	private void initView() {
		service_bt = (Button) findViewById(R.id.title_service);
		service_bt.setVisibility(View.INVISIBLE);
		ensure_bt = (Button) findViewById(R.id.ensure_editmycar);
		delete_bt = (Button) findViewById(R.id.ensure_deletecar);
		take_photo = (ImageView) findViewById(R.id.edit_image);
		
		plateNum = (EditText) findViewById(R.id.edit_content1_edit);
		brand = (EditText) findViewById(R.id.edit_content2_edit);
		color = (EditText) findViewById(R.id.edit_content3_edit);
		
		titleText = (TextView) findViewById(R.id.title_text);
		titleText.setText("修改车辆信息");
	}

	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressDialog.dismiss();
			switch (msg.what) {
			case GET_MODIFY_SIG:
				if(msg.obj.equals("modify success")){
					finish();
					Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
				}else if(msg.obj.equals("modify failed")){
					Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
				}
				break;
			case GET_EDIT_SIG:
				if(msg.obj.equals("modify success")){
					finish();
					Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
				}else if(msg.obj.equals("modify failed")){
					Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
				}
				break;
			case UPLOAD_SUCCESS:
				Toast.makeText(getApplicationContext(), "上传图片成功", Toast.LENGTH_SHORT).show();
//				imageUrl = "'"+Const.DOWNLOAD_URL+((String)msg.obj).substring("success".length())+"'"; //这里把字符串变为绝对字符串
				imageUrl = Const.DOWNLOAD_URL+((String)msg.obj).substring("success".length());
				sendReqEditCarDataBase();
				Log.e("jj",imageUrl);
				break;
			case UPLOAD_FAILED:
				Toast.makeText(getApplicationContext(), "上传图片失败,请检查图片名称格式是否正确，不能包含特殊字符", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
			
		}
	};

	protected void sendReqEditCarDataBase() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String result;
				try {
//修改这里
					params.put("type", "modifydatabase");
					params.put("method", "editcar");
					params.put("id", id);
					params.put("imageurl", newPath);
					params.put("platenum", pn);
					params.put("brand", br);
					params.put("color", co);
					result = WebServicePost.sendPOSTRequest(RequestPath, params, "utf-8");
					Message msg = new Message();
	                msg.what = GET_EDIT_SIG;
	                msg.obj = result;
	                handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();	
	}
	
}
