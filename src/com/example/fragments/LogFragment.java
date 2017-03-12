package com.example.fragments;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.example.addmycar.Const;
import com.example.addmycar.ServerUtils;
import com.example.bean.Car;
import com.example.bean.Logs;
import com.example.bean.User;
import com.example.loglist.Log;
import com.example.loglist.LogAdapter;
import com.example.shanyaocarwash.R;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class LogFragment extends Fragment{
	
	protected static final int GET_LOG = 0;
	private List<Logs> logList = new ArrayList<Logs>();
	
	private String logId;
	private String filePath;
	private String path;
	
	private View v;
	
    public LogFragment(User user) {
    	logId = user.getLogid();
    	filePath = logId+".xml";
	}

	public LogFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab4, container, false);
        v = view;
//        initData();
        getLogs();
        return view;
    }

	private void getLogs() {
		
		path = Const.LOG_URL+filePath;
		
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
                        logList = parseXmlInfo(is);
                        Message message = new Message();
                        message.what = GET_LOG;
                        message.obj = "";
                        handler.sendMessage(message);
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

	protected List<Logs> parseXmlInfo(InputStream is) {
		/*我们用pull解析器解析xml文件*/  
        
        //1.先拿到pull解析器  
        XmlPullParser xParser = Xml.newPullParser();  
        List<Logs> LogsList = null; 
        try {             
            xParser.setInput(is, "utf-8");  
            //获取事件的类型  
            int eventType = xParser.getEventType();  
            Logs logs = null;  
            LogsList = new ArrayList<Logs>();
            while(eventType != XmlPullParser.END_DOCUMENT)  
            {  
                switch (eventType) {  
                case XmlPullParser.START_TAG:  

                    if ("logs".equals(xParser.getName())) {  
                        //new出一个news的对象  
                        logs = new Logs();  
                    }  
                    else if ("detail".equals(xParser.getName())) {  
                        String detail = xParser.nextText();  
                         logs.setDetail(detail);  
                    }  
                    else if ("date".equals(xParser.getName())) {  
                        String date = xParser.nextText();  
                         logs.setDate(date);  
                    }   
                    break;  
                case XmlPullParser.END_TAG:  
                    //当结束时间是logs时，说明一条logs已经解析完成，并且加入到集合中  
                    if("logs".equals(xParser.getName()))  
                    {  
                        LogsList.add(0,logs);  
                    }  
                    break;  
                }  
                  
                eventType = xParser.next();  
            }  
            
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        
        return LogsList;
	}

	
	
	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_LOG:
				LogAdapter logAdapter = new LogAdapter(getActivity(),R.layout.log_item,logList);
		        ListView listView = (ListView) v.findViewById(R.id.log_list_view);
				listView.setAdapter(logAdapter);
				break;
			default:
				break;
			}
			
		}
	};
}
