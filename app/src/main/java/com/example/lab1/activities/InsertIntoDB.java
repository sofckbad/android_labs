package com.example.lab1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lab1.DBHelper;

public class InsertIntoDB extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try (SQLiteDatabase db = (new DBHelper(this)).getWritableDatabase()) {
			Intent intent = getIntent();
			String table = intent.getStringExtra("table");
			String email = intent.getStringExtra("email");
			String password = intent.getStringExtra("password");
			db.execSQL("INSERT INTO " + table + " (" + DBHelper.COLUMN_NAME + ", " + DBHelper.COLUMN_PASSWORD + ") VALUES ('" + email + "', '" + password + "')");
			setResult(RESULT_OK);
		}
		catch (SQLiteConstraintException e) {
			setResult(RESULT_CANCELED);
			e.printStackTrace();
		}
		finish();
	}
}