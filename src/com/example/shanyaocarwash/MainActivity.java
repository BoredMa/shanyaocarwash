package com.example.shanyaocarwash;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.Utility;
import com.example.addmycar.Const;
import com.example.bean.User;
import com.example.fragments.FrcarFragment;
import com.example.fragments.LogFragment;
import com.example.fragments.MineFragment;
import com.example.fragments.MyViewPager;
import com.example.fragments.MycarFragment;
import com.example.fragments.SlidingMenuFragment;
import com.example.login.LoginFilter;

import com.example.weather.Weather;
import com.example.web.WebServicePost;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Paint.Join;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;


import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import android.widget.TextView;


public class MainActivity extends SlidingFragmentActivity implements  android.view.View.OnClickListener, TabListener {
	public static Context that;
	//声明ActionBar
	private ActionBar actionBar;
	private String[] titles = {"账户","我的爱车","帮朋友买单","洗车记录"};
	private Tab[] mTabs = new Tab[titles.length];	
	//声明ViewPager
//	private ViewPager mViewPager;
	private MyViewPager mViewPager;
	//适配器
	private FragmentPagerAdapter mAdapter;
	//装载Fragment的集合
	private List<Fragment> mFragments;
	
	//四个Tab对应的布局
	private LinearLayout mTabMine;
	private LinearLayout mTabMycar;
	private LinearLayout mTabFrcar;
	private LinearLayout mTabLog;
	
	//四个Tab对应的ImageButton
	private ImageButton mImgMine;
	private ImageButton mImgMycar;
	private ImageButton mImgFrcar;
	private ImageButton mImgLog;
	
	//四个Tab对应的TextView
	private TextView mViewMine;
	private TextView mViewMycar;
	private TextView mViewFrcar;
	private TextView mViewLog;
	
	//顶部的侧边菜单栏
	private Fragment mContent;
	private Button logout;
	private TextView Inputphone;
	private TextView nameString;
	
	private String userPhone;

	
	private JSONObject jsonObj;
	
	private Intent intent;
	
	private User user;
	
	Map<String, String> params = new HashMap<String, String>();
	
	private String getInfoPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/GetInfo";
	
	private String RequestPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/ExecuteEvents";
	
	protected static final int GET_INFO = 2;
	protected static final int SHOW_LOCATION = 0;

	
	
	private boolean[] fragmentsUpdateFlag = { false, false, false, false };
	
	private FragmentManager fm;
	
    private Dialog mDialog;
	
	private Bundle savedInstanceState;
	
	private List<String> tagList = new ArrayList<String>();
	 
	public static LocationManager locationManager;
	
	public static LocationListener locationListener; 
	
	public static String currentPosition;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.savedInstanceState = savedInstanceState;		
		// 设置滑动菜单视图界面
		initViews();//初始化控件
		initEvents();//初始化事件
		initDatas();//初始化数据
		//判断目前客户端是否有网络连接
		//如果没有网络连接，弹出一个dialog
		isConnectedToInternet();
		RefreshInternet ri = new RefreshInternet();
		new Thread(ri).start();
	}

	private void isConnectedToInternet() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					StringBuilder url = new StringBuilder();
					url.append(Const.NEWS_URL);
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(url.toString());
					httpGet.addHeader("Accept_Language","zh-CN");
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if(httpResponse.getStatusLine().getStatusCode() == 200){
						LoginFilter.connected = true;
					}else{
						LoginFilter.connected = false;
					}
					
				}catch(Exception e){
					e.printStackTrace();
					LoginFilter.connected = false;
				}finally{
					Message msg = new Message();
					msg.what = LoginFilter.GET_CONNECTED;
					msg.obj = LoginFilter.connected;
					handler.sendMessage(msg);
				}
			  }
			}).start();
	}

	public static void showNoInternetDialog(final Context context) {
		 //先new出一个dialog监听
        DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){  
  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                switch(which){  
                    case Dialog.BUTTON_POSITIVE:
                        break;  
                    case Dialog.BUTTON_NEGATIVE:  
                        break;    
                }  
            }

			
        };  
        //dialog属性设置
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("提示"); //设置标题  
        builder.setMessage("无可用网络,请确认已经连上网络?"); //设置信息  
        builder.setPositiveButton("确认",dialogOnclicListener);  
        builder.setNegativeButton("取消", dialogOnclicListener);   
        builder.create().show();       
	}

	@Override 
	public void onDestroy(){
		super.onDestroy();
		
		//退出程序的时候关闭位置监听
		if(locationManager != null){
			locationManager.removeUpdates(locationListener);
		}
	}
	

	
	private void refreshTables() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {	
				String result = "";
				try {
					params.put("type", "getinfo");
					params.put("phone", userPhone);
					result = WebServicePost.sendPOSTRequest(RequestPath, params, "utf-8");					
					Message msg = new Message();
					msg.what = GET_INFO;
					msg.obj = result;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();	
		
	}

	private void initDatas() {
		setUserInfo();
		initTabs();
		initSlidingMenu(savedInstanceState);
		setActionBar();
//		setUpTabs();
    }	
	
	private void setUserInfo() {		
		intent = getIntent();
//		user = (User) intent.getSerializableExtra("user");
		String jsonUser = intent.getStringExtra("user");
		user = (User) new Gson().fromJson(jsonUser, User.class);
		userPhone = user.getPhone();
	}
	
	
//	private void setUpTabs() {  
//        actionBar = getActionBar();  
//        for (int i = 0; i < mAdapter.getCount(); ++i) {  
//        	mTabs[i] = actionBar.newTab().setText(mAdapter.getPageTitle(i)).setTabListener(this);
//            actionBar.addTab(mTabs[i]);  
//        }
//	}
	
	private void setActionBar() {
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	private void initSlidingMenu(Bundle savedInstanceState) {
		// 如果保存的状态不为空则得到ColorFragment，否则实例化ColorFragment
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
//		if (mContent == null)
//			mContent = new ColorFragment(R.color.red);

		// 设置滑动菜单视图界面
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.menu_frame, new SlidingMenuFragment(user)).commit();
		// 设置滑动菜单的属性值
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
		getSlidingMenu().setFadeDegree(0.35f);

	}


	private static String makeFragmentName(int viewId, long id) {  
        return "android:switcher:" + viewId + ":" + id;  
    } 
	
	private void initTabs() {
		mFragments = new ArrayList<>();
		//将四个Fragment加入集合中
		
		mFragments.add(new MineFragment(user));
		mFragments.add(new MycarFragment(user));
		mFragments.add(new FrcarFragment(user));
		mFragments.add(new LogFragment(user));

		
		
		fm = getSupportFragmentManager();
		//初始化适配器
		mAdapter = new FragmentPagerAdapter(fm) {
			
			@Override
			public int getCount() {
				return mFragments.size();
			}

			@Override
			public android.support.v4.app.Fragment getItem(int postion) {
				return mFragments.get(postion);
			}

			    
			@Override
			public CharSequence getPageTitle(int position) {				
				 super.getPageTitle(position);				 				 
				 String title = titles[position];
				 return title;
			}

			@Override
			public Object instantiateItem(ViewGroup container,int position) {
				Fragment[] fragments = new Fragment[4];	
				
				fragments[0]= new MineFragment(user);
				fragments[1]= new MycarFragment(user);
				fragments[2]= new FrcarFragment(user);
				fragments[3]= new LogFragment(user);
					
			   //得到缓存的fragment
			    Fragment fragment = (Fragment)super.instantiateItem(container,
			           position);

			   //得到tag ❶
			    String fragmentTag = fragment.getTag();         

			   if (fragmentsUpdateFlag[position %fragmentsUpdateFlag.length]) {

			      //如果这个fragment需要更新
			       android.support.v4.app.FragmentTransaction ft =fm.beginTransaction();

			      //移除旧的fragment

			       ft.remove(fragment);

			      //换成新的fragment

			       fragment = fragments[position];

			      //添加新fragment时必须用前面获得的tag ❶

			       ft.add(container.getId(), fragment, fragmentTag);

			       ft.attach(fragment);

			       ft.commit();

			      //复位更新标志
			      fragmentsUpdateFlag[position %fragmentsUpdateFlag.length] =false;

			    }
			   return fragment;

			}
			
		};
		
		
		
		//不要忘记设置ViewPager的适配器
		mViewPager.setAdapter(mAdapter);
		mViewPager.setScroll(true);
		//设置ViewPager的切换监听
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			 
			//页面选中事件
			@Override
			public void onPageSelected(int position) {
				//设置position对应的集合中的Fragment
				mViewPager.setCurrentItem(position);
				resetImgs();
				resetTextColors();
				selectTab(position);
//				actionBar = getActionBar();  
//              actionBar.setSelectedNavigationItem(position);
			}
			

			//页面滚动事件
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// TODO Auto-generated method stub
				
			}
			//页面滚动状态改变事件
			@Override
			public void onPageScrollStateChanged(int state) {
				
				
			}
			
		});
		
		//初始选择页面
		selectTab(0);
	}


	private void initEvents() {
		//设置四个Tab的点击事件
		mTabMine.setOnClickListener(this);
		mTabMycar.setOnClickListener(this);
		mTabFrcar.setOnClickListener(this);
		mTabLog.setOnClickListener(this);
		
		//顶部侧边菜单栏
		
	}

	private void initViews() {
		
		that = MainActivity.this;
		
		mViewPager = (MyViewPager) findViewById(R.id.tabpager);
		
		mTabMine = (LinearLayout) findViewById(R.id.id_tab_mine);
		mTabMycar = (LinearLayout) findViewById(R.id.id_tab_mycar);
		mTabFrcar = (LinearLayout) findViewById(R.id.id_tab_frcar);
		mTabLog = (LinearLayout) findViewById(R.id.id_tab_log);
		
		mImgMine = (ImageButton) findViewById(R.id.id_tab_mine_img);
		mImgMycar = (ImageButton) findViewById(R.id.id_tab_mycar_img);
		mImgFrcar = (ImageButton) findViewById(R.id.id_tab_frcar_img);
		mImgLog = (ImageButton) findViewById(R.id.id_tab_log_img);
		
		mViewMine = (TextView) findViewById(R.id.id_tab_mine_text);
		mViewMycar = (TextView) findViewById(R.id.id_tab_mycar_text);
		mViewFrcar = (TextView) findViewById(R.id.id_tab_frcar_text);
		mViewLog = (TextView) findViewById(R.id.id_tab_log_text);
		
		
//		mAdapter = new MyFragmentPagerAdapter(fm);
		
		
		//获取位置
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//初始化位置监听器
		locationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				showLocation(location);
			}
		};
		
		//后台自动刷新天气信息
		startService(new Intent(this,AutoUpdateService.class));
	}

	
	
	

	public static void showLocation(final Location location) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					//组装方向地理编码的接口地址
					StringBuilder url = new StringBuilder();
					url.append("http://api.map.baidu.com/geocoder/v2/?ak=1zRdWKMlg969mgHyvOP60X1uCWGu50RO&location=");
					url.append(location.getLatitude()).append(",");
					url.append(location.getLongitude());
					url.append("&output=json&pois=0&mcode=6F:D8:B9:0D:28:3B:C9:45:E5:22:EA:E9:74:D6:FD:30:13:85:A1:80;com.example.shanyaocarwash");
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(url.toString());
					httpGet.addHeader("Accept_Language","zh-CN");
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if(httpResponse.getStatusLine().getStatusCode() == 200){
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity,"utf-8");
						JSONObject jsonObject = new JSONObject(response);
						//获取results节点下的位置信息
						String cityName = jsonObject.getJSONObject("result")
								.getJSONObject("addressComponent").getString("city");

						if(!TextUtils.isEmpty(cityName)){
							Message message = new Message();
							message.what = SHOW_LOCATION;
							message.obj = cityName;
							Locationhandler.sendMessage(message);
						}
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_toolbar, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		//先将四个ImageButton置为灰色
        resetImgs();

        //根据点击的Tab切换不同的页面及设置对应的ImageButton为绿色
        switch (v.getId()) {
            case R.id.id_tab_mine:
                selectTab(0);
                break;
            case R.id.id_tab_mycar:
                selectTab(1);
                break;
            case R.id.id_tab_frcar:
                selectTab(2);
                break;
            case R.id.id_tab_log:
                selectTab(3);
                break;
        }
	}

	
	private void selectTab(int i) {
		//根据点击的Tab设置对应的ImageButton为绿色
		switch (i) {
        case 0:
            mImgMine.setImageResource(R.drawable.account);
            mViewMine.setTextColor(getResources().getColor(R.color.deepskyblue));
            break;
        case 1:
            mImgMycar.setImageResource(R.drawable.mycar);
            mViewMycar.setTextColor(getResources().getColor(R.color.deepskyblue));
            break;
        case 2:
            mImgFrcar.setImageResource(R.drawable.frcar);
            mViewFrcar.setTextColor(getResources().getColor(R.color.deepskyblue));
            break;
        case 3:
            mImgLog.setImageResource(R.drawable.log);
            mViewLog.setTextColor(getResources().getColor(R.color.deepskyblue));
            break;
    }
		//设置当前点击的Tab所对应的页面
        mViewPager.setCurrentItem(i);
	}
	
	//将四个ImageButton设置为灰色
	private void resetImgs() {
		mImgMine.setImageResource(R.drawable.account_normal);
		mImgMycar.setImageResource(R.drawable.mycar_normal);
		mImgFrcar.setImageResource(R.drawable.frcar_normal);
		mImgLog.setImageResource(R.drawable.log_normal);
	}
	
	private void resetTextColors() {
		mViewMine.setTextColor(getResources().getColor(R.color.black));
		mViewMycar.setTextColor(getResources().getColor(R.color.black));
		mViewFrcar.setTextColor(getResources().getColor(R.color.black));
		mViewLog.setTextColor(getResources().getColor(R.color.black));
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		  if (tab == mTabs[0])
          {
              mViewPager.setCurrentItem(0);
          } else if (tab == mTabs[1])
          {
              mViewPager.setCurrentItem(1);
          } else if (tab == mTabs[2])
          {
              mViewPager.setCurrentItem(2);
          }else if (tab == mTabs[3])
          {
              mViewPager.setCurrentItem(3);
          }
		
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	class Timer implements Runnable {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					Thread.sleep(30000);	
					//如果有网络连接就刷新
					if(LoginFilter.connected)
						refreshTables();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class RefreshInternet implements Runnable {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					Thread.sleep(5000);	
					//每隔5秒判断是否有网络连接
					try{
						StringBuilder url = new StringBuilder();
						url.append(Const.NEWS_URL);
						HttpClient httpClient = new DefaultHttpClient();
						HttpGet httpGet = new HttpGet(url.toString());
						httpGet.addHeader("Accept_Language","zh-CN");
						HttpResponse httpResponse = httpClient.execute(httpGet);
						if(httpResponse.getStatusLine().getStatusCode() == 200){
							LoginFilter.connected = true;
						}else{
							LoginFilter.connected = false;
						}
						
					}catch(Exception e){
						e.printStackTrace();
						LoginFilter.connected = false;
					}finally{
						//判断是否是刚刚掉线，如果上次还有网，就弹出一个对话框。确保对话框只弹出一次
						if(LoginFilter.lastConnected){
							Message msg = new Message();
							msg.what = LoginFilter.GET_CONNECTED;
							msg.obj = LoginFilter.connected;
							handler.sendMessage(msg);
						}			
						LoginFilter.lastConnected = LoginFilter.connected;
					}					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_INFO:
				String result = (String) msg.obj;
				try {
					jsonObj = new JSONObject(result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				user = new User();
				user = user.geUser(jsonObj);
				for(int i=0;i<fragmentsUpdateFlag.length;i++)
					fragmentsUpdateFlag[i] = true;		
//				updateUi();
				break;
			case LoginFilter.GET_CONNECTED:
				if(!(boolean)msg.obj){
					showNoInternetDialog(that);
				}else{
					Timer timer = new Timer();
					new Thread(timer).start();
				} 
				break;
			}
		}

	};

	private static Handler Locationhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_LOCATION:
				currentPosition = (String) msg.obj;
				//去掉地点后缀'市'
				if(currentPosition.contains("市")){
					currentPosition = currentPosition.substring(0, currentPosition.length()-1);
				}
				//通过城市名获取城市天气信息
				Weather.queryWeatherInfo(currentPosition);
				break;
			default:
				break;
			}
		}

	};


	
//	private void updateUi(){
//		for(int i=0;i<4;i++){
//			mAdapter.update(i);
//		}
//	}
//
//	private void updateSlidingMenu() {
//		getSupportFragmentManager().beginTransaction()
//		.replace(R.id.menu_frame, new SlidingMenuFragment(user)).commit();
//	}
	
//	class MyFragmentPagerAdapter extends FragmentPagerAdapter{
//
//		public MyFragmentPagerAdapter(FragmentManager fm) {
//		super(fm);
//	}
//
//		@Override
//		public int getCount() {
//			return mFragments.size();
//		}
//
//		@Override
//		public android.support.v4.app.Fragment getItem(int postion) {
//			return mFragments.get(postion);
//		}
//
//		    
//		@Override
//		public CharSequence getPageTitle(int position) {				
//			 super.getPageTitle(position);				 				 
//			 String title = titles[position];
//			 return title;
//		}
//		
//		@Override
//		public Object instantiateItem(ViewGroup container, int position) {
//			tagList.add(makeFragmentName(container.getId(),
//					getItemId(position))); // 把tag存起来
//			Fragment fragment = (Fragment) fm
//					.findFragmentByTag(tagList.get(position));
//			return fragment;
//		}
//
//		@Override
//		public void destroyItem(ViewGroup container, int position,
//				Object object) {
//			super.destroyItem(container, position, object);
//			tagList.remove(makeFragmentName(container.getId(),
//					getItemId(position)));// 把tag删掉
//		}  
//		
//		public void update(int position) {// 这个事真正的更新Fragment的内容
//			Fragment fragment = (Fragment) fm
//					.findFragmentByTag(tagList.get(position));
//			if (fragment == null) {
//				return;
//			}
//			switch (position){
//			case 0:
//				fragment = new MineFragment(user);
//				break;
//			case 1:
//				fragment = new MycarFragment(user);
//				break;
//			case 2:
//				fragment = new FrcarFragment();
//				break;
//			case 3:
//				fragment = new LogFragment(user);
//				break;
//			}		
//		} 
//	}
	
}

