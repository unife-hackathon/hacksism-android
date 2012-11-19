package com.hackathon.perizie.model;

import java.util.ArrayList;

import android.graphics.Bitmap;


public class Item {

	public int id;
	public String address;
	public ArrayList<Bitmap> photos;
	public ArrayList<String> photosPath;
	
	public Item(int id, String address) {
		this.address = address;
		this.id = id;
		photos = new ArrayList<Bitmap>();
		photosPath = new ArrayList<String>();
	}
	
	public void addPhoto(Bitmap photo) {
		photos.add(photo);
	}
	
	public void addPhotoPath(String photoPath) {
		photosPath.add(photoPath);
	}

}
