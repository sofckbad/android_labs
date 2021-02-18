package com.example.lab1;

import android.content.ContentUris;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {

	int countOfElement;

	public RecyclerAdapter(int count) { this.countOfElement = count; }
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
		holder.image_content.setImageURI(Uri.parse(Main.imageArray.get(position)));
		holder.text_content.setText(Main.textArray.get(position));
		holder.header_content.setText(Main.headerArray.get(position));

		holder.playButton.setOnClickListener(v -> {
			try {
				if (position != Main.numWhoPlaying){
					Main.mp.reset();
					Main.mp.setDataSource(Main.application, ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Integer.parseInt(Main.mediaArray.get(position).split("%3A")[1])));
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
					Main.numWhoPlaying = position;
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

	public void removeItemAt(int adapterPosition) {
		SQLiteDatabase db = (new DBHelper(Main.application)).getWritableDatabase();
		db.execSQL("DELETE FROM " + DBHelper.DATA + " WHERE " + DBHelper.COLUMN_HEADER + " = ? AND " + DBHelper.COLUMN_IMAGE + " = ? AND " + DBHelper.COLUMN_MUSIC + " = ? AND " + DBHelper.COLUMN_TEXT + " = ?",
				new Object[]{Main.headerArray.get(adapterPosition) ,Main.imageArray.get(adapterPosition), Main.mediaArray.get(adapterPosition), Main.textArray.get(adapterPosition)});

		Toast.makeText(Main.application, "deleted", Toast.LENGTH_SHORT).show();

		notifyItemRangeRemoved(adapterPosition, 1);

		countOfElement--;
		if (Main.numWhoPlaying == adapterPosition) {
			Main.mp.reset();
			Main.numWhoPlaying = -1;
		}

		Main.headerArray.remove(adapterPosition);
		Main.imageArray.remove(adapterPosition);
		Main.mediaArray.remove(adapterPosition);
		Main.textArray.remove(adapterPosition);

		db.close();
	}
	public void addItem() {
		notifyItemRangeInserted(countOfElement, 1);
	}
}
