package com.example.lab1;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddNewElement extends Fragment {
	ImageButton newImage;
	SQLiteDatabase db;
	Button newSound;
	Button apply;

	public AddNewElement() {
		super(R.layout.fragment_add_new_element);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_add_new_element, container, false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		db = new DBHelper(getActivity()).getWritableDatabase();
		newImage = getActivity().findViewById(R.id.addImage);
		newSound = getActivity().findViewById(R.id.addMusic);
		apply = getActivity().findViewById(R.id.apply);

		newImage.setOnClickListener(v -> {
			Intent i = new Intent(Intent.ACTION_PICK);
			i.setType("image/*");
			getActivity().startActivityForResult(i, Main.IMAGE_REQUEST);
		});

		newSound.setOnClickListener(v -> {
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.setType("audio/*");
			getActivity().startActivityForResult(i, Main.AUDIO_REQUEST);
		});

		apply.setOnClickListener(v -> {
			if (Main.image == null || Main.audio == null) return;
			db.execSQL("INSERT INTO " + DBHelper.TABLE + " (" + DBHelper.COLUMN_IMAGE + ", " + DBHelper.COLUMN_MUSIC + ") VALUES ( ?, ?)", new Object[] { Main.image, Main.audio });

			((Main) getActivity()).addIntoFragment(Main.image, Main.audio);

			newImage.setImageDrawable(getResources().getDrawable(R.drawable.plus));
			newSound.setText("add sound");

			Main.image = null;
			Main.audio = null;
		});
	}
}