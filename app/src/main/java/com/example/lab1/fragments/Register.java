package com.example.lab1.fragments;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lab1.DBHelper;
import com.example.lab1.activities.InsertIntoDB;
import com.example.lab1.R;
import com.example.lab1.activities.Main;

import java.util.regex.Pattern;

public class Register extends Fragment {
	SQLiteDatabase db;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_register, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		db = (new DBHelper(getActivity())).getWritableDatabase();
	}

	@Override
	public void onStop() {
		super.onStop();

		db.close();
	}

	public void signUp(View view, Toast mainToast) {
		String email = ((EditText) getActivity().findViewById(R.id.register_email)).getText().toString();
		String password = ((EditText) getActivity().findViewById(R.id.register_password)).getText().toString();
		String confirm_password = ((EditText) getActivity().findViewById(R.id.register_confirm_password)).getText().toString();

		email = email.toLowerCase();

		mainToast.setText("Unknown problem\nInform developers");

		if (! Pattern.matches("[a-z][a-z0-9]{2,19}@[a-z]{2,15}\\.[a-z]{2,10}",email)) {
			String[] s = email.split("@");
			if (Pattern.matches(".*@.*@.*", email)) {
				mainToast.setText("Too many @\nRemove unnecessary");
			} else if (s.length == 0) {
				mainToast.setText("Bad username in e-mail address.\nUsername length cannot be zero");
			} else if (Pattern.matches("[0-9].*", s[0])) {
				mainToast.setText("Bad username in e-mail address.\nUsername cannot start with a number");
			} else if (! Pattern.matches("[a-z0-9]*", s[0])) {
				mainToast.setText("Bad username in e-mail address.\nThe username can include latin letters (a-z), numbers (0-9)");
			} else if (!Pattern.matches("[a-z0-9]{3,20}", s[0])) {
				mainToast.setText("Bad username in e-mail address.\nThe length must be more than 3 and no more than 20 characters");
			}else if (! Pattern.matches(".*@.+", email)) {
				mainToast.setText("Email does not contain domain. Add domain (for example @gmail.com)");
			} else if (! Pattern.matches("^.*@[a-z]{2,15}\\.[a-z]{2,10}$", email)) {
				String[] d = email.split("@");
				String domain = d[d.length-1];
				if (! Pattern.matches("[a-z]{2,15}.*", domain))
					mainToast.setText("Bad domain\nSecond level domain must contain letters and be from 2 to 15 characters in length\nFor example @gmail.com 2nd lvl domain between username and '.' -> '@gmail'");
				else if (! Pattern.matches(".*\\..*", domain))
					mainToast.setText("Bad domain\nAdd first level domain\nFor example @gmail.com 1nd lvl domain between 'gmail' and end of line -> '.com')");
				else if (! Pattern.matches(".*\\.[a-z]{2,10}", domain))
					mainToast.setText("Bad domain\nFirst level domain must contain letters and be from 2 to 10 characters in length\nFor example @gmail.com 1nd lvl domain between 'gmail' and end of line -> '.com'");
			}
			if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
				mainToast.show();
		} else if (!Pattern.matches("[a-zA-Z0-9]{8,16}", password)){
			if (! Pattern.matches(".{8,16}", password)) {
				mainToast.setText("Password length must be 8-16 symbols");
			} else if (! Pattern.matches(".*[A-Z].*", password)) {
				mainToast.setText("Password must contain at least one capital letter");
			} else if (! Pattern.matches(".*[a-z].*", password)) {
				mainToast.setText("Password must contain at least one letter");
			} else if (! Pattern.matches(".*\\d+.*", password)) {
				mainToast.setText("Password must contain at least one numeral");
			}
			if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
				mainToast.show();
		}  else if (! password.equals(confirm_password)) {
			mainToast.setText("Passwords do not match");
			if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
				mainToast.show();
		} else {
			Intent intent = new Intent(getActivity(), InsertIntoDB.class);
			intent.putExtra("table", DBHelper.USERS);
			intent.putExtra("email", email);
			intent.putExtra("password", password);
			startActivityForResult(intent, Main.REGISTRATION_REQUEST);
		}
	}

	public void signIn() {
		((Main) getActivity()).switchFragment(R.id.main_content, ((Main) getActivity()).login);
	}

}