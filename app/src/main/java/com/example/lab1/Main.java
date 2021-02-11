package com.example.lab1;

/*
*
* */

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

public class Main extends AppCompatActivity {
	public static final int GOOGLE_REQUEST = 281;
	public static final int VK_REQUEST = 282;
	public static final int IMAGE_REQUEST = 0;
	public static final int AUDIO_REQUEST = 1;
	public static final int REGISTRATION_REQUEST = 2;
	public static String image = null;
	public static String audio = null;

	GoogleSignInClient mGoogleSignInClient;
	ArrayList<String> media = new ArrayList<>();
	int mediaNumWhoPlaying = - 1;
	MediaPlayer mp = null;
	Login login = new Login();
	Register registration = new Register();
	Calculator calculator = new Calculator();
	ContentScroller contentScroller = new ContentScroller();
	public Elements elements = new Elements();
	public AddNewElement addNewElement = new AddNewElement();

	Toast mainToast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
		getSupportFragmentManager().beginTransaction().add(R.id.main_content, login).commit();

		mainToast = Toast.makeText(this, "", Toast.LENGTH_LONG);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
		}

		mp = new MediaPlayer();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,Intent data) {
		switch (requestCode){
			case VK_REQUEST:
				VK.onActivityResult(requestCode, resultCode, data, new VKAuthCallback() {
					@Override
					public void onLogin(@NotNull VKAccessToken vkAccessToken) { switchFragment(R.id.main_content, calculator); }

					@Override
					public void onLoginFailed(int i) { mainToast.setText("didn't pass vk authorization");
						if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
							mainToast.show();
					}});
				break;
			case GOOGLE_REQUEST:
				try {
					GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
					switchFragment(R.id.main_content, calculator);
				} catch (ApiException e) {
					mainToast.setText("didn't pass google authorization");
					if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
						mainToast.show();
				}
				break;
			case IMAGE_REQUEST:
				if (resultCode == RESULT_OK){
					((ImageButton) findViewById(R.id.addImage)).setImageURI(data.getData());
					image = data.getData().toString();
				}
				break;
			case AUDIO_REQUEST:
				if (resultCode == RESULT_OK) {
					((Button) findViewById(R.id.addMusic)).setText(data.getData().toString().split(".+document/")[1]);
					audio = data.getData().toString();
				}
				break;
			case REGISTRATION_REQUEST:
				if (resultCode == RESULT_OK) {
					switchFragment(R.id.main_content, calculator);
					mainToast.setText("Account was created");
				} else {
					mainToast.setText("Email is busy");
				}
				if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
					mainToast.show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void switchFragment(int id, Fragment fragment) {
		getSupportFragmentManager().beginTransaction().replace( id, fragment).commitAllowingStateLoss();
	}

	public void clickFunction(View view) {
		calculator.calculator_buttons(view);
	}

	public void register_sign_up_click(View view) {
		String email = ((EditText) this.findViewById(R.id.register_email)).getText().toString();
		String password = ((EditText) this.findViewById(R.id.register_password)).getText().toString();
		String confirm_password = ((EditText) this.findViewById(R.id.register_confirm_password)).getText().toString();

		email = email.toLowerCase();

		mainToast.setText("Unknown problem\nInform developers");

		if (!Pattern.matches("[a-z][a-z0-9]{2,19}@[a-z]{2,15}\\.[a-z]{2,10}",email)) {
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
			Intent intent = new Intent(this, InsertIntoDB.class);
			intent.putExtra("table", DBHelper.USERS);
			intent.putExtra("email", email);
			intent.putExtra("password", password);
			startActivityForResult(intent, REGISTRATION_REQUEST);
		}
	}

	public void register_go_back_click(View view) {
		this.switchFragment(R.id.main_content, login);
	}

	public void login_sign_in_click(View view) {
		String email = ((EditText) this.findViewById(R.id.login_email)).getText().toString();
		String password = ((EditText) this.findViewById(R.id.login_password)).getText().toString();
		SQLiteDatabase db = (new DBHelper(this)).getWritableDatabase();
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
				this.switchFragment(R.id.main_content, calculator);
			} else {
				mainToast.setText("User is not registered");
				if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
					mainToast.show();
			}

			db.close();
			userCursor.close();
		}
	}

	public void login_sign_up_click(View view) { this.switchFragment(R.id.main_content, registration); }

	public void vkAuthorise(View view) { VK.login(this, Collections.singleton(VKScope.EMAIL)); }

	public void googleAuthorise(View view) { startActivityForResult(mGoogleSignInClient.getSignInIntent(), GOOGLE_REQUEST); }

	public void addIntoFragment(String image, String audio) {
		ImageButton b = (ImageButton) getLayoutInflater().inflate(R.layout.button, null);
		b.setId(((LinearLayout) findViewById(R.id.content)).getChildCount());
		try {
//			b.setImageURI(Uri.parse(image));
			b.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver() ,Uri.parse(image)));
		} catch (NullPointerException | IOException e) {
			e.printStackTrace();
			System.out.println(image);
		}
		b.setLayoutParams(new ViewGroup.LayoutParams(
				Math.round(getResources().getDimension(R.dimen.width)),
				Math.round(getResources().getDimension(R.dimen.height))));
		media.add(audio);
		b.setOnClickListener(view -> {
			if (mediaNumWhoPlaying != view.getId()) {
				try {
					mp.reset();
					mp.setDataSource(this, ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Integer.parseInt(media.get(view.getId()).split("%3A")[1])));
//					mp.setDataSource(this, Uri.parse(media.get(view.getId())));
//					mp.setDataSource(this, Uri.parse(media.get(view.getId())));
					mp.prepare();
					mp.start();
					mediaNumWhoPlaying = view.getId();
					System.out.println(media.get(view.getId()));
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println(media.get(view.getId()));
				}
			} else {
				if (mp.isPlaying()) mp.pause();
				else mp.start();
			}
		});
		((LinearLayout) findViewById(R.id.content)).addView(b, 0);
	}
}