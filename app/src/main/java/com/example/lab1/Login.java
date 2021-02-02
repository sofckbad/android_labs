package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity
{
	SQLiteDatabase db;
	Cursor userCursor;
	TextView alert;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		db = (new DBHelper(getApplicationContext())).getReadableDatabase();
		alert = findViewById(R.id.login_alert);
		alert.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();

		db.close();

		//нужен ли if если курсор null
		userCursor.close();
	}


	public void onClick(View view)
	{
		String email = ((EditText)findViewById(R.id.login_email)).getText().toString();
		String password = ((EditText)findViewById(R.id.login_password)).getText().toString();

		if (email.length() == 0) setAlert("enter email");
		else if (password.length() == 0) setAlert("enter password");
		else{
			userCursor = db.rawQuery("select * from " + DBHelper.TABLE + " where "
					+ DBHelper.COLUMN_NAME + " = '"+ email + "' and "
					+ DBHelper.COLUMN_PASSWORD + " = '" + password + "'", null);
			if (userCursor.getCount()== 1)
				startActivity(new Intent(Login.this, MainActivity.class));
			else setAlert("no matches");
		}
	}

	public void onClickUpLogin(View view)
	{
		startActivity(new Intent(Login.this, Register.class));
	}

	public void setAlert(String alert_text) {
		alert.setText(alert_text);
		alert.setVisibility(View.VISIBLE);
	}
}