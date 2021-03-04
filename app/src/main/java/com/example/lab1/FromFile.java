package com.example.lab1;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.lab1.activities.Main;
import com.example.lab1.interfaces.dataInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FromFile implements dataInterface {

	JSONObject json;

	@Override
	public void fromVK() {
		if (Main.data.size() == 0) {
			if (Main.VK_TOKEN == null) {
				Main.activity.vkAuthorise(null);
			} else {
				LoadFromVK asyncTask = new LoadFromVK();
				asyncTask.execute();
			}
		}
	}

	@Override
	public void intoDB(String header, String image, String audio, String text, String coordinates) {
		JSONArray data;
		try {
			data = json.getJSONArray("data");
		} catch (JSONException e) {
			try {
				json.put("data", new JSONArray());
			} catch (JSONException jsonException) {
				jsonException.printStackTrace();
			}
		}
		try {
			data = json.getJSONArray("data");
			JSONObject insertedData = new JSONObject();
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String date = f.format(new Date());

			insertedData.put("audio", audio==null?"":audio);
			insertedData.put("text", text);
			insertedData.put("header", header);
			insertedData.put("image", image);
			insertedData.put("coordinates", coordinates);
			insertedData.put("name", Main.curName);
			insertedData.put("date", date);

			data.put(insertedData);

			Main.data.add(new Bean(audio, text, header, image, coordinates, Main.curName, date));

			FileOutputStream fos = Main.activity.openFileOutput("data.json", Context.MODE_PRIVATE);
			fos.write(Crypt.encrypt(json.toString()).getBytes());
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		Main.activity.recyclerFragment.recyclerAdapter.add(1);
		Main.activity.recyclerFragment.recyclerAdapter.addItem();

		Main.activity.mainToast.setText("Пост добавлен");
		if (Main.activity.mainToast.getView().getWindowVisibility() != View.VISIBLE)
			Main.activity.mainToast.show();

		Main.header = null;
		Main.image = null;
		Main.audio = null;
		Main.text = null;
		Main.coordinates = null;
	}

	@Override
	public void fromDB() {
		try {
			json = new JSONObject(getFile());
			JSONArray data = json.getJSONArray("data");

			if (Main.data.size() != 0) { Main.data.clear();}

			Main.activity.mainToast.setText("Подгружаем из json");
			if (Main.activity.mainToast.getView().getWindowVisibility() != View.VISIBLE)
				Main.activity.mainToast.show();

			for (int i = 0; i < data.length(); i++) {
				JSONObject curObj = data.getJSONObject(i);
				Main.data.add(new Bean(
						curObj.getString("audio"),
						curObj.getString("text"),
						curObj.getString("header"),
						curObj.getString("image"),
						curObj.getString("coordinates"),
						curObj.getString("name"),
						curObj.getString("date")
						));
			}
			fromVK();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private String getFile() {
		BufferedReader reader;
		try {
			FileInputStream fin = Main.activity.openFileInput("data.json");

			byte[] bytes = new byte[fin.available()];
			fin.read(bytes);
			String res = new String (bytes);
			res = Crypt.decrypt(res);
			return res;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{\"data\":[]}";
	}

	@Override
	public void removeItemAt(RecyclerHolder holder) {

		//удаление из json

		int position = 0;

		FileOutputStream fos = null;
		try {
			JSONArray array = json.getJSONArray("data");
			int j = -1;
			for (int i = 0; i < array.length(); i++) {
				String right = String.valueOf(holder.text_content.getText());
				String left = array.getJSONObject(i).getString("text");
				if (left.equals(right)) {
					j = i;
					break;
				}
			}
			position = j;
			array.remove(position);
			try {
				Main.data.remove(position);
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			fos = Main.activity.openFileOutput("data.json", Context.MODE_PRIVATE);
			fos.write(Crypt.encrypt(json.toString()).getBytes());
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}


		Main.activity.mainToast.setText("Удалено");
		if (Main.activity.mainToast.getView().getWindowVisibility() != View.VISIBLE) {
			Main.activity.mainToast.show();
		}

		Main.activity.recyclerFragment.recyclerAdapter.notifyItemRangeRemoved(position, 1);
		Main.activity.recyclerFragment.recyclerAdapter.countOfElement--;
		if (Main.numWhoPlaying == position) {
			Main.mp.reset();
			Main.numWhoPlaying = - 1;
		}
	}

	@Override
	public void change(Intent data) {
		int position = 0;

		FileOutputStream fos = null;
		try {
			JSONArray array = json.getJSONArray("data");
			int j = -1;
			for (int i = 0; i < array.length(); i++) {
				if (array.getJSONObject(i).getString("text").equals(data.getStringExtra("old_text"))) {
					j = i;
					break;
				}
			}
			position = j;
			JSONObject obj = array.getJSONObject(position);
			obj.put("text", data.getStringExtra("text"));
			obj.put("image", data.getStringExtra("image"));
			obj.put("header", data.getStringExtra("header"));
			obj.put("coordinates", data.getStringExtra("coordinates"));
			obj.put("audio", data.getStringExtra("audio"));
			fos = Main.activity.openFileOutput("data.json", Context.MODE_PRIVATE);
			fos.write(Crypt.encrypt(json.toString()).getBytes());
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

	}
}