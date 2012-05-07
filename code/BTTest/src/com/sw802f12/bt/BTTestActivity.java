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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class BTTestActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;
	private static final String TAG = "sw8.BT";
	private BluetoothAdapter mBluetoothAdapter;

	private ArrayList<BluetoothDevice> discoveredPeers;
	private ArrayAdapter<String> pairArrayAdapter;
	
	protected UUID MY_UUID = new UUID(40000, 800000);

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
				AcceptThread aThread = new AcceptThread();
				aThread.run();
			}
		});
		
		final Button clientButton = (Button) findViewById(R.id.client_button);
		clientButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ConnectThread cThread = new ConnectThread(discoveredPeers.get(0));
				cThread.run();
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
				Log.d(TAG, device.getName());
				String devicename = device.getName();
				// ListView
				Log.d(TAG, devicename);
				discoveredPeers.add(device);
				Log.d(TAG, "Added to list");
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
	
	 
	private class AcceptThread extends Thread {
	    private final BluetoothServerSocket mmServerSocket;
	 
	    public AcceptThread() {
	        // Use a temporary object that is later assigned to mmServerSocket,
	        // because mmServerSocket is final
	        BluetoothServerSocket tmp = null;
	        try {
	            // MY_UUID is the app's UUID string, also used by the client code
	            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("ServerTest", MY_UUID);
	        } catch (IOException e) { }
	        mmServerSocket = tmp;
	    }
	 
	    public void run() {
	        BluetoothSocket socket = null;
	        // Keep listening until exception occurs or a socket is returned
	        while (true) {
	            try {
	                socket = mmServerSocket.accept();
	            } catch (IOException e) {
	                break;
	            }
	            // If a connection was accepted
	            if (socket != null) {
	                // Do work to manage the connection (in a separate thread)
	                manageConnectedSocket(socket);
	                try {
						mmServerSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                break;
	            }
	        }
	    }
	 
	    private void manageConnectedSocket(BluetoothSocket socket) {
			// TODO Auto-generated method stub
	    	Toast.makeText(getApplicationContext(), "Jeg er server.", Toast.LENGTH_SHORT).show();
		}

		/** Will cancel the listening socket, and cause the thread to finish */
	    public void cancel() {
	        try {
	            mmServerSocket.close();
	        } catch (IOException e) { }
	    }
	}

	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) { }
	        mmSocket = tmp;
	    }
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        mBluetoothAdapter.cancelDiscovery();
	 
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
	        manageConnectedSocket(mmSocket);
	    }
	 
	    private void manageConnectedSocket(BluetoothSocket mmSocket2) {
			// TODO Auto-generated method stub
	    	Toast.makeText(getApplicationContext(), "Jeg er client.", Toast.LENGTH_SHORT).show();
		}

		/** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
}