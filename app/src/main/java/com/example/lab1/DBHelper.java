package com.example.lab1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "users.db";
	private static final int SCHEMA = 1;
	public static final String USERS = "users";
	public static final String DATA = "data";

	public static final String COLUMN_ID = "_id";

	public static final String COLUMN_NAME = "login";
	public static final String COLUMN_PASSWORD = "password";

	public static final String COLUMN_HEADER = "header";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_MUSIC = "audio";
	public static final String COLUMN_TEXT = "text";
	public static final String COLUMN_COORDINATES = "coordinates";
	public static final String COLUMN_USER_ID = "user_id";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS " + USERS + " (" + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME
				+ " TEXT unique, " + COLUMN_PASSWORD + " TEXT);");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + DATA + " (" + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_HEADER + " TEXT," + COLUMN_IMAGE
				+ " TEXT, " + COLUMN_MUSIC + " TEXT, " + COLUMN_TEXT + " TEXT, " + COLUMN_COORDINATES + " TEXT, "+COLUMN_USER_ID+" INTEGER, FOREIGN KEY(" +
				COLUMN_USER_ID + ") REFERENCES "+USERS+" ("+COLUMN_ID+"));");

		db.execSQL("INSERT INTO "+ USERS +" (" + COLUMN_NAME
				+ ", " + COLUMN_PASSWORD + ") VALUES ('admin', 'admin');");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+ USERS);
		onCreate(db);
	}
}
