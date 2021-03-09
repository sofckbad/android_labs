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
	TextView encrypted_type1;
	TextView decrypted_type1;
	TextView encrypted_type2;
	TextView decrypted_type2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crypto);

		defaultText = findViewById(R.id.default_text);
		encrypted_type0 = findViewById(R.id.encrypted_type0);
		decrypted_type0 = findViewById(R.id.decrypted_type0);
		encrypted_type1 = findViewById(R.id.encrypted_type1);
		decrypted_type1 = findViewById(R.id.decrypted_type1);
		encrypted_type2 = findViewById(R.id.encrypted_type2);
		decrypted_type2 = findViewById(R.id.decrypted_type2);

		defaultText.setText(getIntent().getStringExtra("text"));

		new myAsync().execute(getIntent().getStringExtra("text"));
	}
	private class myAsync extends AsyncTask<String, String, Void> {
		@Override
		protected Void doInBackground(String... strings) {
			String s;
			s = Crypt.encryptLib(strings[0]);
			publishProgress(s, "0");
			publishProgress(Crypt.decryptLib(s), "1");
			s = Crypt.encryptVigenere(strings[0]);
			publishProgress(s, "2");
			publishProgress(Crypt.decryptVigenere(s), "3");
			s = Crypt.encryptHuffman(strings[0]);
			publishProgress(s, "4");
			publishProgress(Crypt.decryptHuffman(s), "5");
			return null;
		}
		@Override
		protected void onProgressUpdate(String... values) {
			switch (values[1]) {
				case "0":
					encrypted_type0.setText(values[0]);
					break;
				case "1":
					decrypted_type0.setText(values[0]);
					break;
				case "2":
					encrypted_type1.setText(values[0]);
					break;
				case "3":
					decrypted_type1.setText(values[0]);
					break;
				case "4":
					encrypted_type2.setText(values[0]);
					break;
				case "5":
					decrypted_type2.setText(values[0]);
					break;
			}
		}
	}
}