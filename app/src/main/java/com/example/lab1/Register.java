package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity
{
	SQLiteDatabase db;
	Cursor userCursor;
	TextView alert;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		db = (new DBHelper(getApplicationContext())).getWritableDatabase();
		alert = findViewById(R.id.register_alert);
		alert.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();

		db.close();
		userCursor.close();
	}

	public void onClickUp(View view)
	{
		String email = ((EditText)findViewById(R.id.register_email)).getText().toString();
		String password = ((EditText)findViewById(R.id.register_password)).getText().toString();
		String confirm_password = ((EditText)findViewById(R.id.register_confirm_password)).getText().toString();

		if (!Pattern.matches(".+@mail[.]ru", email) && !Pattern.matches(".+@gmail[.]com", email)) {
			setAlert("bad e-mail address. use mail.ru or gmail.com");
		}
		else if (!Pattern.matches(".{8,16}", password)){
			setAlert("bad password. length {8-16}");
		}
		else if (!Pattern.matches(".*[A-Z].*", password)){
			setAlert("bad password. add capital letter");
		}
		else if (!Pattern.matches(".*[a-z].*", password)){
			setAlert("bad password. add lowercase letter");
		}
		else if (!Pattern.matches(".*\\d+.*", password)){
			setAlert("bad password. add numeral");
		}
		else if (!password.equals(confirm_password))
		{
			setAlert("1st & 2nd password are different");
		}
		else{
			db.execSQL("INSERT INTO "+ DBHelper.TABLE +" (" + DBHelper.COLUMN_NAME
					+ ", " + DBHelper.COLUMN_PASSWORD + ") VALUES ('"+email+"', '"+password+"')");
			startActivity(new Intent(Register.this, MainActivity.class));
		}
	}

	public void onClickBack(View view)
	{
		startActivity(new Intent(Register.this, Login.class));
	}

	public void setAlert(String alert_text) {
		alert.setText(alert_text);
		alert.setVisibility(View.VISIBLE);
	}
}