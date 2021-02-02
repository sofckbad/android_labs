package com.example.lab1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "users.db";
	private static final int SCHEMA = 1;
	static final String TABLE = "users";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "login";
	public static final String COLUMN_PASSWORD = "password";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS users (" + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME
				+ " TEXT unique, " + COLUMN_PASSWORD + " TEXT);");

		db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_NAME
				+ ", " + COLUMN_PASSWORD + ") VALUES ('admin', 'admin');");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE);
		onCreate(db);
	}
}
