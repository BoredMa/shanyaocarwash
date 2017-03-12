package com.example.bottommenu;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.example.addmycar.AddCarActivity;
import com.example.addmycar.EditMyCar;
import com.example.fragments.MycarFragment;
import com.example.shanyaocarwash.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class BottomMenu implements  OnTouchListener {

	public static final int TAKE_PHOTO = 1;
	
	public static final int CROP_PHOTO = 2;
	
	public static final int CHOOSE_PHOTO = 3;
	
	private PopupWindow popupWindow;  
    private Button btn1, btn2, btnCancel;  
    private View mMenuView;  
    private Activity mContext;  
    private OnClickListener clickListener;  
    
    public File outputImage;
    public static Uri imageUri;
    public static  ImageView picture;
    
    public String picName = "";
    
    public BottomMenu() {
		super();
	}

	public BottomMenu(Activity context,OnClickListener clickListener,String event) {  
        LayoutInflater inflater = LayoutInflater.from(context);  
        this.clickListener=clickListener;  
        mContext=context;  
        mMenuView = inflater.inflate(R.layout.layout_popwindow, null);  
        btn1 = (Button) mMenuView.findViewById(R.id.btn1);  
        btn2 = (Button) mMenuView.findViewById(R.id.btn2);  
        btnCancel = (Button) mMenuView.findViewById(R.id.btn_cancel); 
        if(event.equals("weixinshare")){
        	 btn1.setText("分享给朋友");
        	 btn2.setText("分享到朋友圈");
        	 btn1.setOnClickListener(new OnClickListener() {
 				
 				@Override
 				public void onClick(View v) {
 					Toast.makeText(mContext, "你点击了分享给朋友", Toast.LENGTH_SHORT).show();
 					popupWindow.dismiss();
 				}

				
 			});  
              btn2.setOnClickListener(new OnClickListener() {
 				
 				@Override
 				public void onClick(View v) {
 					Toast.makeText(mContext, "你点击了分享到朋友圈", Toast.LENGTH_SHORT).show();
 					popupWindow.dismiss();
 				}
 			});
        	
        }else{
        	if(event.equals("addcar")){
            	picture = (ImageView) mContext.findViewById(R.id.addcar_image);
            }else if(event.equals("editcar")){
            	picture = (ImageView) mContext.findViewById(R.id.edit_image);
            }
            btn1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					openCamera();
					popupWindow.dismiss();
				}
			});  
             btn2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					choosePic();
					popupWindow.dismiss();
				}
			});
        }
        btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
        popupWindow=new PopupWindow(mMenuView,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,true);  
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);  
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.line_gray_tran));  
        popupWindow.setBackgroundDrawable(dw);  
        mMenuView.setOnTouchListener(this);  
    }  
  

    public void show(){  
    	//  获取到根布局
        //View rootView=((ViewGroup)mContext.findViewById(android.R.id.content)).getChildAt(0);  
    	View v = LayoutInflater.from(mContext).inflate(R.layout.addmycar, null);
    	popupWindow.showAtLocation(v, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);   
    }  
       
  
    private void choosePic() {
		Intent intent = new Intent("android.intent.action.GET_CONTENT");
		intent.setType("image/*");
		mContext.startActivityForResult(intent, CHOOSE_PHOTO);	
	}

	private void openCamera() {
		picName = MycarFragment.phoneNum+".jpg";
		outputImage = new File(Environment.getExternalStorageDirectory(),picName);
		try{
			if(outputImage.exists()){
				outputImage.delete();
			}
			outputImage.createNewFile();
		}catch(IOException e){
			e.printStackTrace();
		}
		imageUri = Uri.fromFile(outputImage);
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		mContext.startActivityForResult(intent, TAKE_PHOTO);
	}

	@Override  
    public boolean onTouch(View v, MotionEvent event) {  
        int height = mMenuView.findViewById(R.id.pop_layout).getTop();  
        int y=(int) event.getY();  
        if(event.getAction()==MotionEvent.ACTION_UP){  
            if(y<height){  
                popupWindow. dismiss();  
            }  
        }  
        return true;  
    }  

}
