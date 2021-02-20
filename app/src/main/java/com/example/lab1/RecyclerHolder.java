package com.example.lab1;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerHolder extends RecyclerView.ViewHolder {

	TextView text_content;
	TextView header_content;
	TextView address_content;
	ImageView image_content;
	ConstraintLayout constraintLayout;
	Button playButton;
	Button fullPostButton;
	SeekBar seekBar;
	boolean isHidden = true;

	public RecyclerHolder(@NonNull View itemView) {
		super(itemView);

		image_content = itemView.findViewById(R.id.content_image);
		header_content = itemView.findViewById(R.id.content_header);
		address_content = itemView.findViewById(R.id.content_address);
		text_content = itemView.findViewById(R.id.content_text);
		constraintLayout = itemView.findViewById(R.id.content);
		playButton = itemView.findViewById(R.id.playButton);
		fullPostButton = itemView.findViewById(R.id.fullPostButton);
		seekBar = itemView.findViewById(R.id.music_bar);

		address_content.setOnLongClickListener(v -> {
			((TextView) v).getText();
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
				((ClipboardManager) Main.activity.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("item", ((TextView) v).getText().toString()));
				Toast.makeText(Main.activity, "copied", Toast.LENGTH_SHORT).show();
			}
			return true;
		});

		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					seekBar.setProgress(progress);
					Main.mp.seekTo(progress);
				}
			}
			@Override public void onStartTrackingTouch(SeekBar seekBar) { }
			@Override public void onStopTrackingTouch(SeekBar seekBar) { }
		});
		image_content.setLayoutParams(new ConstraintLayout.LayoutParams(
				ConstraintLayout.LayoutParams.MATCH_PARENT,
				(int) Main.application.getResources().getDimension(R.dimen.height)));
		fullPostButton.setOnClickListener(v -> {
			if (isHidden){
				image_content.setLayoutParams( new ConstraintLayout.LayoutParams(
						ConstraintLayout.LayoutParams.MATCH_PARENT,
						(int) Main.application.getResources().getDimension(R.dimen.height_open_post_image)));
				text_content.setMaxLines(50);
				header_content.setMaxLines(50);
				text_content.setVerticalScrollBarEnabled(true);
				header_content.setVerticalScrollBarEnabled(true);
				((Button)v).setText("hide post");
				playButton.setVisibility(View.VISIBLE);
//				seekBar.setVisibility(View.VISIBLE);
				address_content.setVisibility(View.VISIBLE);
				isHidden = false;
				RecyclerFragment.backButton = 1;
			}
			else {
				image_content.setLayoutParams( new ConstraintLayout.LayoutParams(
						ConstraintLayout.LayoutParams.MATCH_PARENT,
						(int) Main.application.getResources().getDimension(R.dimen.height)));
				text_content.setVerticalScrollBarEnabled(false);
				text_content.setMaxLines(3);
				header_content.setVerticalScrollBarEnabled(false);
				header_content.setMaxLines(3);
				((Button)v).setText("show full post");
				playButton.setVisibility(View.GONE);
//				seekBar.setVisibility(View.GONE);
				address_content.setVisibility(View.GONE);
				isHidden = true;
				RecyclerFragment.backButton = 0;
			}
		});
	}
}
