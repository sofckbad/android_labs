package com.example.lab1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddPost extends AppCompatActivity {

	ImageView image;
	TextView music;
	EditText text;
	EditText header;
	TextView coordinates;
	ImageButton button;
	Intent intent;
	Toast toast;
	double[] coords = {0,0};

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
		coordinates.setOnClickListener(v -> {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+coords[0] + ',' + coords[1]));
//			intent.putExtra("position", holder.getAdapterPosition());
			startActivityForResult(intent, Main.MAP_REQUEST);
		});
		button.setOnClickListener(v -> {
			if (intent.getStringExtra("image") != null && music.getText().length() != 0 && text.getText().length() != 0 && header.getText().length() != 0) {
				intent.putExtra("text", text.getText().toString());
				intent.putExtra("header", header.getText().toString());
				intent.putExtra("coordinates", coords[0] + "," + coords[1]);
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
			case Main.MAP_REQUEST :
				if (resultCode == RESULT_OK) {
					Geocoder geocoder = new Geocoder(this, Locale.getDefault());
					List<Address> addresses;
					String[] s = data.getStringExtra("newCoords").split(",");
					coords[0] = Double.parseDouble(s[0]);
					coords[1] = Double.parseDouble(s[1]);
					try {
						addresses = geocoder.getFromLocation(coords[0], coords[1], 1);
						if (addresses.size() == 0) throw new NumberFormatException();
						String locality = (addresses.get(0).getLocality() == null)?"":addresses.get(0).getLocality();
						String country = (addresses.get(0).getCountryName() == null)?"":addresses.get(0).getCountryName()+" ";
						coordinates.setText((country + locality).equals("")?(coords[0] + "," + coords[1]):(country + locality));
					} catch (IOException | NumberFormatException e) {
						e.printStackTrace();
						coordinates.setText(coords[0] + "," + coords[1]);
					}
				}
				break;
		}
	}
}