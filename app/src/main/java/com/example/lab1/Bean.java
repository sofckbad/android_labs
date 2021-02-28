package com.example.lab1;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Bean {
	public String  link;
	public String media;
	public String text;
	public String header;
	public String image;
	public String coordinates;
	public String name;
	public String date;
	public Bean(String media, String text, String header, String image, String coordinates, String  name, String date) {
		this.media = media;
		this.text = text;
		this.header = header;
		this.image = image;
		this.coordinates = coordinates;
		this.name = name;
		this.date = date;
	}
	public Bean() {};
}
