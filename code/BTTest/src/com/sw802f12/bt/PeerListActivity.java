package com.sw802f12.bt;

import android.R.string;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PeerListActivity extends ListActivity {
	

	private ArrayAdapter<String> discArrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		discArrayAdapter.addAll(this.getIntent().getStringArrayListExtra("Peerlist"));
		setListAdapter(discArrayAdapter);
	}

	
	
}
