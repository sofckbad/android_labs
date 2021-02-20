package com.example.lab1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddPost extends AppCompatActivity {

	ImageView image;
	TextView music;
	EditText text;
	EditText header;
	EditText coordinates;
	ImageButton button;
	Intent intent;
	Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_post);

		toast = Toast.makeText(this,"",Toast.LENGTH_LONG);

		image = findViewById(R.id.imageView);
		music = findViewById(R.id.music);
		text = findViewById(R.id.text_post);
		header = findViewById(R.id.header_post);
		button = findViewById(R.id.add_elements);
		coordinates = findViewById(R.id.coordinates_post);

		intent = getIntent();

		image.setOnClickListener(v -> startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), Main.IMAGE_REQUEST));
		music.setOnClickListener(v -> startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("audio/*"), Main.AUDIO_REQUEST));
		button.setOnClickListener(v -> {
			if (intent.getStringExtra("image") != null && music.getText().length() != 0 && text.getText().length() != 0 && header.getText().length() != 0 && coordinates.getText().length() != 0) {
				intent.putExtra("text", text.getText().toString());
				intent.putExtra("header", header.getText().toString());
				intent.putExtra("coordinates", coordinates.getText().toString());
				try {
					String[] coords = coordinates.getText().toString().split(",");
					if (coords.length != 2) throw new NumberFormatException();
					double lat = Double.parseDouble(coords[0]);
					double lon = Double.parseDouble(coords[1]);
					if (lat <= -90 || lat >= 90 || lon <= -180 || lon >= 180) throw new NumberFormatException();
				}
				catch (NumberFormatException e) {
					e.printStackTrace();
					toast.setText("Не верно введены координаты, используйте (-90.0,90.0),(-180.0,180.0)");
					if (toast.getView().getWindowVisibility() != View.VISIBLE) toast.show();
					return;
				}
				setResult(RESULT_OK, intent);
				finish();
			} else {
				toast.setText("Не все поля заполнены");
				if (toast.getView().getWindowVisibility() != View.VISIBLE) toast.show();
				setResult(RESULT_CANCELED, intent);
//				finish();
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