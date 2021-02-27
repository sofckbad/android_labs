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

import com.example.lab1.activities.Main;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {

	int countOfElement;

	public RecyclerAdapter() {}
	public void setCountOfElement(int countOfElement) {
		this.countOfElement = countOfElement;
	}
	public void add(int add) {countOfElement += add;}

	@NonNull
	@Override
	public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return new RecyclerHolder(inflater.inflate(R.layout.holder, parent,false));
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
//		holder.image_content.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(Main.imageArray.get(position))));
		holder.image_content.setImageURI(Uri.parse(Main.imageArray.get(holder.getAdapterPosition())));
		holder.text_content.setText(Main.textArray.get(holder.getAdapterPosition()));
		holder.header_content.setText(Main.headerArray.get(holder.getAdapterPosition()));
		holder.user_name.setText(Main.nameArray.get(holder.getAdapterPosition()));
		if (holder.getAdapterPosition() == Main.numWhoPlaying) {
			holder.seekBar.setVisibility(View.VISIBLE);
			holder.seekBar.setMax(Main.mp.getDuration());
			holder.seekBar.setProgress(Main.mp.getCurrentPosition());
			Main.currentSeekBar = holder.seekBar;
		}

		Geocoder geocoder = new Geocoder(Main.activity, Locale.getDefault());
		List<Address> addresses;
		String[] coords = Main.coordinatesArray.get(holder.getAdapterPosition()).split(",");
		try {
			addresses = geocoder.getFromLocation(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), 1);
			if (addresses.size() == 0) throw new NumberFormatException();
			String locality = (addresses.get(0).getLocality() == null)?"":addresses.get(0).getLocality();
			String country = (addresses.get(0).getCountryName() == null)?"":addresses.get(0).getCountryName()+" ";
			holder.address_content.setText((country + locality).equals("")?Main.coordinatesArray.get(holder.getAdapterPosition()):(country + locality));
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
			holder.address_content.setText(Main.coordinatesArray.get(holder.getAdapterPosition()));
		}
		holder.address_content.setOnClickListener(v -> {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+Main.coordinatesArray.get(holder.getAdapterPosition())));
			intent.putExtra("position", holder.getAdapterPosition());
			Main.activity.startActivityForResult(intent, Main.MAP_REQUEST);
		});
		holder.playButton.setOnClickListener(v -> {
			try {
				if (holder.getAdapterPosition() != Main.numWhoPlaying){
					Main.mp.reset();
//					Main.mediaArray.get(holder.getAdapterPosition()).split("%3A")[1]
					Matcher m = Pattern.compile("\\d*$").matcher(Main.mediaArray.get(holder.getAdapterPosition()));
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
}
