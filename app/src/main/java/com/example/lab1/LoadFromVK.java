package com.example.lab1;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.example.lab1.activities.Main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class LoadFromVK extends AsyncTask<Void, Void, JSONObject> {

	private static String START_FROM = null;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Main.activity.mainToast.setText("Подгружаем из вк");
		if (Main.activity.mainToast.getView().getWindowVisibility() != View.VISIBLE)
			Main.activity.mainToast.show();
	}

	@Override
	protected JSONObject doInBackground(Void... integers) {
		JSONObject response = null;
		try {
			String METHOD = "newsfeed.getRecommended";
			String VERSION = "5.130";
			String MAX_PHOTOS = "1";
			String COUNT = "3";
			String next = "";
			if (START_FROM != null)
				next = (START_FROM.equals(""))?"":("&start_from="+START_FROM);
			URL request = new URL("https://api.vk.com/method/" + METHOD + "?access_token=" + Main.VK_TOKEN + "&v=" + VERSION + "&max_photos=" + MAX_PHOTOS + "&count=" + COUNT + next);
			Scanner scanner = new Scanner(request.openStream());
			response = new JSONObject(scanner.useDelimiter("\\Z").next());
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		try {
			JSONObject response = result.getJSONObject("response");
			JSONArray items = response.getJSONArray("items");
			START_FROM = result.getJSONObject("response").getString("next_from");
//			JSONArray profiles = response.getJSONArray("profiles");
//			JSONArray groups = response.getJSONArray("groups");
			for (int i = 0; i < items.length(); i++) {
				JSONObject obj = items.getJSONObject(i);
				String image = "";
				String audio = null;
				String text = obj.getString("text");
				if (text.equals("")) text = "пост без текста";
				JSONArray attachments = obj.getJSONArray("attachments");
				boolean aud = true;
				boolean img = true;
				for (int j = 0; j < attachments.length(); j++) {
					if (attachments.getJSONObject(j).getString("type").equals("photo") && img) {
						JSONArray sizes = attachments.getJSONObject(j).getJSONObject("photo").getJSONArray("sizes");
						for (int k = 0, buff = 0; k < sizes.length(); k++) {
							if (buff <= sizes.getJSONObject(k).getInt("height"))
								image += sizes.getJSONObject(k).getString("url")+"|";
							buff = sizes.getJSONObject(k).getInt("height");
						}
						image = image.substring(0, image.length()-1);
						img = false;
					}
					else if (attachments.getJSONObject(j).getString("type").equals("audio") && aud) {
						audio = attachments.getJSONObject(j).getJSONObject("audio").getString("url");
						aud = false;
					}
				}
				Main.dataLoader.intoDB("post from vk", image, audio, text, "0,0");
			}
			Main.activity.recyclerFragment.recyclerAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println(result);
		}
	}
}
