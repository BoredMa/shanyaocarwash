package com.example.slidingmenu;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.bean.Logs;
import com.example.bean.Question;
import com.example.shanyaocarwash.R;

public class QsAdapter extends ArrayAdapter<Question>{

	private int resourceId;
	
	public QsAdapter(Context context, int resource, List<Question> objects) {
		super(context, resource,objects);
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Question qs = getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.question = (TextView) view.findViewById(R.id.question);
			viewHolder.answer = (TextView) view.findViewById(R.id.answer);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.question.setText(qs.getQuestion());
		viewHolder.answer.setText(qs.getAnswer());
		return view;
	}

	class ViewHolder{
		TextView question;
		TextView answer;
	}
}
