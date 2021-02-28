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
				.append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_DATE).append(", ")
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

		if (Main.data.size() != 0) Main.data.clear();

		userCursor.moveToFirst();
		while (! userCursor.isAfterLast()) {
			Main.data.add(new Bean(userCursor.getString(2),
					userCursor.getString(3),
					userCursor.getString(0),
					userCursor.getString(1),
					userCursor.getString(4),
					userCursor.getString(6),
					userCursor.getString(5)));
			userCursor.moveToNext();
		}

		db.close();
		userCursor.close();
	}

	@Override
	public void intoDB(String header, String image, String audio, String text, String coordinates) {
		if (image == null || audio == null || text == null) return;
		SQLiteDatabase db = (new DBHelper(Main.activity)).getWritableDatabase();
		db.execSQL(new StringBuilder()
				.append("INSERT INTO ").append(DBHelper.DATA).append(" (")
				.append(DBHelper.COLUMN_HEADER).append(", ")
				.append(DBHelper.COLUMN_IMAGE).append(", ")
				.append(DBHelper.COLUMN_MUSIC).append(", ")
				.append(DBHelper.COLUMN_TEXT).append(", ")
				.append(DBHelper.COLUMN_COORDINATES).append(", ")
				.append(DBHelper.COLUMN_USER_ID).append(", ")
				.append(DBHelper.COLUMN_DATE)
				.append(") VALUES (?, ?, ?, ?, ?, ?, datetime('now'))").toString(), new Object[] { header, image, audio, text, coordinates, Main.idCurrentUser });

		Main.activity.recyclerFragment.recyclerAdapter.addItem();

		Main.activity.mainToast.setText("Пост добавлен");
		if (Main.activity.mainToast.getView().getWindowVisibility() != View.VISIBLE)
			Main.activity.mainToast.show();

		Cursor cursor = db.rawQuery( "select datetime('now')", null);

		cursor.moveToFirst();
		Main.data.add(new Bean(audio,
				text,
				header,
				image,
				coordinates,
				Main.curName,
				cursor.getString(0)));

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
		db.execSQL("DELETE FROM " + DBHelper.DATA + " WHERE " + DBHelper.COLUMN_HEADER + " = ? AND " + DBHelper.COLUMN_IMAGE + " = ? AND " + DBHelper.COLUMN_MUSIC + " = ? AND " + DBHelper.COLUMN_TEXT + " = ?", new Object[] { Main.data.get(adapterPosition).header, Main.data.get(adapterPosition).image, Main.data.get(adapterPosition).media, Main.data.get(adapterPosition).text });

		Main.activity.mainToast.setText("Удалено");
		if (Main.activity.mainToast.getView().getWindowVisibility() != View.VISIBLE)
			Main.activity.mainToast.show();

		Main.activity.recyclerFragment.recyclerAdapter.notifyItemRangeRemoved(adapterPosition, 1);

		Main.activity.recyclerFragment.recyclerAdapter.countOfElement--;
		if (Main.numWhoPlaying == adapterPosition) {
			Main.mp.reset();
			Main.numWhoPlaying = - 1;
		}

		Main.data.remove(adapterPosition);

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
