package com.example.lab1.interfaces;

import android.content.Intent;

public interface dataInterface {
	void intoDB(String header, String image, String audio, String text, String coordinates);
	void fromDB();
	void removeItemAt(int position);
	void change(Intent data);
}
