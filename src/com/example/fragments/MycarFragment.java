package com.example.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.addmycar.AddCarActivity;
import com.example.addmycar.EditMyCar;
import com.example.bean.Car;
import com.example.bean.User;
import com.example.mycarlist.MyCarAdapter;
import com.example.shanyaocarwash.R;
import com.example.web.WebServicePost;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MycarFragment extends Fragment{
	
	protected static final int GET_CAR_INFO = 0;

	private Button addCar_bt;
	
	private List<Car> mycarList = new ArrayList<Car>();
	
	private String[] Ids;
	private String[] carIds;
	private String[] imageUrls;
	private String[] carBrands;
	private String[] plateNums;
	private String[] carColors;
	
	public static String phoneNum;
	
	private String carid;
	Map<String, String> params = new HashMap<String, String>();
	private String RequestPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/ExecuteEvents";
	
	private JSONObject jsonObj;
	
	private Car[] cars;
	
	private MyCarAdapter mycarAdapter;
	
	private int listViewLen = 1;
	
	private Dialog mDialog;
	
    public MycarFragment(User user) {
    	this.phoneNum = user.getPhone();
    	this.carid = user.getCarid();
    	this.cars = user.getCars();
    	if(this.cars == null){
    		this.listViewLen = 0;
    	}else{
    		int len = cars.length;
    		this.Ids = new String[len];
        	this.carIds = new String[len];
        	this.carBrands = new String[len];
        	this.carColors = new String[len];
        	this.plateNums = new String[len];
        	this.imageUrls = new String[len];
        	for(int i=0;i<len;i++){
        		this.Ids[i] = cars[i].getId();
        		this.carBrands[i] = cars[i].getBrand();
            	this.carColors[i] = cars[i].getColor();
            	this.plateNums[i] = cars[i].getPlateNum();
            	this.imageUrls[i] = cars[i].getImageUrl();
        	}
        	this.listViewLen = 1;
    	}  	
    	
	}

	public MycarFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.tab2, container, false);
	    initData();
	    addCarButton(view);
		mycarAdapter = new MyCarAdapter(getActivity(),R.layout.mycar_item,mycarList,phoneNum);
		ListView listView = (ListView) view.findViewById(R.id.mycar_list_view);
		listView.setEmptyView(view.findViewById(R.id.layout_empty));
		listView.setAdapter(mycarAdapter);
//		mycarAdapter.notifyDataSetChanged();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Car clickedCar = mycarList.get(position);
				String clickedId = clickedCar.getId();
				String clickedCarId = clickedCar.getCarId();
				String clickedCarImage = clickedCar.getImageUrl();
				String clickedCarBrand = clickedCar.getBrand();
				String clickedCarPlateNum = clickedCar.getPlateNum();
				String clickedCarColor = clickedCar.getColor();
				Intent intent = new Intent(getActivity(),EditMyCar.class);
				intent.putExtra("id", clickedId);
				intent.putExtra("car_id", clickedCarId);
				intent.putExtra("car_image", clickedCarImage);
				intent.putExtra("car_brand", clickedCarBrand);
				intent.putExtra("car_platenum", clickedCarPlateNum);
				intent.putExtra("car_color", clickedCarColor);
				startActivity(intent);
			}
		});
        	return view;
    }
    

	private void addCarButton(View view) {
    	addCar_bt = (Button) view.findViewById(R.id.add_mycar);
		setAddCarTouch();
		setAddCarOnClick();		
	}

	

	private void setAddCarOnClick() {
		addCar_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),AddCarActivity.class);
				intent.putExtra("carid", ""+carid);
				intent.putExtra("phone", phoneNum);
				startActivity(intent); 
			}
		});
		
	}

	private void setAddCarTouch() {
		addCar_bt.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					addCar_bt.setBackgroundResource(R.drawable.rc_btn_voice_hover);
					addCar_bt.setTextColor(getResources().getColor(R.color.lightgreen));
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					addCar_bt.setBackgroundResource(R.drawable.rc_btn_voice_normal);
					addCar_bt.setTextColor(getResources().getColor(R.color.green));
				}
				return false;
			}
		});
		
	}

	private void initData() {
		mycarList.clear();
		if(listViewLen == 0){
			
		}else{
			for(int i=0;i<plateNums.length;i++){
				Car car = new Car(Ids[i],carIds[i],imageUrls[i],carBrands[i],plateNums[i],carColors[i]);
				mycarList.add(car);
			}
		}		
	}

	private void showRoundProcessDialog(Context mContext,int layout) {

        OnKeyListener keyListener = new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_SEARCH)
                {
                    return true;
                }
                return false;
            }
        };

        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.setOnKeyListener(keyListener);
        mDialog.show();
        // 注意此处要放在show之后 否则会报异常
        mDialog.setContentView(layout);
    
	}
}
