package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
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

import java.util.ArrayList;
import java.util.Collections;

public class Main extends AppCompatActivity {

	public static Application application;

	public static final int GOOGLE_REQUEST = 281;
	public static final int VK_REQUEST = 282;
	public static final int IMAGE_REQUEST = 0;
	public static final int AUDIO_REQUEST = 1;
	public static final int REGISTRATION_REQUEST = 2;
	public static final int ADD_REQUEST = 3;

	public static String header = null;
	public static String image = null;
	public static String audio = null;
	public static String text = null;


	public static ArrayList<String> mediaArray = new ArrayList<>();
	public static ArrayList<String> textArray = new ArrayList<>();
	public static ArrayList<String> headerArray = new ArrayList<>();
	public static ArrayList<String> imageArray = new ArrayList<>();

	public static MediaPlayer mp = null;
	public static SeekBar currentSeekBar = null;
	public static Button currentButton = null;
	public static int numWhoPlaying = -1;

	GoogleSignInClient mGoogleSignInClient;

	Login login = new Login();
	Register registration = new Register();
	RecyclerFragment recyclerFragment = new RecyclerFragment();

	Toast mainToast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		application = getApplication();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
		getSupportFragmentManager().beginTransaction().add(R.id.main_content, login).commit();

		mainToast = Toast.makeText(this, "", Toast.LENGTH_LONG);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
		}

//		image = "content://media/external/images/media/85938";
//		audio = "content://com.android.providers.media.documents/document/audio%3A72426";
//		text = "https://overcoder.net/q/4076/%D0%BA%D0%B0%D0%BA-%D1%81%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C-%D1%81%D1%81%D1%8B%D0%BB%D0%BA%D0%B8-%D0%B2-textview-%D0%BA%D0%BB%D0%B8%D0%BA%D0%B0%D0%B1%D0%B5%D0%BB%D1%8C%D0%BD%D1%8B%D0%BC%D0%B8";

		mp = new MediaPlayer();
		fromDB();


		Thread t = new Thread() {
			@Override
			public void run() {
				while (mp != null)
				{
					try {
						if (currentSeekBar != null)
						currentSeekBar.setProgress(mp.getCurrentPosition());
						Thread.sleep(1000);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode,Intent data) {
		switch (requestCode){
			case VK_REQUEST:
				VK.onActivityResult(requestCode, resultCode, data, new VKAuthCallback() {
					@Override
					public void onLogin(@NotNull VKAccessToken vkAccessToken) { switchFragment(R.id.main_content, recyclerFragment); }

					@Override
					public void onLoginFailed(int i) { mainToast.setText("didn't pass vk authorization");
						if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
							mainToast.show();
					}});
				break;
			case GOOGLE_REQUEST:
				try {
					GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
					switchFragment(R.id.main_content, recyclerFragment);
				} catch (ApiException e) {
					mainToast.setText("didn't pass google authorization");
					if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
						mainToast.show();
				}
				break;
			case ADD_REQUEST:
				if (resultCode == RESULT_OK){
					header = data.getStringExtra("header");
					image = data.getStringExtra("image");
					audio = data.getStringExtra("audio");
					text = data.getStringExtra("text");
					intoDB(header ,image, audio, text);
				} else {
					mainToast.setText("Проблемы с добавлением поста");
					if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
						mainToast.show();
				}
				break;
			case REGISTRATION_REQUEST:
				if (resultCode == RESULT_OK) {
					switchFragment(R.id.main_content, recyclerFragment);
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

	public void register_sign_up_click(View view) {
		registration.signUp(view, mainToast);
	}

	public void register_go_back_click(View view) {
		registration.signIn();
	}

	public void login_sign_in_click(View view) {
		login.signIn(mainToast);
	}

	public void login_sign_up_click(View view) {
		login.signUp();
	}

	public void vkAuthorise(View view) { VK.login(this, Collections.singleton(VKScope.EMAIL)); }

	public void googleAuthorise(View view) { startActivityForResult(mGoogleSignInClient.getSignInIntent(), GOOGLE_REQUEST); }

	public void fromDB() {
		SQLiteDatabase db = (new DBHelper(this)).getWritableDatabase();
		Cursor userCursor;
		userCursor = db.rawQuery("select * from " + DBHelper.DATA, null);

		userCursor.moveToFirst();
		while (!userCursor.isAfterLast()) {
			headerArray.add(userCursor.getString(1));
			imageArray.add(userCursor.getString(2));
			mediaArray.add(userCursor.getString(3));
			textArray.add(userCursor.getString(4));
			userCursor.moveToNext();
		}

		db.close();
		userCursor.close();
	}

	public void intoDB(String header,String image, String audio, String text) {
		if (image == null || audio == null || text == null) return;
		SQLiteDatabase db = (new DBHelper(this)).getWritableDatabase();
		db.execSQL("INSERT INTO " + DBHelper.DATA + " (" + DBHelper.COLUMN_HEADER + ", " + DBHelper.COLUMN_IMAGE + ", " + DBHelper.COLUMN_MUSIC + ", " + DBHelper.COLUMN_TEXT + ") VALUES (?, ?, ?, ?)", new Object[] { header ,image, audio, text });

		recyclerFragment.recyclerAdapter.addItem();

		mainToast.setText("Пост добавлен");
		if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
			mainToast.show();

		headerArray.add(header);
		imageArray.add(image);
		mediaArray.add(audio);
		textArray.add(text);

		recyclerFragment.recyclerAdapter.add(1);

		Main.header = null;
		Main.image = null;
		Main.audio = null;
		Main.text = null;

		db.close();
	}

	public void addPost(View view) {
		startActivityForResult(new Intent(this, AddPost.class), ADD_REQUEST);
	}


}