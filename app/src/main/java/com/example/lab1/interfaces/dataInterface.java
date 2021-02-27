package com.example.lab1.interfaces;

public interface dataInterface {
	void intoDB(String header, String image, String audio, String text, String coordinates);
	void fromDB();
	void removeItemAt(int position);
}
