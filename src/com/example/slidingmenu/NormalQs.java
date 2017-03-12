package com.example.slidingmenu;

import java.util.ArrayList;
import java.util.List;

import com.example.bean.Logs;
import com.example.bean.Question;
import com.example.loglist.LogAdapter;
import com.example.shanyaocarwash.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class NormalQs extends Activity{

	String[] questions = null;
	
	String[] answers = null;
	
	private List<Question> qsList = new ArrayList<Question>();
	
	private Button service_bt;
	private TextView title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.normal_qs);
		initData();
		initTitleBar();
		QsAdapter qsAdapter = new QsAdapter(this,R.layout.question_item,qsList);
        ListView listView = (ListView) findViewById(R.id.sliding_qs_list);
		listView.setAdapter(qsAdapter);
	}

	private void initTitleBar() {
		service_bt = (Button) findViewById(R.id.title_service);
		title = (TextView) findViewById(R.id.title_text);
		
		service_bt.setVisibility(View.INVISIBLE);
		title.setText("常见问题解答");
		
	}

	private void initData() {
		questions = new String[20];
		answers = new String[20];
		for(int i=0;i<8;i++){
			Question q = new Question();
			switch(i){
			case 0:
				questions[i] = "1.车辆停放位置?";
				answers[i] = "答：车辆停放务必停靠在洗车员指定的位置，不要违占停车，否则后果自负";

				q.setQuestion(questions[i]);
				q.setAnswer(answers[i]);
				break;
			case 1:
				questions[i] = "2.出现问题应该如何联系客服?";
				answers[i] = "答：因为订单较多，电话客服作息繁忙，您可以选择在线客服，同样可以解决您的问题";
				q.setQuestion(questions[i]);
				q.setAnswer(answers[i]);
				break;
			case 2:
				questions[i] = "3.如何检查洗车效果?";
				answers[i] = "答：首先检查漆面的脏东西是否清洗干净，是否有残留污垢；其次进入车内，对着阳光观察玻璃，是否存在大块的水痕水渍的情况";
				q.setQuestion(questions[i]);
				q.setAnswer(answers[i]);
				break;
			case 3:
				questions[i] = "4.闪耀汽车美容除了洗车还有什么服务?";
				answers[i] = "答：包含打蜡、加玻璃水，玻璃镀膜、发动机清洗、精品室内清洁(预约)等等";
				q.setQuestion(questions[i]);
				q.setAnswer(answers[i]);
				break;
			case 4:
				questions[i] = "5.洗车的顺序?";
				answers[i] = "答：洗车的顺序一般情况下是按照先到先洗的原则，如果您有特别的情况，可与洗车员说明情况，加急洗车";
				q.setQuestion(questions[i]);
				q.setAnswer(answers[i]);
				break;
			case 5:
				questions[i] = "6.如何检查洗车效果?";
				answers[i] = "答：首先检查漆面的脏东西是否清洗干净，是否有残留污垢；其次进入车内，对着阳光观察玻璃，是否存在大块的水痕水渍的情况";
				q.setQuestion(questions[i]);
				q.setAnswer(answers[i]);
				break;
			case 6:
				questions[i] = "7.如何检查洗车效果?";
				answers[i] = "答：首先检查漆面的脏东西是否清洗干净，是否有残留污垢；其次进入车内，对着阳光观察玻璃，是否存在大块的水痕水渍的情况";
				q.setQuestion(questions[i]);
				q.setAnswer(answers[i]);
				break;
			case 7:
				questions[i] = "8.如何检查洗车效果?";
				answers[i] = "答：首先检查漆面的脏东西是否清洗干净，是否有残留污垢；其次进入车内，对着阳光观察玻璃，是否存在大块的水痕水渍的情况";
				q.setQuestion(questions[i]);
				q.setAnswer(answers[i]);
				break;

			}
			qsList.add(q);
		}
	}

}
