package com.example.lab1;

import androidx.fragment.app.Fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
			userCursor = db.rawQuery("select * from " + DBHelper.USERS + " where " + DBHelper.COLUMN_NAME + " = '" + email + "' and " + DBHelper.COLUMN_PASSWORD + " = '" + password + "'", null);
			if (userCursor.getCount() == 1) {
				((Main) getActivity()).switchFragment(R.id.main_content, ((Main) getActivity()).recyclerFragment);
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