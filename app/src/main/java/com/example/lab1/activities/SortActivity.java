package com.example.lab1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.lab1.Bean;
import com.example.lab1.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class SortActivity extends AppCompatActivity {

	Spinner spinner;
	int sortByDate = 0;
	Button dateSort;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sort);

		setResult(RESULT_CANCELED);

		HashSet<String> nms = new HashSet<>();
		for (Bean bean: Main.data)
		    nms.add(bean.name);
		ArrayList<String> names = new ArrayList<>(nms);

		names.add(0, "all");

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner = findViewById(R.id.spinner);
		dateSort = findViewById(R.id.date);

		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				getIntent().putExtra("name", names.get(position));
				setResult(RESULT_OK, getIntent());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	public void sortByDate(View view) {
		if (sortByDate == 0 || sortByDate == 2){
			sortByDate = 1;
			dateSort.setText("newer");
		}
		else {
			sortByDate = 2;
			dateSort.setText("older");
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Main.data.sort((o1, o2) -> (sortByDate==1?-1:1) * o1.date.compareTo(o2.date));
		} else {
			for (int i = 0; i < Main.data.size() - 1; i++){
				for (int j = i + 1; j < Main.data.size(); j++) {
					if (Main.data.get(i).date.compareTo(Main.data.get(j).date) * (sortByDate==1?-1:1) > 0) {
						Collections.swap(Main.data, i, j);
					}
				}
			}
		}
		setResult(RESULT_OK);
	}

	public void apply(View view) {
		finish();
	}
}