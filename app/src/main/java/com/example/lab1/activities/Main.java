package com.example.lab1.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.lab1.DBHelper;
import com.example.lab1.InDataBase;
import com.example.lab1.fragments.Login;
import com.example.lab1.R;
import com.example.lab1.fragments.RecyclerFragment;
import com.example.lab1.fragments.Register;
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
	public static Main activity;

	public static final int GOOGLE_REQUEST = 281;
	public static final int VK_REQUEST = 282;
	public static final int IMAGE_REQUEST = 0;
	public static final int AUDIO_REQUEST = 1;
	public static final int REGISTRATION_REQUEST = 2;
	public static final int ADD_REQUEST = 3;
	public static final int MAP_REQUEST = 4;
	public static final int CHANGE_REQUEST = 5;

	public static String header = null;
	public static String image = null;
	public static String audio = null;
	public static String text = null;
	public static String coordinates = null;


	public static ArrayList<String> mediaArray = new ArrayList<>();
	public static ArrayList<String> textArray = new ArrayList<>();
	public static ArrayList<String> headerArray = new ArrayList<>();
	public static ArrayList<String> imageArray = new ArrayList<>();
	public static ArrayList<String> coordinatesArray = new ArrayList<>();
	public static ArrayList<String> nameArray = new ArrayList<>();

	public static MediaPlayer mp = null;
	public static InDataBase dataLoader = new InDataBase();
	public static SeekBar currentSeekBar = null;
	public static Button currentButton = null;
	public static int numWhoPlaying = - 1;
	public static int idCurrentUser = 1;
	public static String curName = "admin";
	public boolean isAdmin = true;

	GoogleSignInClient mGoogleSignInClient;

	public Login login = new Login();
	public Register registration = new Register();
	public RecyclerFragment recyclerFragment = new RecyclerFragment();

	public Toast mainToast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		application = getApplication();
		activity = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
		switchFragment(R.id.main_content, login);

		mainToast = Toast.makeText(this, "", Toast.LENGTH_LONG);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
		}

		mp = new MediaPlayer();

		dataLoader.fromDB();

		Thread t = new Thread() {
			@Override
			public void run() {
				while (mp != null) {
					try {
						if (currentSeekBar != null)
							currentSeekBar.setProgress(mp.getCurrentPosition());
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case VK_REQUEST:
				//TODO auth token vk
				VK.onActivityResult(requestCode, resultCode, data, new VKAuthCallback() {
					@Override
					public void onLogin(@NotNull VKAccessToken vkAccessToken) { switchFragment(R.id.main_content, recyclerFragment); }

					@Override
					public void onLoginFailed(int i) {
						mainToast.setText("didn't pass vk authorization");
						if (mainToast.getView().getWindowVisibility() != View.VISIBLE)
							mainToast.show();
					}
				});
				break;
			case GOOGLE_REQUEST:
				try {
					GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
					switchFragment(R.id.main_content, recyclerFragment);
				} catch (ApiException e) {
					mainToast.setText("didn't pass google authorization");
					if (mainToast.getView().getWindowVisibility() != View.VISIBLE) mainToast.show();
				}
				break;
			case ADD_REQUEST:
				if (resultCode == RESULT_OK) {
					header = data.getStringExtra("header");
					image = data.getStringExtra("image");
					audio = data.getStringExtra("audio");
					text = data.getStringExtra("text");
					coordinates = data.getStringExtra("coordinates");
					dataLoader.intoDB(header, image, audio, text, coordinates);
				}
				break;
			case REGISTRATION_REQUEST:
				if (resultCode == RESULT_OK) {
					switchFragment(R.id.main_content, recyclerFragment);
					mainToast.setText("Account was created");
				} else {
					mainToast.setText("Email is busy");
				}
				if (mainToast.getView().getWindowVisibility() != View.VISIBLE) mainToast.show();
				break;
			case MAP_REQUEST:
				SQLiteDatabase db = (new DBHelper(this)).getWritableDatabase();
				if (resultCode == RESULT_OK) {
					int i = - 1;
					i = data.getIntExtra("position", i);
					db.execSQL("update " + DBHelper.DATA + " set " + DBHelper.COLUMN_COORDINATES + " = ? where " + DBHelper.COLUMN_COORDINATES + " = '" + data.getData().toString().split("geo:")[1] + "'", new Object[] { data.getStringExtra("newCoords") });
					coordinatesArray.set(i, data.getStringExtra("newCoords"));
//					i = data.getIntExtra("position", -1);
					Toast.makeText(this, "Метка сохранена", Toast.LENGTH_SHORT).show();
					recyclerFragment.recyclerAdapter.notifyItemChanged(i);
				}
				db.close();
				break;
			case CHANGE_REQUEST:
				if (resultCode == RESULT_OK) {
					dataLoader.change(data);
					int i = data.getIntExtra("position", - 1);
					headerArray.set(i, data.getStringExtra("header"));
					imageArray.set(i, data.getStringExtra("image"));
					textArray.set(i, data.getStringExtra("text"));
					mediaArray.set(i, data.getStringExtra("audio"));
					coordinatesArray.set(i, data.getStringExtra("coordinates"));

					if (numWhoPlaying == i) {
						mp.reset();
						numWhoPlaying = -1;
					}
					recyclerFragment.recyclerAdapter.notifyItemChanged(i);
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void switchFragment(int id, Fragment fragment) {
		getSupportFragmentManager().beginTransaction().replace(id, fragment).commitAllowingStateLoss();
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

	public void addPost(View view) {
		startActivityForResult(new Intent(this, AddPost.class), ADD_REQUEST);
//		startActivity(new Intent(this, MapsActivity.class));
	}


}