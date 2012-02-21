package sw8.proto1;

import sw8.proto1.PlayService.LocalBinder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class FirstProtoPlayerActivity extends Activity {
	/** Called when the activity is first created. */
	
	private SeekBar volumeBar;
	private SeekBar progressBar;
	int currentsong = 0;

	String tag = "SW8tag";
	PlayService player;
	boolean pBound = false;
	Intent intent = new Intent();
	
	final Playlist songstrings = new Playlist();
	AudioManager am;
	
	@Override
    protected void onStart() {
        super.onStart();
        // Bind to PlayService
        intent = new Intent(this, PlayService.class);
        bindService(intent, mConnection , Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (pBound) {
            unbindService(mConnection);
           
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		volumeBar = (SeekBar) findViewById(R.id.volume);
		volumeBar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
		
		progressBar = (SeekBar) findViewById(R.id.playProgress);
		progressBar.setOnSeekBarChangeListener(new SeekbarListener());
		final TextView t = (TextView) findViewById(R.id.trackName);

		fillPlaylist();
		
		final Button play = (Button) findViewById(R.id.play);
		play.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					player.playTrack(Uri.parse(songstrings.get(currentsong).getAbsPath()));
				}
			}
		});

		final Button next = (Button) findViewById(R.id.playNext);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					currentsong++;
					if (currentsong == songstrings.size()) currentsong = 0;
					player.switchTrack(Uri.parse(songstrings.get(currentsong).getAbsPath()));
				}
			}
		});
		
		final Button previous = (Button) findViewById(R.id.playLast);
		previous.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					if (currentsong == 0) currentsong = songstrings.size();
					
					currentsong--;
					player.switchTrack(Uri.parse(songstrings.get(currentsong).getAbsPath()));
				}
			}
		});
		
		final Button playlist = (Button) findViewById(R.id.playlist_button);
		playlist.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				intent = new Intent(FirstProtoPlayerActivity.this, ChoosePlaylistActivity.class);
	            startActivity(intent);
			}

		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	/**
	 * Receiver registering for play service update.
	 */
	public void onResume() {
		super.onResume();
		startService(intent);
		registerReceiver(bcr, new IntentFilter(PlayService.UPDATE_INFO));
	}
	
	/**
	 * Receiver unregistering for play service update.
	 */
	public void onPause() {
		super.onPause();
		unregisterReceiver(bcr);
		stopService(intent);
	}
	
	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
  
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            player = binder.getService();
            pBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            pBound = false;
            player.die();
        }
    };
    
    /**
     * BroadcastReceiver for updating user interface.
     */
    private BroadcastReceiver bcr = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		updateUI(intent);
    	}
    };
    
    /**
     * Updates the progressbar in the UI, with the value sent to the bcr.
     * @param intent
     */
    private void updateUI(Intent intent) {
    	progressBar = (SeekBar) findViewById(R.id.playProgress);
    	
    	float progress = intent.getFloatExtra("progress", 0);
    	if (progress > 100 || progress < 0) {
    		progress = 0;
    	}
    	
    	progressBar.setProgress((int) progress);
    }
    
    private class SeekbarListener implements SeekBar.OnSeekBarChangeListener {
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (seekBar == progressBar) {
				if(fromUser){
					Log.d(tag, "progress was seeked to:" +progress);
					player.seekTrack(progress);
				}
			} else if (seekBar == volumeBar) {
				if (fromUser) {
					Log.d(tag, "Changing volume to " + progress);
					float maxvol = (float) am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
					float vol = ((float) progress/maxvol) * 100;
					am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) vol, 0);
				}
			}
		}
	};
	
	private void fillPlaylist() {
		boolean anders = false;
		
		String basePath = "/sdcard/Music/";
		
		if (anders) {
			songstrings.add(new Song("The Spell", basePath + "Alphabeat/The Best of Blue Magic_ Soulful Spell/01 The Spell.wma"));
			songstrings.add(new Song("You Make me feel", basePath + "Cobra Starship/You Make Me Feel... (feat. Sabi) - Singl/01 You Make Me Feel... (feat. Sabi).wma"));
			songstrings.add(new Song("Hvem springer du for", basePath + "De Eneste To/De eneste to/02 Hvem springer du for.wma"));
		} else {
			songstrings.add(new Song("Cemeteries of London", basePath + "Coldplay/Coldplay - Cemeteries Of London - Viva La Vida Or Death and all his friends.mp3"));
			songstrings.add(new Song("Danmark Man Dark", basePath + "Nephew/Nephew - Danmark Man Dark - DanmarkDenmark.WMA"));
			songstrings.add(new Song("Jerk It Out", basePath + "The Caesars/The Caesars - Jerk It Out - Love For The Streets.mp3"));
			songstrings.add(new Song("Berlin", basePath + "Kent/Kent - Berlin - Tillbaka Till Samtiden.mp3"));
		}		
	}
}