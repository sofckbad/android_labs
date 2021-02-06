package com.example.lab1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Elements extends Fragment {
	SQLiteDatabase db;
	Cursor userCursor;
	LinearLayout linearLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_elements, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		linearLayout = getActivity().findViewById(R.id.content);
		db = (new DBHelper(getContext().getApplicationContext())).getReadableDatabase();
		userCursor = db.rawQuery("select * from " + DBHelper.TABLE, null);
		userCursor.moveToFirst();
		while (!userCursor.isAfterLast()) {
			((Main) getActivity()).addIntoFragment(userCursor.getString(1), userCursor.getString(2));
			userCursor.moveToNext();
		}
	}
}