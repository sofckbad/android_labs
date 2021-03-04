package com.example.lab1.interfaces;

import android.content.Intent;

import com.example.lab1.RecyclerHolder;

public interface dataInterface {
	void fromVK();

	void intoDB(String header, String image, String audio, String text, String coordinates);
	void fromDB();
	void removeItemAt(RecyclerHolder holder);
	void change(Intent data);
}
