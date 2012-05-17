package dk.aau.sw802f12.proto3;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import dk.aau.sw802f12.proto3.R.id;

public class MainActivity extends Activity {
	public static String tag = "SW8PLAYER";
	private static final int REQUEST_ENABLE_BT = 1;
	private SeekBar volumeBar;
	private ProgressBar progressBar;
	private AudioManager am;
	
	BluetoothAdapter mBluetoothAdapter;
	private PlayService player;
	
	private ArrayList<BluetoothDevice> discoveredPeers;
	
	private TextView currentSongText;
	private TextView song1;
	private TextView song2;
	private TextView song3;
	private TextView song4;
	private Button toggleButton;
	private Button connectionSettingButton;
    
	private Intent intent = new Intent();
	
	private boolean playerBound = false;
	
	@Override
	protected void onStart() {
		super.onStart();
		Intent serviceIntent = new Intent(this, PlayService.class);
		startService(serviceIntent);
		bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
		
		registerReceiver(updateInterface, new IntentFilter(PlayService.UPDATESTATE));
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (playerBound) {
			unbindService(mConnection);
		}
		
		unregisterReceiver(updateInterface);
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
		}
     
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeBar = (SeekBar) findViewById(R.id.volume);
        progressBar = (ProgressBar) findViewById(R.id.songprogressbar);
        
        discoveredPeers = new ArrayList<BluetoothDevice>();
        
        SeekbarListener sbl = new SeekbarListener();
        volumeBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volumeBar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
		volumeBar.setOnSeekBarChangeListener(sbl);
		
		toggleButton = (Button) findViewById(id.toggleplayerstatus);
		toggleButton.setOnClickListener(toggleListener);
		
		currentSongText = (TextView) findViewById(id.trackName);
		song1 = (TextView) findViewById(id.song1_text);
		song2 = (TextView) findViewById(id.song2_text);
		song3 = (TextView) findViewById(id.song3_text);
		song4 = (TextView) findViewById(id.song4_text);
    }
    
    public void onResume(Bundle savedInstanceState) {
    	super.onResume();
    	// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister
												// during onDestroy
    }
    
    public void onPause(Bundle savedInstanceState) {
    	super.onPause();
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
    
    private class SeekbarListener implements SeekBar.OnSeekBarChangeListener {
		public void onStopTrackingTouch(SeekBar seekBar) { }
		
		public void onStartTrackingTouch(SeekBar seekBar) { }
		
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				Log.d(tag, "Changing volume to " + progress);
				am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			}
		}
	};
	
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			player = ((PlayService.LocalBinder) service).getService();
			if (player != null) playerBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private BroadcastReceiver updateInterface = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(getApplicationContext(), "Received broadcast", Toast.LENGTH_SHORT).show();
			if (intent.getAction() != PlayService.UPDATESTATE) return;
			
			Toast.makeText(getApplicationContext(), "Received Update broadcast", Toast.LENGTH_SHORT).show();
			
			try {
				currentSongText.setText(intent.getStringExtra("current"));
				song1.setText(intent.getStringExtra("song1"));
				song2.setText(intent.getStringExtra("song2"));
				song3.setText(intent.getStringExtra("song3"));
				song4.setText(intent.getStringExtra("song4"));
			} catch (NullPointerException e) {
				currentSongText.setText("");
				song1.setText("");
				song2.setText("");
				song3.setText("");
				song4.setText("");
			}
		}
	};
	
	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			Log.d(tag, action);
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				Log.d(tag, "If-statement was true");
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a
				// ListView
				try {
					discoveredPeers.add(device);
					Log.d(tag, device.getName() + " added to discovered list.");
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Failed adding device to device list.", Toast.LENGTH_SHORT).show();
					Log.d(tag, "Failed adding device to device list.");
				}
			}
		}
	};
	
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
				Log.d(tag, "BT enabled");
				Toast.makeText(this, "BT Enabled", Toast.LENGTH_SHORT).show();

			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(tag, "BT not enabled");
				Toast.makeText(this, "BT Not Enabled", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
	
	private OnClickListener toggleListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (playerBound) player.toggle();
			else Toast.makeText(getApplicationContext(), "Player not bound yet", Toast.LENGTH_SHORT);
		}
	};
	
	private OnClickListener connectionSettingListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (playerBound) player.toggle();
			else Toast.makeText(getApplicationContext(), "Player not bound yet", Toast.LENGTH_SHORT);
		}
	};
	
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setMessage("Are you sure you want to exit?");
	builder.setCancelable(false);
	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                MyActivity.this.finish();
	           }
	       });
	builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	           }
	       });
	AlertDialog alert = builder.create();
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
				volumeBar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
				volumeBar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
				return true;
			default:
				return super.onKeyDown(keyCode, event);
		}		
	}
}