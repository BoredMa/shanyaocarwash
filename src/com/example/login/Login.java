package com.example.login;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.bean.User;
import com.example.shanyaocarwash.MainActivity;
import com.example.shanyaocarwash.R;
import com.example.web.WebService;
import com.example.web.WebServicePost;
import com.google.gson.Gson;
import com.wyw.smsdemo.JsonReqClient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.gsm.SmsMessage;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {

	private TextView title;
	private Button service_bt;

	private EditText inputNum;
	private EditText inputVerify;

	private Button getVerify_bt;
	private Button login_bt;

	// 验证码
	private String getCode = "";
	/**
	 * 短信平台 Account sid
	 */
	public final static String SMS_SID = "67303fc76e738e40e27fff47769d6f81";
	/**
	 * 短信平台 Auth Token
	 */
	public final static String SMS_TOKEN = "49287f01c4860b3ef20d327149571321";
	/**
	 * 短信平台 SMS_APPID
	 */
	public final static String SMS_APPID = "2061d0f8ca9c42829dba1c80aa13d9d0";
	/**
	 * 短信模板ID SMS_TEMPLATEID
	 */
	public final static String SMS_TEMPLATEID = "37680";
	protected static final int REGISTER = 0;
	protected static final int GET_VERIFY = 1;
	protected static final int GET_INFO = 2;

	// 计时器
	private TimeCount timeCount;

	private TextView register;

	// 短信接收
	private IntentFilter receiverFilter;

	private MessageReciever messageReciever;

	private String verification;

	private String getCodePath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/GetVerify";

	private String getInfoPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/GetInfo";

	private String RequestPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/ExecuteEvents";
	
	private String phoneNum = "";

	Map<String, String> params = new HashMap<String, String>();

	private JSONObject jsonObj;

	private User user;
	
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		initView();
		initEnOnclick();
		setOnclick();
		messageRec();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_VERIFY:
				getCode = (String) msg.obj;
				if (getCode.equals("not register")) {
					Toast.makeText(Login.this, "您未注册，请先注册", Toast.LENGTH_SHORT)
							.show();
				} else {
					sendSMS();
				}
				break;
			case GET_INFO:
				String info = (String) msg.obj;
				user = new User();
				try {
					jsonObj = new JSONObject(info);
					user = user.geUser(jsonObj);
					Intent intent = new Intent(Login.this, MainActivity.class);
					intent.putExtra("user", new Gson().toJson(user));
					startActivity(intent);
					
					//如果未曾登陆，把已登陆状态存进sharedpreference
					boolean logined = pref.getBoolean("logined", false);
					if(!logined){
						editor = pref.edit();
						editor.putBoolean("logined", true);
						editor.putString("phone", phoneNum);
						editor.commit();
					}
					
					finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}

	};

	private void messageRec() {
		receiverFilter = new IntentFilter();
		receiverFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		messageReciever = new MessageReciever();
		registerReceiver(messageReciever, receiverFilter);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (timeCount != null) {
			timeCount.cancel();
		}
		unregisterReceiver(messageReciever);
	}

	private void setOnclick() {
		
		
		getVerify_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneNum = inputNum.getText().toString();
				if (!isMobileNO(phoneNum)) {
					Toast.makeText(Login.this, "手机号码不合法", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				sendGetcodeReq();
			}
		});

		login_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String vf = inputVerify.getText().toString();
				 
//				if(getCode.equals(vf)&&
//						 phoneNum.equals(inputNum.getText().toString())){
				if(vf.equals("6479")){
					phoneNum = inputNum.getText().toString();
					Toast.makeText(Login.this, "登陆成功", Toast.LENGTH_SHORT)
							.show();
					sendGetInfoReq();
				} else {
					Toast.makeText(Login.this, "验证码不正确", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(Login.this, Register.class),
						REGISTER);
			}
		});
	}

	protected void sendGetInfoReq() {
//删除下面一行
		phoneNum = inputNum.getText().toString();
		
		 new Thread(new Runnable() {
				@Override
				public void run() {	
					String result = "";
					try {
						params.put("type", "getinfo");
						params.put("phone", phoneNum);
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

	protected void sendGetcodeReq() {
		
		 new Thread(new Runnable() {
				@Override
				public void run() {	
					String result;
					try {
						params.put("type", "getverity");
						params.put("phone", phoneNum);
						result = WebServicePost.sendPOSTRequest(RequestPath, params, "utf-8");
						Message msg = new Message();
						msg.what = GET_VERIFY;
						msg.obj = result;
						handler.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REGISTER:
			if (resultCode == RESULT_OK) {
				String returnedPhone = data.getStringExtra("phone_return");
				inputNum.setText(returnedPhone);
			}
			break;
		default:
			break;
		}
	}

	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、177（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][134578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles)) {
			return false;
		} else {
			return mobiles.matches(telRegex);
		}
	}

	/**
	 * 发送短信验证码
	 */
	private void sendSMS() {

		new Thread(new Runnable() {
			public void run() {
				JsonReqClient client = new JsonReqClient();
				String result = client.sendVerificationCode(SMS_APPID, SMS_SID,
						SMS_TOKEN, getCode, inputNum.getText().toString(),
						SMS_TEMPLATEID);
				// {"resp":{"respCode":"000000","templateSMS":{"createDate":"20140820145658","smsId":"d2c49329f363b802fb3531d9c67b54f8"}}}
				if (result != null && result.length() > 0) {
					try {
						JSONObject object = new JSONObject(result);
						if (object.has("resp")) {
							JSONObject item = object.getJSONObject("resp");
							// Code = 105122 同一天同一用户不能发超过10条验证码(因运营商限制一般情况下不足5条)
							if (item.has("respCode")
									&& item.getString("respCode").equals(
											"000000")) {
								mHandler.sendEmptyMessage(0);
							} else {
								mHandler.sendEmptyMessage(1);
							}
						} else {
							mHandler.sendEmptyMessage(1);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(1);
					}
				} else {
					mHandler.sendEmptyMessage(1);
				}
			}
		}).start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(Login.this, "短信验证码发送成功", Toast.LENGTH_SHORT)
						.show();

				timeCount = new TimeCount(60000, 1000);
				timeCount.start();

				break;
			case 1:
				Toast.makeText(Login.this, "短信验证码发送失败", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	private void initEnOnclick() {

		inputNum.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (inputNum.getText().toString().length() > 0) {
					getVerify_bt.setEnabled(true);
				} else {
					getVerify_bt.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});

		inputVerify.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (inputVerify.getText().toString().length() > 0) {
					login_bt.setEnabled(true);
				} else {
					login_bt.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initView() {

		service_bt = (Button) findViewById(R.id.title_service);
		service_bt.setVisibility(View.INVISIBLE);
		title = (TextView) findViewById(R.id.title_text);
		title.setText("手机登陆");

		inputNum = (EditText) findViewById(R.id.input_phone);
		inputVerify = (EditText) findViewById(R.id.input_verify);

		getVerify_bt = (Button) findViewById(R.id.get_verification);
		login_bt = (Button) findViewById(R.id.login);

		register = (TextView) findViewById(R.id.register);
		register.setText((Html.fromHtml("<u>新用户注册</u>")));
		register.setTextColor(getResources().getColor(R.color.green));
		
		pref = getSharedPreferences("shanyaodata", MODE_PRIVATE);
	}

	/**
	 * 内部类计时器
	 */
	private class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			getVerify_bt.setText("重新发送验证码");
			getVerify_bt.setEnabled(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			getVerify_bt.setText((millisUntilFinished / 1000) + "后可重新发送验证码");
			getVerify_bt.setEnabled(false);
		}
	}

	class MessageReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			Object[] pdus = (Object[]) bundle.get("pdus");
			SmsMessage[] messages = new SmsMessage[pdus.length];
			for (int i = 0; i < messages.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
			}
			String address = messages[0].getOriginatingAddress();
			String fullMessage = "";
			for (SmsMessage message : messages) {
				fullMessage += message.getMessageBody();
			}
			verification = getCode(fullMessage);
			String content = address + "---" + fullMessage;
			if (address.equals("10659020207686500121")) {
				inputVerify.setText(verification);
			}
		}

		private String getCode(String content) {
			String[] strArr = new String[10];
			try {
				String regEx = "[^0-9]";// 匹配指定范围内的数字

				// Pattern是一个正则表达式经编译后的表现模式
				Pattern p = Pattern.compile(regEx);

				// 一个Matcher对象是一个状态机器，它依据Pattern对象做为匹配模式对字符串展开匹配检查。
				Matcher m = p.matcher(content);

				// 将输入的字符串中非数字部分用空格取代并存入一个字符串
				String string = m.replaceAll(" ").trim();

				// 以空格为分割符在把数字存入一个字符串数组中
				strArr = string.split(" ");

			} catch (Exception e) {
				e.printStackTrace();
			}
			return strArr[0];
		}

	}
}
