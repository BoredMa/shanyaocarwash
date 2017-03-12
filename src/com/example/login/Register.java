package com.example.login;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.example.shanyaocarwash.R;
import com.example.util.HttpConnectionUtil;
import com.example.web.WebServicePost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class Register extends Activity {

	public static final int REGISTER = 1;

	protected static final int SHOW_RESPONSE = 0;


	private TextView title;
	private Button service_bt;

	private EditText phone;
	private EditText name;
	private EditText age;

	private RadioGroup radioGroup;
	private RadioButton femaleRadioButton;
	private RadioButton maleRadioButton;

	private boolean radioClicked;
	private Button register_bt;

	private Judge judge;

	private String p;
	private String n;
	private String a;
	private String sex;

	private String body;
	
	Map<String, String> params = new HashMap<String, String>();
	
	private String RequestPath = "http://" + WebServicePost.IP
			+ "/WirelessOrder/ExecuteEvents";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		initView();
		judge = new Judge();
		setRadioOnclick();
		initEnOnclick();
		setOnclick();
	}

	private void setOnclick() {
		register_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Login.isMobileNO(p)) {
					Toast.makeText(Register.this, "手机号格式不正确",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!isNameChinese(n)) {
					Toast.makeText(Register.this, "请输入中文名字", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (!isCorrectAge(a)) {
					Toast.makeText(Register.this, "请输入正确年龄", Toast.LENGTH_SHORT)
							.show();
				}
				body = "phone="+p+"&name="+n+"&age="+a+"&sex="+sex;
				sendRequestWithHttpClient();
			}
		});
	}

	// 新建Handler的对象，在这里接收Message，然后更新TextView控件的内容
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_RESPONSE:
				String response = (String) msg.obj;
				switch(response){
				case "register_success":
					Toast.makeText(Register.this, "注册成功", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("phone_return", p);
					setResult(RESULT_OK, intent);
					finish();
					break;
				case "register_failed":
					Toast.makeText(Register.this, "注册失败", Toast.LENGTH_SHORT).show();
					break;
				}
				break;

			default:
				break;
			}
		}

	};

	private void sendRequestWithHttpClient() {
		new Thread(new Runnable() {
			String result = "";
			@Override
			public void run() {
				params.put("type", "register");
				params.put("phone", p);
				params.put("name", n);
				params.put("age", a);
				params.put("sex", sex);
				try {
					result = WebServicePost.sendPOSTRequest(RequestPath, params, "utf-8");
					Message msg = new Message();
	                msg.what = SHOW_RESPONSE;
	                msg.obj = result;
	                handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	protected boolean isCorrectAge(String a) {
		boolean flag = false;
		int age = Integer.parseInt(a);
		if (age > 0 && age < 120)
			flag = true;
		return flag;
	}

	protected boolean isNameChinese(String n) {
		if (n.length() == 0) {
			return false;
		}

		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$");
			Matcher m = p.matcher(n);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	private void setRadioOnclick() {
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				radioClicked = true;
				if (checkedId == femaleRadioButton.getId()) {
					System.out.println("选中了female!");
					sex = "female";
				} else if (checkedId == maleRadioButton.getId()) {
					System.out.println("选中了male!");
					sex = "male";
				}
				judge.judgeEnOnclick();
			}
		});

	}

	private void initEnOnclick() {

		phone.addTextChangedListener(new textChangeListener());

		name.addTextChangedListener(new textChangeListener());

		age.addTextChangedListener(new textChangeListener());

	}

	class textChangeListener implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			judge.judgeEnOnclick();
		}

	}

	private void initView() {

		service_bt = (Button) findViewById(R.id.title_service);
		service_bt.setVisibility(View.INVISIBLE);
		title = (TextView) findViewById(R.id.title_text);
		title.setText("手机注册");

		radioGroup = (RadioGroup) findViewById(R.id.sex);
		femaleRadioButton = (RadioButton) findViewById(R.id.female);
		maleRadioButton = (RadioButton) findViewById(R.id.male);

		phone = (EditText) findViewById(R.id.input_phone);
		name = (EditText) findViewById(R.id.input_name);
		age = (EditText) findViewById(R.id.input_age);

		radioClicked = false;
		register_bt = (Button) findViewById(R.id.register_bt);
	}

	private class Judge {
		private void judgeEnOnclick() {
			p = phone.getText().toString();
			n = name.getText().toString();
			a = age.getText().toString();
			if (!p.equals("") && !n.equals("") && !a.equals("")
					&& radioClicked == true) {
				register_bt.setEnabled(true);
			} else {
				register_bt.setEnabled(false);
			}
		}
	}
}
