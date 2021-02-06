package com.example.lab1;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContentScroller extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_content_scroller, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		((Main) getActivity()).switchFragment(R.id.allElements, ((Main) getActivity()).elements);
		((Main) getActivity()).switchFragment(R.id.addElement, ((Main) getActivity()).addNewElement);
	}
}