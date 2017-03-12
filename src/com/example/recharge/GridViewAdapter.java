package com.example.recharge;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


public class GridViewAdapter extends BaseAdapter implements ListAdapter {
	
	private List<String> mList;
	private Context mContex;
	
	public GridViewAdapter(Context context, List<String> list) {
		this.mContex = context;
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = View.inflate(mContex, com.example.shanyaocarwash.R.layout.item_gridview_mine, null);
			viewHolder.price = (TextView) convertView.findViewById(com.example.shanyaocarwash.R.id.prices);
//			viewHolder.desc = (TextView) convertView.findViewById(R.id.desc);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String map = mList.get(position);
//		viewHolder.image.setImageResource((int) map.get("images"));
//		viewHolder.button.setb
		viewHolder.price.setText((String) map);
//		viewHolder.desc.setText((String)map.get("descs"));
		return convertView;
	}
	
	class ViewHolder{
	//	ImageView image;
//		Button button;
		TextView price;
//		TextView desc;
	}
}

