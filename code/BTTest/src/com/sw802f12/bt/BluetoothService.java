package com.sw802f12.bt;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class BluetoothService {
	private static final String TAG = "SW8.bt";
	static final UUID MY_UUID = UUID.fromString("99E67F40-9849-11E1-A8B0-0800200C9A66");
	
	private Context context;
	private BluetoothAdapter mBluetoothAdapter;
	
	public BluetoothService(Context context, BluetoothAdapter btAdapter) {
		this.context = context;
		mBluetoothAdapter = btAdapter;
	}
	
	public synchronized void start() {
		AcceptThread aThread = new AcceptThread();
		aThread.start();
	}
	
	public synchronized void connect(BluetoothDevice bd) {
		ConnectThread cThread = new ConnectThread(bd);
		cThread.start();
		cThread.cancel();
	}

	private class AcceptThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;
		 
	    public AcceptThread() {
	        // Use a temporary object that is later assigned to mmServerSocket,
	        // because mmServerSocket is final
	        BluetoothServerSocket tmp = null;
	        try {
	            // MY_UUID is the app's UUID string, also used by the client code
	            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BTserviceTest", MY_UUID);
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
						e.printStackTrace();
					}
	                break;
	            }
	        }
	    }
	    
	    private void manageConnectedSocket(BluetoothSocket socket) {
	    	Log.d(TAG, "Connection as server OK - transmitting data");
	    	Toast.makeText(context, "Server connected!", Toast.LENGTH_SHORT);
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
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	 
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
	        	Log.d(TAG, "Connecting to BT peer.");
	            mmSocket.connect();
	            Log.d(TAG, "Connection OK");
	        } catch (IOException connectException) {
	        	Log.d(TAG, "Failed to connect" + connectException.getMessage());
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
	        manageConnectedSocket(mmSocket);
	    }
	    
	    private void manageConnectedSocket(BluetoothSocket socket) {
	    	Log.d(TAG, "Connected.");
	    	Toast.makeText(context, "Client connected.", Toast.LENGTH_SHORT);
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
}
