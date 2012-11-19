package com.hackathon.perizie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hackathon.perizie.helpers.DataHelper;
import com.hackathon.perizie.model.Item;

// Please note, I was sleep deprived 

public class MainActivity extends Activity {

	protected ProgressDialog mProgressDialog;
	protected EditText mTextHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DataHelper.getInstance().initData();

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Downloading...");
		mProgressDialog.setCancelable(false);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		mTextHost = (EditText) findViewById(R.id.edit_host);

		Button button = (Button) findViewById(R.id.button_login);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mProgressDialog.show();
				
				DataHelper.getInstance().setHost("http://" + mTextHost.getText().toString() + "/sisma/");
				
				DataHelper.getInstance().getData().clear();
				new LoadList().execute();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private class LoadList extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			if (isCancelled()) {
				return null;
			}


			StringBuilder response = new StringBuilder();

			// Prepare the URL and the connection
			URL u = null;
			try {
				u = new URL(DataHelper.getInstance().getHost() + "get_list");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			HttpURLConnection conn;
			try {
				conn = (HttpURLConnection) u.openConnection();

				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					// Get the Stream reader ready
					BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);

					// Loop through the return data and copy it over to the
					// response
					// object to be processed
					String line = null;

					while ((line = input.readLine()) != null) {
						response.append(line);
					}

					input.close();

					String result = response.toString();

					JSONArray obj = new JSONArray(result);

					synchronized (DataHelper.getInstance().getData()) {
						for (int i = 0; i < obj.length(); i++) {
							JSONObject item = (JSONObject) obj.get(i);
							DataHelper
									.getInstance()
									.getData()
									.add(new Item((Integer) item.get("id"), (String) item.get("address")));
						}

					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.hide();
			Intent intent = new Intent(getApplicationContext(), ActivityList.class);
			startActivity(intent);

			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Toast.makeText(MainActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
			super.onProgressUpdate(values);
		}

		@Override
		protected void onCancelled() {

		}
	}

}
