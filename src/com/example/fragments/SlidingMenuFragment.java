package com.example.fragments;


import java.util.ArrayList;
import java.util.List;

import com.example.addmycar.AddCarActivity;
import com.example.bean.User;
import com.example.bottommenu.ShowBottomMenu;
import com.example.login.Login;
import com.example.newslist.News;
import com.example.newslist.NewsAdapter;
import com.example.shanyaocarwash.R;
import com.example.shanyaocarwash.SlidingMenuActivity_4;
import com.example.slidingmenu.NormalQs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.AvoidXfermode.Mode;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SlidingMenuFragment extends Fragment{

	private Button logOut;
	
	private List<SlidingItem> slidingList = new ArrayList<SlidingItem>();
	
	private String userPhone = "";
	private String userName = "";
	private String userAge = "";
	private String userSex = "";
	
	private String honorName = ""; 
	
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	
	private ListView listView;

	
	
	public SlidingMenuFragment(User user) {
		userPhone = user.getPhone();
		userName = user.getName();
		userAge = user.getAge();
		userSex = user.getSex();
	}

	public SlidingMenuFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.list, null);
	    initView(view);
	    setAccountDetails(view);
	    pressLogout(view);
	    setOnclick(view);
		return view;
	}

	private void initView(View view) {
		logOut = (Button) view.findViewById(R.id.log_out);
		pref = this.getActivity().getSharedPreferences("shanyaodata", Context.MODE_PRIVATE);
		
		initSlidingMenu();
		listView = (ListView) view.findViewById(R.id.list_view);
		RowAdapter SlidingItemAdapter = new RowAdapter(getActivity(), 
				R.layout.row,  slidingList);
		
		listView.setAdapter(SlidingItemAdapter);
		listView.addHeaderView(new ViewStub(getActivity()));
		listView.addFooterView(new ViewStub(getActivity()));
		listView.setDividerHeight(2);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 1:
					startActivity(new Intent(getActivity(),NormalQs.class));
					break;
				case 2:
					Toast.makeText(getActivity(), "当前已是最新版本", Toast.LENGTH_SHORT).show();
					break;
				case 3:
					ShowBottomMenu showBM = new ShowBottomMenu();
					showBM.showButtonMenu(getActivity(),"weixinshare");
					break;
				}
			}
		});
	}

	private void setOnclick(View view) {
		logOut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(getActivity(), "退出登陆", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getActivity(),Login.class));
				getActivity().finish();
				editor = pref.edit();
				editor.clear();
				editor.commit();
			}
		});
		
	}

	private void pressLogout(View view) {
		
		 logOut.setOnTouchListener(new View.OnTouchListener() {  
	            @Override  
	            public boolean onTouch(View v, MotionEvent event) {  
	                if (event.getAction() == MotionEvent.ACTION_DOWN) {  
	                    logOut.setBackgroundResource(R.drawable.log_out_pressed);    
	                } else if (event.getAction() == MotionEvent.ACTION_UP) {  
	                    logOut.setBackgroundResource(R.drawable.log_out_normal);          
	                }  
	                return false;	
	            }
		 });
	}

	private void setAccountDetails(View view) {
		//修改用户的手机号和称呼
	    TextView phoneNum = (TextView) view.findViewById(R.id.phone_num);
		phoneNum.setText(userPhone);
		
		TextView nameString = (TextView) view.findViewById(R.id.name_string);
		nameString.setText(getHonorName(userName, userSex));	
	}

	private String getHonorName(String name,String sex){
		String hName = "";
		char[] nameArray = name.toCharArray();
		if(sex.equals("male")){
			hName = nameArray[0] + "先生";
		}else if(sex.equals("female")){
			hName = nameArray[0] + "女士";
		}
		return hName;
	}
	


	private void initSlidingMenu() {
		SlidingItem common_problem = new SlidingItem("常见问题解答",R.drawable.icon_question);
		slidingList.add(common_problem);
		SlidingItem update = new SlidingItem("检查更新",R.drawable.icon_update);
		slidingList.add(update);
		SlidingItem wx_share = new SlidingItem("微信分享",R.drawable.icon_share);
		slidingList.add(wx_share);
	}


	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		
		if (getActivity() instanceof SlidingMenuActivity_4) {
			SlidingMenuActivity_4 fca = (SlidingMenuActivity_4) getActivity();
			fca.switchContent(fragment);
		} 
	}


}
