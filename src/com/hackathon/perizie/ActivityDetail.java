package com.hackathon.perizie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devsmart.android.ui.HorizontalListView;
import com.hackathon.perizie.helpers.DataHelper;

public class ActivityDetail extends SherlockFragmentActivity {
	private ProgressDialog mProgressDialog;
	private HorizontalListView mListView;
	private EditText mComments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		mListView = (HorizontalListView) findViewById(R.id.list_photos);
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Uploading...");
		mProgressDialog.setCancelable(false);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		mComments = (EditText) findViewById(R.id.edit_comment);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Bundle extras = data.getExtras();
		Bitmap bitmap = (Bitmap) extras.get("data");
		if (bitmap == null)
			return;
		DataHelper.getInstance().getCurrentItem().photos.add(bitmap);

		String path = null;
		try {

			File image = File
					.createTempFile("image-" + DataHelper.getInstance().getCurrentItem().photos.size(), ".jpg");

			FileOutputStream fos = new FileOutputStream(image);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			
			path = image.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}

		DataHelper.getInstance().getCurrentItem().photosPath.add(path);
		mAdapter.notifyDataSetChanged();

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void post(String url, List<NameValuePair> nameValuePairs) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);

		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			for (int index = 0; index < nameValuePairs.size(); index++) {
				if (nameValuePairs.get(index).getName().contains("image")) {

					entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index)
							.getValue()), "upload.jpg", "image/jpeg", "UTF-8"));
				} else {
					// Normal string data
					entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index)
							.getValue()));
				}
			}

			httpPost.setEntity(entity);

			HttpResponse response = httpClient.execute(httpPost, localContext);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.menu_upload:
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(takePictureIntent, 0);
			break;
		case R.id.menu_confirm:
			mProgressDialog.show();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", "" + DataHelper.getInstance().getCurrentItem().id));
			nameValuePairs.add(new BasicNameValuePair("sopr_speditivo_esito", mComments.getText().toString()));
			for (int i = 0; i < DataHelper.getInstance().getCurrentItem().photosPath.size(); i++) {
				nameValuePairs.add(new BasicNameValuePair("images-" + i + "-image", DataHelper.getInstance()
						.getCurrentItem().photosPath.get(i)));
			}

			new PostResult(nameValuePairs).execute();
			break;
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_detail, menu);

		return super.onCreateOptionsMenu(menu);
	}

	private BaseAdapter mAdapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return DataHelper.getInstance().getCurrentItem().photos.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, null);

			ImageView image = (ImageView) retval.findViewById(R.id.image_thumb);
			image.setImageBitmap(DataHelper.getInstance().getCurrentItem().photos.get(position));
			return retval;
		}

	};

	private class PostResult extends AsyncTask<Void, Integer, Void> {

		private List<NameValuePair> args;

		public PostResult(List<NameValuePair> args) {
			this.args = args;
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (isCancelled()) {
				return null;
			}

			post(DataHelper.getInstance().getHost() + "post", args);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.hide();
			finish();

			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onCancelled() {

		}
	}
}
