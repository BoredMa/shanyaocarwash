package com.example.newslist;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.bean.Logs;
import com.example.shanyaocarwash.R;

public class NewsAdapter extends ArrayAdapter<News>{

	private int resourceId;
	
	public NewsAdapter(Context context, int resource, List<News> objects) {
		super(context, resource,objects);
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		News news = getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) view.findViewById(R.id.news_title);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.title.setText(news.getTitle());
		return view;
	}

	class ViewHolder{
		TextView title;
		TextView url;
	}
}
