package com.example.lab1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.lab1.activities.Main;
import com.example.lab1.interfaces.dataInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InDataBase implements dataInterface {

	@Override
	public void fromDB() {
		if (Main.data.size() != 0) { Main.data.clear();}

		if (! Main._isFirebase) {
			SQLiteDatabase db = (new DBHelper(Main.activity)).getWritableDatabase();
			Cursor userCursor;
			userCursor = db.rawQuery(new StringBuilder().append("select ").append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_HEADER).append(", ").append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_IMAGE).append(", ").append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_MUSIC).append(", ").append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_TEXT).append(", ").append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_COORDINATES).append(", ").append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_DATE).append(", ").append(DBHelper.USERS).append(".").append(DBHelper.COLUMN_NAME).append(" from ").append(DBHelper.DATA).append(" INNER JOIN ").append(DBHelper.USERS).append(" ON ").append(DBHelper.DATA).append(".").append(DBHelper.COLUMN_USER_ID).append(" = ").append(DBHelper.USERS).append(".").append(DBHelper.COLUMN_ID).toString(), null);


			userCursor.moveToFirst();
			while (! userCursor.isAfterLast()) {
				Main.data.add(new Bean(userCursor.getString(2), userCursor.getString(3), userCursor.getString(0), userCursor.getString(1), userCursor.getString(4), userCursor.getString(6), userCursor.getString(5)));
				userCursor.moveToNext();
			}

			db.close();
			userCursor.close();

			fromVK();
		} else {
			Main.activity.mainToast.setText("Подгружаем из Firebase");
			if (Main.activity.mainToast.getView().getWindowVisibility() != View.VISIBLE)
				Main.activity.mainToast.show();
			DatabaseReference database = FirebaseDatabase.getInstance().getReference("items");
			ValueEventListener valueEventListener = new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					Main.activity.isSorted = false;
					if (Main.data.size() > 0) Main.data.clear();
					for (DataSnapshot ds : dataSnapshot.getChildren()) {
						Bean bean = ds.getValue(Bean.class);
						bean.link = ds.getKey();
						Main.data.add(bean);
					}

					fromVK();

					Main.activity.recyclerFragment.recyclerAdapter.countOfElement = Main.activity.recyclerFragment.recyclerAdapter.dataLink.size();
					Main.activity.recyclerFragment.recyclerAdapter.notifyDataSetChanged();
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {

				}
			};
			database.addValueEventListener(valueEventListener);
		}
	}

	private void fromVK() {
		if (Main.data.size() == 0) {
			if (Main.VK_TOKEN == null) {
				Main.activity.vkAuthorise(null);
			} else {
				LoadFromVK asyncTask = new LoadFromVK();
				asyncTask.execute();
			}
		}
	}

	@Override
	public void intoDB(String header, String image, String audio, String text, String coordinates) {
		if (! Main._isFirebase) {
			SQLiteDatabase db = (new DBHelper(Main.activity)).getWritableDatabase();
			db.execSQL(new StringBuilder().append("INSERT INTO ").append(DBHelper.DATA).append(" (").append(DBHelper.COLUMN_HEADER).append(", ").append(DBHelper.COLUMN_IMAGE).append(", ").append(DBHelper.COLUMN_MUSIC).append(", ").append(DBHelper.COLUMN_TEXT).append(", ").append(DBHelper.COLUMN_COORDINATES).append(", ").append(DBHelper.COLUMN_USER_ID).append(", ").append(DBHelper.COLUMN_DATE).append(") VALUES (?, ?, ?, ?, ?, ?, datetime('now'))").toString(), new Object[] { header, image, audio, text, coordinates, Main.idCurrentUser });


			Cursor cursor = db.rawQuery("select datetime('now')", null);

			cursor.moveToFirst();
			Bean bean = new Bean(audio, text, header, image, coordinates, Main.curName, cursor.getString(0));
			Main.data.add(bean);

			cursor.close();
			db.close();
		} else {
			FirebaseDatabase database = FirebaseDatabase.getInstance();
			DatabaseReference myRef = database.getReference("items");
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Bean bean = new Bean(audio, text, header, image, coordinates, Main.curName, f.format(new Date()));
			Main.data.add(bean);
			myRef.push().setValue(bean);
			bean.link = myRef.getKey();
		}

		Main.activity.recyclerFragment.recyclerAdapter.add(1);
		Main.activity.recyclerFragment.recyclerAdapter.addItem();

		Main.activity.mainToast.setText("Пост добавлен");
		if (Main.activity.mainToast.getView().getWindowVisibility() != View.VISIBLE)
			Main.activity.mainToast.show();

		Main.header = null;
		Main.image = null;
		Main.audio = null;
		Main.text = null;
		Main.coordinates = null;

	}

	@Override
	public void removeItemAt(int adapterPosition) {
		if (! Main._isFirebase) {
			SQLiteDatabase db = (new DBHelper(Main.application)).getWritableDatabase();
			db.execSQL("DELETE FROM " + DBHelper.DATA + " WHERE " + DBHelper.COLUMN_HEADER + " = ? AND " + DBHelper.COLUMN_IMAGE + " = ? AND " + DBHelper.COLUMN_TEXT + " = ?", new Object[] { Main.data.get(adapterPosition).header, Main.data.get(adapterPosition).image, Main.data.get(adapterPosition).text });
			db.close();
		} else {
			FirebaseDatabase.getInstance().getReference("items").child(Main.data.get(adapterPosition).link).removeValue();
		}

		Main.activity.mainToast.setText("Удалено");
		if (Main.activity.mainToast.getView().getWindowVisibility() != View.VISIBLE) {
			Main.activity.mainToast.show();
		}

		Main.activity.recyclerFragment.recyclerAdapter.notifyItemRangeRemoved(adapterPosition, 1);

		Main.activity.recyclerFragment.recyclerAdapter.countOfElement--;
		if (Main.numWhoPlaying == adapterPosition) {
			Main.mp.reset();
			Main.numWhoPlaying = - 1;
		}

		Main.data.remove(adapterPosition);

	}

	@Override
	public void change(Intent data) {
		if (! Main._isFirebase) {
			SQLiteDatabase db = (new DBHelper(Main.application)).getWritableDatabase();
			db.execSQL(new StringBuilder().append("UPDATE ").append(DBHelper.DATA).append(" SET ").append(DBHelper.COLUMN_HEADER).append(" = '").append(data.getStringExtra("header")).append("',").append(" ").append(DBHelper.COLUMN_IMAGE).append(" = '").append(data.getStringExtra("image")).append("',").append(" ").append(DBHelper.COLUMN_TEXT).append(" = '").append(data.getStringExtra("text")).append("',").append(" ").append(DBHelper.COLUMN_MUSIC).append(" = '").append(data.getStringExtra("audio")).append("',").append(" ").append(DBHelper.COLUMN_COORDINATES).append(" = '").append(data.getStringExtra("coordinates")).append("'").append(" WHERE ").append(DBHelper.COLUMN_HEADER).append(" = '").append(data.getStringExtra("old_header")).append("' and ").append(DBHelper.COLUMN_IMAGE).append(" = '").append(data.getStringExtra("old_image")).append("' and ").append(DBHelper.COLUMN_TEXT).append(" = '").append(data.getStringExtra("old_text")).append("' and ").append(DBHelper.COLUMN_COORDINATES).append(" = '").append(data.getStringExtra("old_coordinates")).append("'").toString(), new Object[] { });
		} else {
			int i = data.getIntExtra("position", - 1);
			Map<String, Object> map = new HashMap<>();
			map.put("header", data.getStringExtra("header"));
			map.put("image", data.getStringExtra("image"));
			map.put("text", data.getStringExtra("text"));
			map.put("media", data.getStringExtra("audio"));
			map.put("coordinates", data.getStringExtra("coordinates"));

			FirebaseDatabase database = FirebaseDatabase.getInstance();
			DatabaseReference myRef = database.getReference("items").child(Main.data.get(i).link);
			myRef.updateChildren(map, (databaseError, databaseReference) -> {
				if (databaseError != null)
					Toast.makeText(Main.activity, "problems with change", Toast.LENGTH_SHORT).show();
			});
		}
	}
}
