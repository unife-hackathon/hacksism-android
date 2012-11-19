package com.hackathon.perizie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hackathon.perizie.adapters.AdapterItems;
import com.hackathon.perizie.helpers.DataHelper;

public class ActivityList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		ListView list = (ListView)findViewById(R.id.list);
		
		AdapterItems adapter = new AdapterItems(this, R.layout.row_item, DataHelper.getInstance().getData());
		
		list.setAdapter(adapter);
		list.setOnItemClickListener(new ListItemClickListener());
		
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}
	
	public class ListItemClickListener implements OnItemClickListener {

		public ListItemClickListener() {
			
		}

		public void onItemClick(AdapterView<?> v, View view, int position, long ciop) {
			
			DataHelper.getInstance().setCurrentItem(position);
			
			Intent intent = new Intent(getApplicationContext(), ActivityDetail.class);
			startActivity(intent);
		}

	}
	
}
