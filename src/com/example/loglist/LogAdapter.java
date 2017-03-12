package com.example.loglist;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.bean.Logs;
import com.example.shanyaocarwash.R;

public class LogAdapter extends ArrayAdapter<Logs>{

	private int resourceId;
	
	public LogAdapter(Context context, int resource, List<Logs> objects) {
		super(context, resource,objects);
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Logs logs = getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.logDate = (TextView) view.findViewById(R.id.log_time);
			viewHolder.logMessage = (TextView) view.findViewById(R.id.log_message);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.logDate.setText(logs.getDate());
		viewHolder.logMessage.setText(logs.getDetail());
		return view;
	}

	class ViewHolder{
		TextView logDate;
		TextView logMessage;
	}
}
