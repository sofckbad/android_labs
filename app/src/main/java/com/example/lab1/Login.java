package com.example.lab1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class Login extends AppCompatActivity {
	private static final int RC_GOOGLE_IN = 0;
	private static final int RC_VK_IN = 282;
	SQLiteDatabase db;
	Cursor userCursor;
	TextView alert;
	GoogleSignInClient mGoogleSignInClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		alert = findViewById(R.id.login_alert);
		alert.setVisibility(View.INVISIBLE);
		db = (new DBHelper(getApplicationContext())).getReadableDatabase();

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
		userCursor.close();
	}
	public void setAlert(String alert_text) {
		alert.setText(alert_text);
		alert.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if ((data == null || !VK.onActivityResult(requestCode, resultCode, data,
				new VKAuthCallback() {
			@Override public void onLogin(@NotNull VKAccessToken vkAccessToken) { startActivity(new Intent(Login.this, MainActivity.class)); }
			@Override public void onLoginFailed(int i) { setAlert("didn't pass vk authorization");
			}}))&&requestCode == RC_VK_IN)
		{ super.onActivityResult(requestCode, resultCode, data); }
		else if (requestCode == RC_GOOGLE_IN) {
			try {
				GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
				startActivity(new Intent(Login.this, MainActivity.class));
			} catch (ApiException e) { setAlert("didn't pass google authorization"); }
		}
	}

	public void button_sign_in_click(View view) {
		String email = ((EditText)findViewById(R.id.login_email)).getText().toString();
		String password = ((EditText)findViewById(R.id.login_password)).getText().toString();

		if (email.length() == 0) setAlert("enter email");
		else if (password.length() == 0) setAlert("enter password");
		else{
			userCursor = db.rawQuery("select * from " + DBHelper.TABLE + " where "
					+ DBHelper.COLUMN_NAME + " = '"+ email + "' and "
					+ DBHelper.COLUMN_PASSWORD + " = '" + password + "'", null);
			if (userCursor.getCount()== 1)
				startActivity(new Intent(Login.this, MainActivity.class));
			else setAlert("no matches");
		}
	}
	public void button_sign_up_click(View view) { startActivity(new Intent(Login.this, Register.class)); }
	public void vkAuthorise(View view) { VK.login(this, Collections.singleton(VKScope.EMAIL)); }
	public void googleAuthorise(View view) { startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_GOOGLE_IN); }
}