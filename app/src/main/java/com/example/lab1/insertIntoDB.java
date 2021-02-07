package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

public class insertIntoDB extends AppCompatActivity {

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
			Toast.makeText(this, "SQLiteConstraintException", Toast.LENGTH_LONG).show();
			setResult(RESULT_CANCELED);
		}
		finish();
	}
}