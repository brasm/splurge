package dk.aau.sw802f12.proto2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import dk.aau.sw802f12.proto2.api.Playback;

public class playActivity extends Activity {
	private SeekBar volumeBar;
	private SeekBar progressBar;

	String tag = "SW8tag";
	Playback player;
	boolean pBound = false;
	Intent intent = new Intent();

	AudioManager am;

	TextView current_text;
	TextView song1;
	TextView song2;
	TextView song3;
	TextView song4;

	@Override
	protected void onStart() {
		super.onStart();
		intent = new Intent(this, PlaybackService.class);
		startService(intent);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (pBound) {
			unbindService(mConnection);
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		// player = new PlayService();

		SeekbarListener sbl = new SeekbarListener();

		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		volumeBar = (SeekBar) findViewById(R.id.volume);
		volumeBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volumeBar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
		volumeBar.setOnSeekBarChangeListener(sbl);

		progressBar = (SeekBar) findViewById(R.id.playProgress);
		progressBar.setOnSeekBarChangeListener(sbl);

		// TextViews for trackinfo
		current_text = (TextView) findViewById(R.id.trackName);
		song1 = (TextView) findViewById(R.id.song1_text);
		song2 = (TextView) findViewById(R.id.song2_text);
		song3 = (TextView) findViewById(R.id.song3_text);
		song4 = (TextView) findViewById(R.id.song4_text);

		final Button play = (Button) findViewById(R.id.play);
		play.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					play();
				}
			}
		});

		final Button next = (Button) findViewById(R.id.playNext);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					next();
				}
			}
		});

		final Button previous = (Button) findViewById(R.id.playLast);
		previous.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					previous();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		if(pBound)
			unbindService(mConnection);
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		// TODO: FIX
		/*
		 * registerReceiver(bcr, new IntentFilter(PlayService.UPDATE_INFO));
		 * registerReceiver(nextSongReceiver, new
		 * IntentFilter(PlayService.TRACK_CHANGE_NOTIFICATION));
		 * registerReceiver(playbackCompleteReceiver, new
		 * IntentFilter(PlayService.PLAYBACK_COMPLETE_NOTIFICATION));
		 */
	}

	@Override
	public void onPause() {
		super.onPause();
		// TODO: FIX
		/*
		 * unregisterReceiver(bcr); unregisterReceiver(nextSongReceiver);
		 * unregisterReceiver(playbackCompleteReceiver); stopService(intent);
		 */
	}

	private BroadcastReceiver nextSongReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO: Intent should contain current song + 4 upcoming songs!
			Log.d(tag, "New Track intent received");
			String testname = intent.getStringExtra("currentsong");
			current_text.setText("CURRENT");
			song1.setText("1. ");
			song2.setText("2. ");
			song3.setText("3. ");
			song4.setText("4. ");
		}
	};

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			player = ((PlaybackService.LocalBinder) service).getService();
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance

			// TODO: fix!
			/*
			 * LocalBinder binder = (LocalBinder) service; player =
			 * binder.getService();
			 */
			pBound = true;
		}

		public void onServiceDisconnected(ComponentName arg0) {
			pBound = false;
			// TODO: fix!
			// player.die();
		}
	};

	private BroadcastReceiver bcr = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);
		}
	};

	private void updateUI(Intent intent) {
		progressBar = (SeekBar) findViewById(R.id.playProgress);

		int maxVal = intent.getIntExtra("maxval", 1000000);
		int progress = intent.getIntExtra("progress", 0);
		if (progress > maxVal || progress < 0) {
			progress = 0;
		}

		if (progressBar.getMax() != maxVal)
			progressBar.setMax(maxVal);
		progressBar.setProgress(progress);
	}

	private void next() {
		player.next();
	}

	private void previous() {
		// player.previous();
	}

	private void play() {
		player.toggle();

	}

	private void stop() {
		player.stop();
	}

	private void searchTo(int progress) {
		// player.searchToTime(progress);
	}

	private void volume(int progress) {
		am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
	}

	// TODO: is this actually required?
	private void addTrack(Song song) {

	}

	// TODO: is this to be used in this activity?
	private void clearPlaylist() {

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
				if (fromUser) {
					Log.d(tag, "progress was seeked to:" + progress);
					searchTo(progress);
				}
			} else if (seekBar == volumeBar) {
				if (fromUser) {
					Log.d(tag, "Changing volume to " + progress);
					volume(progress);
				}
			}
		}
	};
}