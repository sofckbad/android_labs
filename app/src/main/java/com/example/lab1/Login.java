package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class Login extends AppCompatActivity
{
	SQLiteDatabase db;
	Cursor userCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		db = (new DBHelper(getApplicationContext())).getReadableDatabase();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();

		db.close();
		userCursor.close();
	}


	public void onClick(View view)
	{

	}

	public void onClickUp(View view)
	{
		Intent intent = new Intent(Login.this, Register.class);
		startActivity(intent);
	}
}