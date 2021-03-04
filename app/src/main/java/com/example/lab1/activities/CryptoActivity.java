package com.example.lab1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.example.lab1.Crypt;
import com.example.lab1.R;

public class CryptoActivity extends AppCompatActivity {

	TextView defaultText;
	TextView encrypted_type0;
	TextView decrypted_type0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crypto);

		defaultText = findViewById(R.id.default_text);
		encrypted_type0 = findViewById(R.id.encrypted_type0);
		decrypted_type0 = findViewById(R.id.decrypted_type0);

		defaultText.setText(getIntent().getStringExtra("text"));

		new myAsync().execute(getIntent().getStringExtra("text"));
	}
	private class myAsync extends AsyncTask<String, String, Void> {
		int i = 0;
		@Override
		protected Void doInBackground(String... strings) {
			String s = Crypt.encrypt(strings[0]);
			publishProgress(s);
			s = Crypt.decrypt(s);
			publishProgress(s);
			return null;
		}
		@Override
		protected void onProgressUpdate(String... values) {
			switch (i) {
				case 0:
					encrypted_type0.setText(values[0]);
					break;
				case 1:
					decrypted_type0.setText(values[0]);
					break;
			}
			i++;
		}
	}
}