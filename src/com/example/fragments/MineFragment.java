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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import com.coolweather.app.activity.WeatherActivity;
import com.example.fragments.MyScrollView;
import com.example.addmycar.Const;
import com.example.bean.LogMessage;
import com.example.bean.Logs;
import com.example.bean.User;
import com.example.loglist.LogAdapter;
import com.example.newslist.News;
import com.example.newslist.NewsAdapter;
import com.example.recharge.Recharge;
import com.example.recharge.ShowRechageDeatil;
import com.example.recharge.ShowWalletDeatil;
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
import android.net.Uri;
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
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MineFragment extends Fragment implements OnClickListener,OnTouchListener, OnGestureListener{

	
	
	private Button recharge_bt;
	private Button wallet_bt;
	
	
	private int washed_num = 0;
	private int remaining_num = 0;
	private int card_price = 0;
	private int remainings = 0;
	private String userPhone;
	
		
	
	
	private ImageView show_pic1;
	private ImageView show_pic2;
	private ImageView show_pic3;
	private ImageView show_pic4;
	
	
	
	Map<String, String> logMap = new HashMap<String, String>();
	
	private String RequestPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/ExecuteEvents";
	
	private String filePath;
	private String path;
	
	
	
	private ViewFlipper viewFlipper;
	private boolean showNext = true;
	private boolean isRun = true;
	private int currentPage = 0;
	private final int SHOW_NEXT = 0011;
	private static final int FLING_MIN_DISTANCE = 50;
	private static final int FLING_MIN_VELOCITY = 0;
	protected static final int GET_NEWS = 0;
	private GestureDetector mGestureDetector;
	
	private float x;
	private float y;
	
	private View mView;
	
	private String fragmentExist = null;
	
	FinalBitmap fb=null;
	
	
	
	public static TextView mLocation;
	
	public static TextView weatherType;
	
	public static TextView weatherTemp;
	
	public static TextView weatherReminder;
	
	private String provider;
	
	private RelativeLayout weatherLayout;
	
	public static boolean isFromMineFragment = false;
	
	private ListView listView;
	private NewsAdapter adapter;
	private List<News> dataList = new ArrayList<News>();
	
	
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
        isFromMineFragment = true;
        return view;
    }


	@SuppressWarnings("deprecation")
	private void initView(View v) {
		
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
	        weatherTemp = (TextView) v.findViewById(R.id.temp);
	        weatherType = (TextView) v.findViewById(R.id.show_type);
	        weatherReminder = (TextView) v.findViewById(R.id.show_reminder);
	        //在线程中更新天气
	        Weather weather = new Weather();
			new Thread(weather).start();
	        
			
			//获取天气布局的点击事件
			weatherLayout = (RelativeLayout) v.findViewById(R.id.weather_layout);
			weatherLayout.setOnClickListener(this);
			
			//获取两个按钮的点击事件
			recharge_bt = (Button) v.findViewById(R.id.recharge_button);
			wallet_bt = (Button) v.findViewById(R.id.wallet_button);
			recharge_bt.setOnClickListener(this);
			wallet_bt.setOnClickListener(this);
			
			//设置消息推送listview

			listView = (ListView) v.findViewById(R.id.news_list_view);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					News news = dataList.get(position-1);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(news.getUrl()));
					startActivity(intent);
				}
			});
			loadNews();
	}

	 private void loadNews() {
		
		 path = Const.NEWS_URL;
			
			//在子线程中获取服务器的数据  
	        Thread thread = new Thread(){  
	            @Override  
	            public void run() { 
	                try {  
	                    URL url = new URL(path);  
	                    //建立连接  
	                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	                    //设置请求方式  
	                    conn.setRequestMethod("GET");  
	                    //设置请求超时时间  
	                    conn.setConnectTimeout(5000);  
	                    //设置读取超时时间  
	                    conn.setReadTimeout(5000);  
	                    //判断是否获取成功  
	                    if(conn.getResponseCode() == 200)  
	                    {  
	                        //获得输入流  
	                        InputStream is = conn.getInputStream();  
	                        //解析输入流中的数据  
	                        dataList = parseXmlInfo(is);
	                        Message message = new Message();
	                        message.what = GET_NEWS;
	                        message.obj = "";
	                        mHandler.sendMessage(message);
	                    }  
	                } catch (Exception e) {  
	                    // TODO Auto-generated catch block  
	                    e.printStackTrace();  
	                }          
	                
	            } 
	        };  
	          
	        //启动线程  
	        thread.start();
	}
	 
	 
	 protected List<News> parseXmlInfo(InputStream is) {
			/*我们用pull解析器解析xml文件*/  
	        
	        //1.先拿到pull解析器  
	        XmlPullParser xParser = Xml.newPullParser();  
	        List<News> newsList = null; 
	        try {             
	            xParser.setInput(is, "utf-8");  
	            //获取事件的类型  
	            int eventType = xParser.getEventType();  
	            News news = null;  
	            newsList = new ArrayList<News>();
	            while(eventType != XmlPullParser.END_DOCUMENT)  
	            {  
	                switch (eventType) {  
	                case XmlPullParser.START_TAG:  

	                    if ("News".equals(xParser.getName())) {  
	                        //new出一个news的对象  
	                        news = new News();  
	                    }  
	                    else if ("title".equals(xParser.getName())) {  
	                        String title = xParser.nextText();  
	                         news.setTitle(title);  
	                    }  
	                    else if ("url".equals(xParser.getName())) {  
	                        String url = xParser.nextText();  
	                         news.setUrl(url);  
	                    }   
	                    break;  
	                case XmlPullParser.END_TAG:  
	                    //当结束时间是logs时，说明一条logs已经解析完成，并且加入到集合中  
	                    if("News".equals(xParser.getName()))  
	                    {  
	                        newsList.add(0,news);  
	                    }  
	                    break;  
	                }  
	                  
	                eventType = xParser.next();  
	            }  
	            
	        } catch (Exception e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	        
	        return newsList;
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
			case GET_NEWS:				
				adapter = new NewsAdapter(getActivity(),R.layout.news_item,dataList);
				listView.setAdapter(adapter);
				listView.setDividerHeight(2);
				listView.addHeaderView(new ViewStub(getActivity()));
				listView.addFooterView(new ViewStub(getActivity()));
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



	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.weather_layout:
			Intent intent = new Intent(getActivity(),WeatherActivity.class);
			intent.putExtra("city_name", MainActivity.currentPosition);
			startActivity(intent);
			break;
		case R.id.recharge_button:
			Intent intent1 = new Intent(getActivity(),ShowRechageDeatil.class);
			intent1.putExtra("phone", userPhone);
			intent1.putExtra("card_price", card_price);
			intent1.putExtra("remainings", remainings);
			intent1.putExtra("filepath", filePath);
			startActivity(intent1);

			break;
		case R.id.wallet_button:
			Intent intent2 = new Intent(getActivity(),ShowWalletDeatil.class);
			intent2.putExtra("washed_num", washed_num);
			intent2.putExtra("remaining_num", remaining_num);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}
    
}
