package com.example.lab1;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
	Geocoder geocoder;
	List<Address> addresses;
	boolean b = false;
	float zoom = 15f;

	Toast toast;

	DecimalFormat df = new DecimalFormat("###.##");

	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		geocoder = new Geocoder(this, Locale.getDefault());
		mMap = googleMap;
		LatLng latLng = null;
		try {
			String[] str = getIntent().getData().toString().split(",");
			String[] str1 = str[0].split("geo:");
			latLng = new LatLng(Double.parseDouble(str1[1]), Double.parseDouble(str[1]));
		} catch (NullPointerException e) {
			finish();
		}
		try {
			addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
		} catch (IOException e) { e.printStackTrace(); }
		MarkerOptions mo = new MarkerOptions();
		mo.position(latLng);
		if (addresses.size() != 0)
			mo.title("see u here " + df.format(latLng.latitude) + ":" + df.format(latLng.longitude) + " " + addresses.get(0).getLocality() + " " + addresses.get(0).getCountryName());
		else
			mo.title("see u here " + df.format(latLng.latitude) + ":" + df.format(latLng.longitude));
		mMap.addMarker(mo);
		mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
		mMap.setOnMapClickListener(latlng -> {
			MarkerOptions markerOptions = new MarkerOptions();
			try {
				addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
			} catch (IOException e) { e.printStackTrace(); }
			markerOptions.position(latlng);
			if (addresses.size() != 0) {
				markerOptions.title("see u here " + df.format(latlng.latitude) + ":" + df.format(latlng.longitude) + " " + addresses.get(0).getLocality() + " " + addresses.get(0).getCountryName());
				getIntent().putExtra("city", addresses.get(0).getLocality());
				getIntent().putExtra("country", addresses.get(0).getCountryName());
			} else
				mo.title("see u here " + df.format(latlng.latitude) + ":" + df.format(latlng.longitude));
			mMap.clear();
			mMap.addMarker(markerOptions);
			getIntent().putExtra("newCoords", markerOptions.getPosition().latitude + "," + markerOptions.getPosition().longitude);
			setResult(RESULT_OK, getIntent());
			if (toast.getView().getWindowVisibility() != View.VISIBLE) {
				toast.setText("Новый маркер будет сохранен");
				toast.show();
			}
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
		});
//		mMap.setOnMarkerClickListener(marker -> {
//			if (mMap.getCameraPosition().zoom == zoom) {
//				if (b) {
//					marker.hideInfoWindow();
//					b = false;
//				} else {
//					marker.showInfoWindow();
//					b = true;
//				}
//			} else {
//				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoom));
//			}
//			return true;
//		});
	}
}