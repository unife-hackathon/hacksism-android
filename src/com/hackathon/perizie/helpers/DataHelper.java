package com.hackathon.perizie.helpers;

import java.util.ArrayList;

import com.hackathon.perizie.model.Item;

public class DataHelper {

	
	private static DataHelper instance;

	private ArrayList<Item> mData;
	private int mCurrentItem;
	private String mHost;
	
	private DataHelper() {}

	public static DataHelper getInstance() {
		if (instance == null) {
			instance = new DataHelper();
		}
		return instance;
	}
	
	public void initData() {
		mData = new ArrayList<Item>();
	}
	
	public ArrayList<Item> getData() {
		return mData;
	}
	
	public void setCurrentItem(int item) {
		mCurrentItem = item;
	}
	
	public Item getCurrentItem() {
		return mData.get(mCurrentItem);
	}
	
	public void setHost(String host) {
		mHost = host;
	}
	
	public String getHost() {
		return mHost;
	}

	
}
