package com.hackathon.perizie.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hackathon.perizie.R;
import com.hackathon.perizie.model.Item;

public class AdapterItems extends ArrayAdapter<Item> {
	
	private ArrayList<Item> items;
	private Context context;
	private int viewResourceId;
	
	public AdapterItems(Context context, int viewResourceId, ArrayList<Item> items) {
		super(context, viewResourceId, items);
		this.context = context;
		this.viewResourceId = viewResourceId;
		this.items = items;
	}

	static class ViewHolder {
		TextView street;
	}

	private void updateRow(int position, final ViewHolder holder) {

		Item item = items.get(position);
		if (item != null) {
//			holder.name.setText(item.name);
			holder.street.setText(item.address);
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(viewResourceId, parent, false);
			holder = new ViewHolder();
			holder.street = (TextView) convertView.findViewById(R.id.row_street);	
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}

		updateRow(position, holder);
		return convertView;
	}

}
