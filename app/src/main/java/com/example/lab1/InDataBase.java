package com.example.lab1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import com.example.lab1.activities.Main;
import com.example.lab1.interfaces.dataInterface;

public class InDataBase implements dataInterface {

	@Override
	public void fromDB() {
		SQLiteDatabase db = (new DBHelper(Main.activity)).getWritableDatabase();
		Cursor userCursor;
		userCursor = db.rawQuery(new StringBuilder()
				.append("select ")
				.append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_HEADER).append(", ")
				.append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_IMAGE).append(", ")
				.append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_MUSIC).append(", ")
				.append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_TEXT).append(", ")
				.append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_COORDINATES).append(", ")
				.append(DBHelper.USERS).append(".").append(DBHelper.COLUMN_NAME)
				.append(" from ")
				.append(DBHelper.DATA)
				.append(" INNER JOIN ")
				.append(DBHelper.USERS)
				.append(" ON ")
				.append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_USER_ID)
				.append(" = ")
				.append(DBHelper.USERS).append(".").append(DBHelper.COLUMN_ID)
				.toString(), null);

		if (Main.mediaArray.size() != 0) Main.mediaArray.clear();
		if (Main.coordinatesArray.size() != 0) Main.coordinatesArray.clear();
		if (Main.headerArray.size() != 0) Main.headerArray.clear();
		if (Main.textArray.size() != 0) Main.textArray.clear();
		if (Main.imageArray.size() != 0) Main.imageArray.clear();
		if (Main.nameArray.size() != 0) Main.nameArray.clear();

		userCursor.moveToFirst();
		while (! userCursor.isAfterLast()) {
			Main.headerArray.add(userCursor.getString(0));
			Main.imageArray.add(userCursor.getString(1));
			Main.mediaArray.add(userCursor.getString(2));
			Main.textArray.add(userCursor.getString(3));
			Main.coordinatesArray.add(userCursor.getString(4));
			Main.nameArray.add(userCursor.getString(5));
			userCursor.moveToNext();
		}

		db.close();
		userCursor.close();
	}

	@Override
	public void intoDB(String header, String image, String audio, String text, String coordinates) {
		if (image == null || audio == null || text == null) return;
		SQLiteDatabase db = (new DBHelper(Main.activity)).getWritableDatabase();
		db.execSQL("INSERT INTO " + DBHelper.DATA + " (" + DBHelper.COLUMN_HEADER + ", " + DBHelper.COLUMN_IMAGE + ", " + DBHelper.COLUMN_MUSIC + ", " + DBHelper.COLUMN_TEXT + ", " + DBHelper.COLUMN_COORDINATES + ", " + DBHelper.COLUMN_USER_ID + ") VALUES (?, ?, ?, ?, ?, ?)", new Object[] { header, image, audio, text, coordinates, Main.idCurrentUser });

		Main.activity.recyclerFragment.recyclerAdapter.addItem();

		Main.activity.mainToast.setText("Пост добавлен");
		if (Main.activity.mainToast.getView().getWindowVisibility() != View.VISIBLE)
			Main.activity.mainToast.show();

		Main.headerArray.add(header);
		Main.imageArray.add(image);
		Main.mediaArray.add(audio);
		Main.textArray.add(text);
		Main.coordinatesArray.add(coordinates);
		Main.nameArray.add(Main.curName);

		Main.activity.recyclerFragment.recyclerAdapter.add(1);

		Main.header = null;
		Main.image = null;
		Main.audio = null;
		Main.text = null;
		Main.coordinates = null;

		db.close();
	}

	@Override
	public void removeItemAt(int adapterPosition) {
		SQLiteDatabase db = (new DBHelper(Main.application)).getWritableDatabase();
		db.execSQL("DELETE FROM " + DBHelper.DATA + " WHERE " + DBHelper.COLUMN_HEADER + " = ? AND " + DBHelper.COLUMN_IMAGE + " = ? AND " + DBHelper.COLUMN_MUSIC + " = ? AND " + DBHelper.COLUMN_TEXT + " = ?", new Object[] { Main.headerArray.get(adapterPosition), Main.imageArray.get(adapterPosition), Main.mediaArray.get(adapterPosition), Main.textArray.get(adapterPosition) });

		Main.activity.mainToast.setText("Удалено");
		if (Main.activity.mainToast.getView().getWindowVisibility() != View.VISIBLE)
			Main.activity.mainToast.show();

		Main.activity.recyclerFragment.recyclerAdapter.notifyItemRangeRemoved(adapterPosition, 1);

		Main.activity.recyclerFragment.recyclerAdapter.countOfElement--;
		if (Main.numWhoPlaying == adapterPosition) {
			Main.mp.reset();
			Main.numWhoPlaying = - 1;
		}

		Main.headerArray.remove(adapterPosition);
		Main.imageArray.remove(adapterPosition);
		Main.mediaArray.remove(adapterPosition);
		Main.textArray.remove(adapterPosition);
		Main.coordinatesArray.remove(adapterPosition);
		Main.nameArray.remove(adapterPosition);

		db.close();
	}

	@Override
	public void change(Intent data) {
		SQLiteDatabase db = (new DBHelper(Main.application)).getWritableDatabase();
		db.execSQL(new StringBuilder()
				.append("UPDATE ")
				.append(DBHelper.DATA)
				.append(" SET ")
				.append(DBHelper.COLUMN_HEADER).append(" = '").append(data.getStringExtra("header")).append("',").append(" ")
				.append(DBHelper.COLUMN_IMAGE).append(" = '").append(data.getStringExtra("image")).append("',").append(" ")
				.append(DBHelper.COLUMN_TEXT).append(" = '").append(data.getStringExtra("text")).append("',").append(" ")
				.append(DBHelper.COLUMN_MUSIC).append(" = '").append(data.getStringExtra("audio")).append("',").append(" ")
				.append(DBHelper.COLUMN_COORDINATES).append(" = '").append(data.getStringExtra("coordinates")).append("'")
				.append(" WHERE ")
				.append(DBHelper.COLUMN_HEADER).append(" = '").append(data.getStringExtra("old_header")).append("' and ")
				.append(DBHelper.COLUMN_IMAGE).append(" = '").append(data.getStringExtra("old_image")).append("' and ")
				.append(DBHelper.COLUMN_TEXT).append(" = '").append(data.getStringExtra("old_text")).append("' and ")
				.append(DBHelper.COLUMN_MUSIC).append(" = '").append(data.getStringExtra("old_audio")).append("' and ")
				.append(DBHelper.COLUMN_COORDINATES).append(" = '").append(data.getStringExtra("old_coordinates")).append("'")
				.toString(), new Object[]{});

	}
}
