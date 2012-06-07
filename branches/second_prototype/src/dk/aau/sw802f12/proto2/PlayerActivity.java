package dk.aau.sw802f12.proto2;

import dk.aau.sw802f12.proto2.api.Playback;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PlayerActivity extends Activity {
	
	private Settings mSettings = Settings.getInstance();
	private Playback mService;
	public boolean pBound = false;
	private Handler mHandler = new Handler();

	/* UI Elements */
	private TextView mNowPlayingTitle;
	private TextView mNowPlayingArtist;
	private ProgressBar mProgressBar;
	private Button mStopBtn;
	private Button mToggleBtn;
	private Button mNextBtn;

	private void initView() {
		setContentView(R.layout.bmtest);
		mNowPlayingTitle = (TextView) findViewById(R.id.nowPlayingTitle);
		mNowPlayingArtist = (TextView) findViewById(R.id.nowPlayingArtist);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mStopBtn = (Button) findViewById(R.id.stopBtn);
		mToggleBtn = (Button) findViewById(R.id.toggleBtn);
		mNextBtn = (Button) findViewById(R.id.NextBtn);
	}

	private void updateView() {
		Song np = null;
		String artist = " ";
		String title = "No Song";
		int progress = 0;

		if (pBound)
			np = mService.nowPlaying();

		if (np != null) {
			artist = np.getArtist();
			title = np.getTitle();
			progress = mService.getProgress();
		}
		mNowPlayingArtist.setText(artist);
		mNowPlayingTitle.setText(title);
		mProgressBar.setProgress(progress);
	}

	private Runnable updateViewRunner = new Runnable() {
		public void run() {
			int updateInterval = mSettings.getUserInterfaceUpdateInterval();
			updateView();
			mHandler.postDelayed(this, updateInterval);
		}
	};

	private void setListeners() {
		mStopBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					mService.stop();
				}
			}
		});

		mToggleBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					mService.toggle();
				}
			}
		});

		mNextBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					mService.next();
				}
			}
		});
	}

	/* Service Coommunication */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((PlaybackService.LocalBinder) service).getService();
			pBound = true;
		}

		public void onServiceDisconnected(ComponentName name) {
			pBound = false;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		setListeners();
		Intent intent = new Intent(this, PlaybackService.class);
		startService(intent);
		bindService(intent, mConnection, BIND_AUTO_CREATE);
		new Thread(updateViewRunner).start();
	}

	@Override
	public void onDestroy() {
		unbindService(mConnection);
		super.onDestroy();
	}
}
