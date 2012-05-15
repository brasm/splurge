package com.sw802f12.bt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass.Device;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class BTTestActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;
	private static final String TAG = "sw8.BT";
	BluetoothAdapter mBluetoothAdapter;

	private ArrayList<BluetoothDevice> discoveredPeers;
	private ArrayAdapter<String> pairArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blueactivity);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
		}
		
		discoveredPeers = new ArrayList<BluetoothDevice>();

		final Button BluOn = (Button) findViewById(R.id.bt_on);
		BluOn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!mBluetoothAdapter.isEnabled()) {
					Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

				} else {
					Toast.makeText(getApplicationContext(),
							"BT Already Enabled", Toast.LENGTH_SHORT).show();
				}
			}
		});

		final Button BluOff = (Button) findViewById(R.id.bt_off);
		BluOff.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mBluetoothAdapter.isEnabled()) {
					mBluetoothAdapter.disable();
					Toast.makeText(getApplicationContext(), "BT Disabled",
							Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(getApplicationContext(),
							"BT Already Disabled", Toast.LENGTH_SHORT).show();
				}
			}
		});

		final Button DiscoverButton = (Button) findViewById(R.id.disc_button);
		DiscoverButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mBluetoothAdapter.startDiscovery();
			}
		});

		final Button EnableDiscovery = (Button) findViewById(R.id.enable_disc_button);
		EnableDiscovery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent discoverableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(
						BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
				startActivity(discoverableIntent);
			}
		});

		final Button showPeers = (Button) findViewById(R.id.showbut);
		showPeers.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String peerlist = "You can see the following devices: ";
				int counter = 1;
				if (discoveredPeers != null) {
					for (BluetoothDevice m : discoveredPeers) {
						peerlist = peerlist + counter + " - " + m.getName() + "   ";
						counter++;
					}
				} else{
					peerlist = peerlist + "None";
				}
				Toast.makeText(getApplicationContext(), peerlist,
						Toast.LENGTH_SHORT).show();
			}
		});
		
		final Button serverButton = (Button) findViewById(R.id.serv_button);
		serverButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BluetoothService bts = new BluetoothService(getApplicationContext(), mBluetoothAdapter);
				Log.d(TAG, "trying to connect-server");
				bts.start();
			}
		});
		
		final Button clientButton = (Button) findViewById(R.id.client_button);
		clientButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BluetoothService bts = new BluetoothService(getApplicationContext(), mBluetoothAdapter);
				Log.d(TAG, "trying to connect-client");
				for(BluetoothDevice device: discoveredPeers){
					if (device.getName() == "GT-I9100"){
						bts.connect(device);
					}
				}
				
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister
												// during onDestroy
	}

	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			Log.d(TAG, action);
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				Log.d(TAG, "If-statement was true");
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a
				// ListView
				try {
					discoveredPeers.add(device);
					Log.d(TAG, device.getName() + " added to discovered list.");
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Failed adding device to device list.", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "Failed adding device to device list.");
				}
			}
		}
	};

	public void lookForPaired() {
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				pairArrayAdapter.add(device.getName() + "\n"
						+ device.getAddress());
			}
		}
	}

	@Override
	protected void onActivityResult(int request, int result, Intent intent) {
		switch (request) {
		// case REQUEST_CONNECT_DEVICE_SECURE:
		// // When DeviceListActivity returns with a device to connect
		// if (result == Activity.RESULT_OK) {
		// connectDevice(intent, true);
		// }
		// break;
		// case REQUEST_CONNECT_DEVICE_INSECURE:
		// // When DeviceListActivity returns with a device to connect
		// if (result == Activity.RESULT_OK) {
		// connectDevice(intent, false);
		// }
		// break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (result == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				Log.d(TAG, "BT enabled");
				Toast.makeText(this, "BT Enabled", Toast.LENGTH_SHORT).show();

			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, "BT Not Enabled", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
}