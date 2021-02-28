package com.example.lab1;

import android.content.ContentUris;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab1.activities.AddPost;
import com.example.lab1.activities.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {

	public int countOfElement;

	public RecyclerAdapter() {}
	public void setCountOfElement(int countOfElement) {
		this.countOfElement = countOfElement;
	}
	public void add(int add) {countOfElement += add;}
	public ArrayList<Bean> dataLink = Main.data;

	@NonNull
	@Override
	public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return new RecyclerHolder(inflater.inflate(R.layout.holder, parent,false));
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
//		holder.image_content.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(Main.imageArray.get(position))));
		Bean data = dataLink.get(holder.getAdapterPosition());
		holder.image_content.setImageURI(Uri.parse(data.image));
		holder.text_content.setText(data.text);
		holder.header_content.setText(data.header);
		holder.user_name.setText(data.name);
		if (holder.getAdapterPosition() == Main.numWhoPlaying) {
			holder.seekBar.setVisibility(View.VISIBLE);
			holder.seekBar.setMax(Main.mp.getDuration());
			holder.seekBar.setProgress(Main.mp.getCurrentPosition());
			Main.currentSeekBar = holder.seekBar;
		}

		String[] coords = dataLink.get(holder.getAdapterPosition()).coordinates.split(",");

		if (Main.activity.isAdmin) {
			holder.change.setVisibility(View.VISIBLE);
			holder.change.setOnClickListener(v -> {
				Intent intent = new Intent(Main.activity, AddPost.class);
				intent.putExtra("position", holder.getAdapterPosition());
				intent.putExtra("old_image", data.image);
				intent.putExtra("old_audio", data.media);
				intent.putExtra("old_text", data.text);
				intent.putExtra("old_header", data.header);
				intent.putExtra("old_coordinates", data.coordinates);
				Main.activity.startActivityForResult(intent, Main.CHANGE_REQUEST);
			});
		}

		Geocoder geocoder = new Geocoder(Main.activity, Locale.getDefault());
		List<Address> addresses;
		try {
			addresses = geocoder.getFromLocation(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), 1);
			if (addresses.size() == 0) throw new NumberFormatException();
			String locality = (addresses.get(0).getLocality() == null)?"":addresses.get(0).getLocality();
			String country = (addresses.get(0).getCountryName() == null)?"":addresses.get(0).getCountryName()+" ";
			holder.address_content.setText((country + locality).equals("")?data.coordinates:(country + locality));
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
			holder.address_content.setText(data.coordinates);
		}
		holder.address_content.setOnClickListener(v -> {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+data.coordinates));
			intent.putExtra("position", holder.getAdapterPosition());
			Main.activity.startActivityForResult(intent, Main.MAP_REQUEST);
		});
		holder.playButton.setOnClickListener(v -> {
			try {
				if (holder.getAdapterPosition() != Main.numWhoPlaying){
					Main.mp.reset();
//					data.media.split("%3A")[1]
					Matcher m = Pattern.compile("\\d*$").matcher(data.media);
					m.find();
					int i = Integer.parseInt(m.group());
					Main.mp.setDataSource(Main.application, ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, i));
					Main.mp.prepare();
					Main.mp.start();
					((Button) v).setText("pause");
					if (Main.currentSeekBar != null)
						Main.currentSeekBar.setVisibility(View.GONE);
					if (Main.currentSeekBar != null)
						Main.currentButton.setText("play");
					Main.currentButton = (Button) v;
					Main.currentSeekBar = holder.seekBar;
					Main.currentSeekBar.setVisibility(View.VISIBLE);
					Main.currentSeekBar.setProgress(0);
					Main.currentSeekBar.setMax(Main.mp.getDuration());
					holder.seekBar.setMax(Main.mp.getDuration());
					Main.numWhoPlaying = holder.getAdapterPosition();
				} else {
					if (Main.mp.isPlaying()) {
						Main.mp.pause();
						((Button) v).setText("play");
					}
					else {
						Main.mp.start();
						((Button) v).setText("pause");
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		});
	}


	@Override
	public int getItemCount() {
		return countOfElement;
	}

	public void addItem() {
		notifyItemRangeInserted(countOfElement, 1);
	}

	public void showSorted(ArrayList<Bean> arrayList) {
		dataLink = arrayList;
		countOfElement = arrayList.size();
		System.out.println("-----------"+Main.data.size());
		System.out.println("----------+"+countOfElement);
	}
}
