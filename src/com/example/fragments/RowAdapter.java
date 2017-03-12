package com.example.fragments;

import java.util.List;

import com.example.shanyaocarwash.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RowAdapter extends ArrayAdapter<SlidingItem>{

	private int resourceId;
	
	public RowAdapter(Context context,  int textViewResourceId,List<SlidingItem> objects ) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
	}
	
	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		SlidingItem slidingItem = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		ImageView itemImage = (ImageView) view.findViewById(R.id.row_icon);
		TextView itemName = (TextView) view.findViewById(R.id.row_title);
		itemImage.setImageResource(slidingItem.getImageId());
		itemName.setText(slidingItem.getName());
		return view;
	}
}
