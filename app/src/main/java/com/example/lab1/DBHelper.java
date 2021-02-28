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
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_USER_ID = "user_id";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS " + USERS + " (" + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME
				+ " TEXT unique, " + COLUMN_PASSWORD + " TEXT);");

		db.execSQL(new StringBuilder()
				.append("CREATE TABLE IF NOT EXISTS ").append(DATA).append(" (")
				.append(COLUMN_ID)
				.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
				.append(COLUMN_HEADER)
				.append(" TEXT,")
				.append(COLUMN_IMAGE)
				.append(" TEXT, ")
				.append(COLUMN_MUSIC)
				.append(" TEXT, ")
				.append(COLUMN_TEXT)
				.append(" TEXT, ")
				.append(COLUMN_COORDINATES)
				.append(" TEXT, ")
				.append(COLUMN_DATE)
				.append(" TEXT, ")
				.append(COLUMN_USER_ID)
				.append(" INTEGER, ")
				.append("FOREIGN KEY(").append(COLUMN_USER_ID).append(") REFERENCES ")
				.append(USERS).append(" (").append(COLUMN_ID).append(")")
				.append(");").toString());

		db.execSQL("INSERT INTO "+ USERS +" (" + COLUMN_NAME
				+ ", " + COLUMN_PASSWORD + ") VALUES ('admin', 'admin');");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+ USERS);
		onCreate(db);
	}
}
