package com.example.lab1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab1.LoadFromVK;
import com.example.lab1.R;
import com.example.lab1.RecyclerAdapter;
import com.example.lab1.activities.Main;
import com.example.lab1.activities.SortActivity;
import com.google.firebase.database.IgnoreExtraProperties;

public class RecyclerFragment extends Fragment {

	public RecyclerAdapter recyclerAdapter;
	public RecyclerView recyclerView;
	public static int backButton = 0;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		recyclerAdapter = new RecyclerAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_elements, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		OnBackPressedCallback callback = new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				switch (backButton) {
					case 1:
						backButton--;
					case 0:
						Toast.makeText(getActivity(), "Еще раз для выхода", Toast.LENGTH_SHORT).show();
						backButton--;
						new CountDownTimer(2000, 1000) {
							public void onTick(long millisUntilFinished) { }
							public void onFinish() { backButton = 0; }}.start();
					break;
					case -1:

						getActivity().moveTaskToBack(true);
						break;
					default:

						break;
				}
			}
		};
		requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

		initRecyclerAdapter();

		recyclerAdapter.setCountOfElement(recyclerAdapter.dataLink.size());

		getActivity().findViewById(R.id.sortButton).setOnClickListener(v -> {
			Intent intent = new Intent(getContext(), SortActivity.class);
			getActivity().startActivityForResult(intent, Main.SORT_REQUEST);
		});
		getActivity().findViewById(R.id.refresh).setOnClickListener(v -> {
			if (Main.VK_TOKEN == null) {
				Main.activity.vkAuthorise(null);
			} else {
				LoadFromVK asyncTask = new LoadFromVK();
				asyncTask.execute();
			}
		});

		Main.dataLoader.fromDB();
		recyclerAdapter.setCountOfElement(recyclerAdapter.dataLink.size());
		recyclerAdapter.notifyDataSetChanged();
	}

	public void initRecyclerAdapter() {
		recyclerView = getActivity().findViewById(R.id.recycler);
		recyclerView.setLayoutManager(new LinearLayoutManager(null, LinearLayoutManager.VERTICAL,
				false));
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(recyclerAdapter);

		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
				ItemTouchHelper.LEFT) {

			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
								  @NonNull RecyclerView.ViewHolder viewHolder1) {
				return false;
			}

			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
				Main.dataLoader.removeItemAt(viewHolder.getAdapterPosition());
			}
		});
		itemTouchHelper.attachToRecyclerView(recyclerView);
	}
}
