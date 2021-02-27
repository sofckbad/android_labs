package com.example.lab1.fragments;

import androidx.fragment.app.Fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lab1.DBHelper;
import com.example.lab1.R;
import com.example.lab1.activities.Main;

public class Login extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_login, container, false);
	}

	public void signIn(Toast mainToast) {

		String email = ((EditText) getActivity().findViewById(R.id.login_email)).getText().toString();
		String password = ((EditText) getActivity().findViewById(R.id.login_password)).getText().toString();
		SQLiteDatabase db = (new DBHelper(getActivity())).getWritableDatabase();
		Cursor userCursor;

		if (email.length() == 0) {
			mainToast.setText("Enter email");
			if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
				mainToast.show();
		}
		else if (password.length() == 0){
			mainToast.setText("Enter password");
			if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
				mainToast.show();
		}
		else {
			String s = "select "+DBHelper.COLUMN_ID+", "+DBHelper.COLUMN_NAME+" from " + DBHelper.USERS + " where " + DBHelper.COLUMN_NAME + " = '" + email + "' and " + DBHelper.COLUMN_PASSWORD + " = '" + password + "'";
			userCursor = db.rawQuery(s, null);
			if (userCursor.getCount() == 1) {
				userCursor.moveToFirst();
				if (userCursor.getString(1).equals("admin"))
					Main.activity.isAdmin = true;
				else
					Main.activity.isAdmin = false;
				Main.curName = userCursor.getString(1);
				Main.idCurrentUser = userCursor.getInt(0);
				Main.activity.switchFragment(R.id.main_content, ((Main) getActivity()).recyclerFragment);
			} else {
				mainToast.setText("User is not registered");
				if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
					mainToast.show();
			}

			db.close();
			userCursor.close();
		}
	}

	public void signUp() {
		((Main) getActivity()).switchFragment(R.id.main_content, ((Main) getActivity()).registration);
	}
}