package dk.aau.sw802f12.splurge;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import dk.aau.sw802f12.splurge.R;
import dk.aau.sw802f12.splurge.R.id;
import dk.aau.sw802f12.splurge.database.MusicRegistry;
import dk.aau.sw802f12.splurge.network.NetworkService;
import dk.aau.sw802f12.splurge.player.PlayService;

public class MainActivity extends Activity {
	
	Context mContext;
	public static String tag = "SW8PLAYER";
	private static final int REQUEST_ENABLE_BT = 1;
	private SeekBar volumeBar;
	private ProgressBar progressBar;
	private AudioManager am;
	private AlertDialog serverDialog;
	private AlertDialog stopServerDialog;
	private AlertDialog clientDialog;
	private AlertDialog discDialog;
	private AlertDialog lastFMDialog;
	EditText input_serverName;
	EditText input_clientServerName;
	EditText input_LastFM;
	private String clientServerName;
	private String lastFMUser;    
	NetworkService netService;
	BluetoothAdapter mBluetoothAdapter;
	private PlayService player;
	private ArrayList<BluetoothDevice> discoveredPeers;
	private TextView currentSongText;
	private TextView song1;
	private TextView song2;
	private TextView song3;
	private TextView song4;
	private Button toggleButton;
	private Button nextButton;
	private Button startServerButton;
	private Button startClientButton;
	private Button lastFMButton;
	private boolean playerBound = false;
	
	@Override
	protected void onStart() {
		super.onStart();
		// initialize database connection
		MusicRegistry.getInstance(getApplicationContext());
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
        mContext = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
		}
		IntentFilter deviceFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		Log.d(tag, "Registering deviceReceiver");
		registerReceiver(deviceReceiver, deviceFilter); // Don't forget to unregister during onDestroy
		IntentFilter adapterFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		Log.d(tag, "Registering adapterReceiver");
		registerReceiver(adapterReceiver, adapterFilter); // Don't forget to unregister during onDestroy
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeBar = (SeekBar) findViewById(R.id.volume);
        progressBar = (ProgressBar) findViewById(R.id.songprogressbar);
    	serverDialog = new AlertDialog.Builder(this).create();
    	clientDialog = new AlertDialog.Builder(this).create();
    	lastFMDialog = new AlertDialog.Builder(this).create();
    	stopServerDialog = new AlertDialog.Builder(this).create();
    	discDialog = new AlertDialog.Builder(this).create();
    	input_serverName = new EditText(this);
    	input_clientServerName = new EditText(this);
    	input_LastFM = new EditText(this);
    	lastFMUser = null;
        discoveredPeers = new ArrayList<BluetoothDevice>();
        SeekbarListener sbl = new SeekbarListener();
        volumeBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volumeBar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
		volumeBar.setOnSeekBarChangeListener(sbl);
		toggleButton = (Button) findViewById(id.toggleplayerstatus);
		toggleButton.setOnClickListener(toggleListener);
		nextButton = (Button) findViewById(id.next_button);
		nextButton.setOnClickListener(nextSongListener);
		startServerButton = (Button) findViewById(id.start_server_button);
		startServerButton.setOnClickListener(startServerListener);
		startClientButton = (Button) findViewById(id.start_client_button);
		startClientButton.setOnClickListener(startClientListener);
		lastFMButton = (Button) findViewById(id.lastfm_button);
		lastFMButton.setOnClickListener(lastFMListener);		
		currentSongText = (TextView) findViewById(id.trackName);
		song1 = (TextView) findViewById(id.song1_text);
		song2 = (TextView) findViewById(id.song2_text);
		song3 = (TextView) findViewById(id.song3_text);
		song4 = (TextView) findViewById(id.song4_text);
    }
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(bcr, new IntentFilter(PlayService.UPDATE_INFO));
	}
    
	public void onPause() {
		super.onPause();
		unregisterReceiver(bcr);
	}
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(tag, "Unregistering deviceReceiver");
		unregisterReceiver(deviceReceiver);
		Log.d(tag, "Unregistering adapterReceiver");
		unregisterReceiver(adapterReceiver);
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
			//Not handled by current prototype.
		}
	};
	
	private BroadcastReceiver updateInterface = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() != PlayService.UPDATESTATE) return;
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
	private final BroadcastReceiver deviceReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			Log.d(tag, action);
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				Log.d(tag, "If-statement was true");
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
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
	
	// Create a BroadcastReceiver for ACTION_DISCOVERY
		private final BroadcastReceiver adapterReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device
				Log.d(tag, action);
				if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
					netService = new NetworkService(getApplicationContext(), mBluetoothAdapter);
			    	  Log.d(tag, "Trying to connect as client.");
			    	  boolean bo = false;
			    	  for(BluetoothDevice device: discoveredPeers){
							Log.d(tag, "DEVICE NAME: " + device.getName());
							if (device.getName().equals(clientServerName)) {
								Log.d(tag, "Discovered, OK.");
								netService.connect(device, lastFMUser);
								startClientButton.setText(mContext.getText(R.string.disc_client));
					    	  	startServerButton.setVisibility(View.GONE);
					    	  	lastFMButton.setVisibility(View.GONE);
					    	  	startClientButton.setOnClickListener(disconnectListener);
					    	  	bo = true;
							}
						}
			    	  if(!bo){
			    		  Toast.makeText(mContext, "Could not find server with name: " + clientServerName, Toast.LENGTH_SHORT);
			    	  }
				}
			}
		};
		
		private BroadcastReceiver bcr = new BroadcastReceiver() {
	    	@Override
	    	public void onReceive(Context context, Intent intent) {
	    		updateUI(intent);
	    	}
	    };
	    
	    private void updateUI(Intent intent) {
	    	int maxVal = intent.getIntExtra("maxval", 1000000);
	    	int progress = intent.getIntExtra("progress", 0);
	    	if (progress > maxVal || progress < 0) {
	    		progress = 0;
	    	}
	    	if (progressBar.getMax() != maxVal) progressBar.setMax(maxVal);
	    	progressBar.setProgress((int) progress);
	    }
	
	@Override
	protected void onActivityResult(int request, int result, Intent intent) {
		switch (request) {
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
	
	private OnClickListener nextSongListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			player.next();
		}
	};
	
	private OnClickListener startServerListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			serverDialog.setTitle(R.string.input_server);
			serverDialog.setView(input_serverName);
			serverDialog.setMessage(mContext.getText(R.string.server_dialog_msg));
			serverDialog.setCancelable(false);
			serverDialog.setButton(DialogInterface.BUTTON_POSITIVE , mContext.getText(R.string.set_server_button), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(lastFMUser == null){
			    		  Log.d(tag, "The Last.FM user was not set.");
			    		  Toast.makeText(mContext, "You have not yet set your Last.FM user account. Please do so before trying to host a server.", Toast.LENGTH_LONG).show();
			    		  return;
			    	  }
			    	  Log.d(tag, "The Last.FM user was: " + lastFMUser);
					if (!mBluetoothAdapter.isEnabled()) {
						Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			    	  	}
		    	  	Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		    	  	discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
					startActivity(discoverableIntent);
					Toast.makeText(getApplicationContext(), input_serverName.getText(), Toast.LENGTH_SHORT).show();
					String bName = input_serverName.getText().toString();
					Log.d(tag, "Setting name to: " + bName);
					mBluetoothAdapter.setName(bName);
		    	  	Log.d(tag, "Bluetooth name set to: " + mBluetoothAdapter.getName());
		    	  	netService = new NetworkService(getApplicationContext(), mBluetoothAdapter);
		    	  	Log.d(tag, "Starting server.");
		    	  	netService.start(bName, lastFMUser);
		    	  	startServerButton.setText(mContext.getText(R.string.stop_server));
		    	  	startServerButton.setOnClickListener(stopServerListener);
		    	  	startClientButton.setVisibility(View.GONE);
		    	  	lastFMButton.setVisibility(View.GONE);
		       } });
			serverDialog.setButton(DialogInterface.BUTTON_NEGATIVE , mContext.getText(R.string.cancel_button), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					serverDialog.cancel();
				} });
			serverDialog.show();
		}
	};
	
	private OnClickListener stopServerListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			stopServerDialog.setTitle(R.string.stop_server);
			stopServerDialog.setMessage(mContext.getText(R.string.input_stop_server_msg));
			stopServerDialog.setCancelable(false);
			stopServerDialog.setButton(DialogInterface.BUTTON_POSITIVE , mContext.getText(R.string.stop_server), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					netService.stop();
		    	  	startServerButton.setText(mContext.getText(R.string.start_server));
		    	  	startServerButton.setOnClickListener(startServerListener);
		    	  	startClientButton.setVisibility(View.VISIBLE);
		    	  	lastFMButton.setVisibility(View.VISIBLE);
		       } });
			stopServerDialog.setButton(DialogInterface.BUTTON_NEGATIVE , mContext.getText(R.string.cancel_button), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					stopServerDialog.cancel();
				} });
			stopServerDialog.show();
		}
	};
	
	private OnClickListener disconnectListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			discDialog.setTitle(R.string.disc_client);
			discDialog.setMessage(mContext.getText(R.string.disc_client_msg));
			discDialog.setCancelable(false);
			discDialog.setButton(DialogInterface.BUTTON_POSITIVE , mContext.getText(R.string.stop_server), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (netService != null) netService.stop();
		    	  	startClientButton.setText(mContext.getText(R.string.start_client));
		    	  	startClientButton.setOnClickListener(startClientListener);
		    	  	startServerButton.setVisibility(View.VISIBLE);
		    	  	lastFMButton.setVisibility(View.VISIBLE);
		       } });
			discDialog.setButton(DialogInterface.BUTTON_NEGATIVE , mContext.getText(R.string.cancel_button), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					discDialog.cancel();
				} });
			discDialog.show();
		}
	};
	private OnClickListener startClientListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			clientDialog.setTitle(R.string.input_client);
			clientDialog.setView(input_clientServerName);
			clientDialog.setMessage(mContext.getText(R.string.client_dialog_msg));
			clientDialog.setCancelable(false);
			clientDialog.setButton(DialogInterface.BUTTON_POSITIVE , mContext.getText(R.string.set_client_button), new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  if(lastFMUser == null){
			    		  Log.d(tag, "The Last.FM user was not set.");
			    		  Toast.makeText(mContext, "You have not yet set your Last.FM user account. Please do so before trying to connect.", Toast.LENGTH_LONG).show();
			    		  return;
			    	  }
			    	  Log.d(tag, "The Last.FM user was: " + lastFMUser);
			    	  Log.d(tag, "The server chosen was: " + input_clientServerName.getText());
			    	  if (!mBluetoothAdapter.isEnabled()) {
							Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
							startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			    	  }
			    	  clientServerName = input_clientServerName.getText().toString();
			    	  Log.d(tag, "clientServerName is: " + clientServerName);
			    	  mBluetoothAdapter.startDiscovery();
			       } });
			clientDialog.setButton(DialogInterface.BUTTON_NEGATIVE , mContext.getText(R.string.cancel_button), new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  clientDialog.cancel();
			       } });
			clientDialog.show();
		}
	};
	
	private OnClickListener lastFMListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			lastFMDialog.setTitle(R.string.input_lastfm);
			lastFMDialog.setView(input_LastFM);
			lastFMDialog.setMessage(mContext.getText(R.string.lastfm_dialog_msg));
			lastFMDialog.setCancelable(false);
			lastFMDialog.setButton(DialogInterface.BUTTON_POSITIVE , mContext.getText(R.string.set_lastfm_button), new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  Log.d(tag, "Last.FM account set to: " + input_LastFM.getText());
			    	  //Set account for user here.
			    	  lastFMUser = input_LastFM.getText().toString();
			    	  Toast.makeText(mContext, "last.FM account set to: " + lastFMUser, Toast.LENGTH_SHORT);
			       } });
			lastFMDialog.setButton(DialogInterface.BUTTON_NEGATIVE , mContext.getText(R.string.cancel_button), new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  clientDialog.cancel();
			       } });
			lastFMDialog.show();
		}
	};

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