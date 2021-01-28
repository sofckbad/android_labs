package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Register extends AppCompatActivity
{
	SQLiteDatabase db;
	Cursor userCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		db = (new DBHelper(getApplicationContext())).getReadableDatabase();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();

		db.close();
		userCursor.close();
	}

	public void onClickUp(View view)
	{

	}

	public void onClickBack(View view)
	{
		Intent intent = new Intent(Register.this, Login.class);
		startActivity(intent);
	}
}