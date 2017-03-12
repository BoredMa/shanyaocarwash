package com.example.clientservice;

import java.util.List;

import com.example.shanyaocarwash.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProblemAdapter extends ArrayAdapter<String>{

	private int resourceId;
	
	public ProblemAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String problem = getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView ==null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder =new ViewHolder();
			viewHolder.problem = (TextView) view.findViewById(R.id.problem);
			viewHolder.arr_right = (ImageView) view.findViewById(R.id.arr_right);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.problem.setText(problem);
		viewHolder.arr_right.setImageResource(R.drawable.icon_arrows);
		return view;
	}

	class ViewHolder{
		TextView problem;
		ImageView arr_right;
	}
}
