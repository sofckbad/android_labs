package com.example.lab1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.regex.Pattern;

public class LoadImageAsync extends AsyncTask<Void, Void, Bitmap> {

	private ImageView imageView;
	int position;
	String uri;

	public LoadImageAsync(ImageView imageView, int position, String uri) {
		this.imageView = imageView;
		this.position = position;
		this.uri = uri;
	}

	@Override
	protected Bitmap doInBackground(Void... voids) {
		Bitmap img = null;
		try {
			if (uri == null) return null;
			if (! Pattern.compile("^http").matcher(uri).find()) return null;
			java.net.URL url = new java.net.URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			img = BitmapFactory.decodeStream(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (uri == null) return;
		if (bitmap == null) imageView.setImageURI(Uri.parse(uri));
		else imageView.setImageBitmap(bitmap);
	}
}
