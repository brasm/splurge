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

public class FirstProtoPlayerActivity extends Activity {
	/** Called when the activity is first created. */
	
	private SeekBar volumeBar;
	private SeekBar progressBar;
	int currentsong = 0;

	String tag = "SW8tag";
	PlayService player;
	boolean pBound = false;
	Intent intent = new Intent();
	
	Playlist songstrings;
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
		
		SeekbarListener sbl = new SeekbarListener();
		
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		volumeBar = (SeekBar) findViewById(R.id.volume);
		volumeBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volumeBar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
		volumeBar.setOnSeekBarChangeListener(sbl);
		
		progressBar = (SeekBar) findViewById(R.id.playProgress);
		progressBar.setOnSeekBarChangeListener(sbl);

		songstrings = new Playlist();
		
		final Button play = (Button) findViewById(R.id.play);
		play.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					player.playTrack(songstrings.get(currentsong));
				}
			}
		});

		final Button next = (Button) findViewById(R.id.playNext);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					switchTrack(true);
				}
			}
		});
		
		final Button previous = (Button) findViewById(R.id.playLast);
		previous.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					switchTrack(false);
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
	
	/**
	 * Enforce the media player to switch to another track.
	 * @param goNext If true, switch to next track in the play queue. If false, switch to previous track.
	 */
	private void switchTrack (boolean goNext) {
		if (goNext) {
			currentsong++;
			if (currentsong == songstrings.size()) {
				currentsong = 0;
			}
		} else {
			currentsong--;
			if (currentsong < 0) {
				currentsong = songstrings.size() - 1;
			}
		}
		player.switchTrack(songstrings.get(currentsong));
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
		registerReceiver(nextSongReceiver, new IntentFilter(PlayService.TRACK_CHANGE_NOTIFICATION));
	}
	
	/**
	 * Receiver unregistering for play service update.
	 */
	public void onPause() {
		super.onPause();
		unregisterReceiver(bcr);
		unregisterReceiver(nextSongReceiver);
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
    
    private BroadcastReceiver nextSongReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
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
					am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
				}
			}
		}
	};
}