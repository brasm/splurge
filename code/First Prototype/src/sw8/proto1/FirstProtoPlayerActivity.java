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
					if (!player.playing) {
						player.playTrack(Uri.parse(songstrings.get(currentsong).getAbsPath()));
					} else {
						player.pauseTrack();
					}
				}
			}
		});

		final Button next = (Button) findViewById(R.id.playNext);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (currentsong == (songstrings.size() - 1)) currentsong = 0;
				else currentsong++;
				player.playTrack(Uri.parse(songstrings.get(currentsong).getAbsPath()));
			}
		});
		
		final Button previous = (Button) findViewById(R.id.playLast);
		previous.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				currentsong--;
				if (currentsong < 0) currentsong = songstrings.size() - 1;
				player.playTrack(Uri.parse(songstrings.get(currentsong).getAbsPath()));
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
		String basePath = "/sdcard/Music/";
		Song s1 = new Song("The Spell", basePath + "Alphabeat/The Best of Blue Magic_ Soulful Spell/01 The Spell.wma");
		Song s2 = new Song("You Make me feel", basePath + "Cobra Starship/You Make Me Feel... (feat. Sabi) - Singl/01 You Make Me Feel... (feat. Sabi).wma");
		Song s3 = new Song("Hvem springer du for", basePath + "De Eneste To/De eneste to/02 Hvem springer du for.wma");
		songstrings.add(s1);
		songstrings.add(s2);
		songstrings.add(s3);
	}
}