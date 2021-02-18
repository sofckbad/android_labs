package com.example.lab1;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerHolder extends RecyclerView.ViewHolder {

	TextView text_content;
	TextView header_content;
	ImageView image_content;
	ConstraintLayout constraintLayout;
	Button playButton;
	SeekBar seekBar;
	boolean isHidden = true;

	public RecyclerHolder(@NonNull View itemView) {
		super(itemView);

		image_content = itemView.findViewById(R.id.content_image);
		header_content = itemView.findViewById(R.id.content_header);
		text_content = itemView.findViewById(R.id.content_text);
		constraintLayout = itemView.findViewById(R.id.content);
		playButton = itemView.findViewById(R.id.playButton);
		seekBar = itemView.findViewById(R.id.seekBar4);

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
		constraintLayout.setOnClickListener(v -> {
			if (isHidden){
				image_content.setLayoutParams( new ConstraintLayout.LayoutParams(
						ConstraintLayout.LayoutParams.MATCH_PARENT,
						(int) Main.application.getResources().getDimension(R.dimen.height_open_post_image)));
				text_content.setMaxLines(50);
				header_content.setMaxLines(50);
				text_content.setVerticalScrollBarEnabled(true);
				header_content.setVerticalScrollBarEnabled(true);
				isHidden = false;
			}
			else {
				image_content.setLayoutParams( new ConstraintLayout.LayoutParams(
						ConstraintLayout.LayoutParams.MATCH_PARENT,
						(int) Main.application.getResources().getDimension(R.dimen.height)));
				text_content.setVerticalScrollBarEnabled(false);
				text_content.setMaxLines(3);
				header_content.setVerticalScrollBarEnabled(false);
				header_content.setMaxLines(3);
				isHidden = true;
			}
		});
	}
}
