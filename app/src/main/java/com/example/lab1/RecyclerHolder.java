package com.example.lab1;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab1.activities.CryptoActivity;
import com.example.lab1.activities.Main;
import com.example.lab1.fragments.RecyclerFragment;

public class RecyclerHolder extends RecyclerView.ViewHolder {

	TextView text_content;
	TextView header_content;
	TextView address_content;
	TextView user_name;
	ImageView image_content;
	ConstraintLayout constraintLayout;
	Button playButton;
	Button fullPostButton;
	Button change;
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
		change = itemView.findViewById(R.id.change);
		user_name = itemView.findViewById(R.id.userName);

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
		text_content.setOnLongClickListener(v -> {
			Intent intent = new Intent(Main.activity, CryptoActivity.class);
			intent.putExtra("text", ((TextView) v).getText());
			Main.activity.startActivity(intent);
			return true;
		});
	}
}
