package com.example.recharge;

import java.util.List;
import java.util.Map;

import com.example.shanyaocarwash.R;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


public class PriceGridViewAdapter extends BaseAdapter implements ListAdapter {
	
	private List<String> mList;
	private Context mContex;
	private ViewHolder viewHolder;
	
	
	public PriceGridViewAdapter(Context context, List<String> list) {
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
		
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = View.inflate(mContex, com.example.shanyaocarwash.R.layout.item_gridview_cardprice, null);
			viewHolder.price = (TextView) convertView.findViewById(com.example.shanyaocarwash.R.id.cardprices);
			viewHolder.unit = (TextView) convertView.findViewById(com.example.shanyaocarwash.R.id.cardunit);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String map = mList.get(position);
		viewHolder.price.setText((String) map);
		switch(map){
		case "500":
			viewHolder.unit.setText("元(20次)");
			break;
		case "1000":
			viewHolder.unit.setText("元(40次)");
			break;
		case "1500":
			viewHolder.unit.setText("元(60次)");
			break;
		case "2000":
			viewHolder.unit.setText("元(80次)");
			break;
		}
		
		return convertView;
	}
	
	class ViewHolder{
	//	ImageView image;
//		Button button;
		TextView price;
		TextView unit;
	}
	
	
}

