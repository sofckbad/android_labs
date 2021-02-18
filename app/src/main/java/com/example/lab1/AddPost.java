package com.example.lab1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AddPost extends AppCompatActivity {

	ImageView image;
	TextView music;
	EditText text;
	EditText header;
	ImageButton button;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_post);

		image = findViewById(R.id.imageView);
		music = findViewById(R.id.music);
		text = findViewById(R.id.text_post);
		header = findViewById(R.id.header_post);
		button = findViewById(R.id.add_elements);

		intent = getIntent();

		image.setOnClickListener(v -> startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), Main.IMAGE_REQUEST));
		music.setOnClickListener(v -> startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("audio/*"), Main.AUDIO_REQUEST));
		button.setOnClickListener(v -> {
			if (text.getText().length() != 0) {
				intent.putExtra("text", text.getText().toString());
				intent.putExtra("header", header.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
			} else {
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case Main.IMAGE_REQUEST :
				if (resultCode == RESULT_OK){
					image.setImageURI(data.getData());
					intent.putExtra("image", data.getData().toString());
				}
				break;
			case Main.AUDIO_REQUEST :
				if (resultCode == RESULT_OK){
					music.setText(data.getData().toString());
					intent.putExtra("audio", data.getData().toString());
				}
				break;
		}
	}
}