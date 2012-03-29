package dk.aau.sw802f12.king;

import java.util.Timer;

import dk.aau.sw802f12.king.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.storage.OnObbStateChangeListener;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
//import android.widget.TextView;
import android.widget.ToggleButton;

public class PlayerActivity extends Activity {
	
	/* UI Elements */	
	private TextView mNowPlayingTitle;
	private TextView mNowPlayingArtist;
	private ProgressBar mProgressBar;
	private TextView mProgressTxt;
	private TextView mProgressLength;
	private Button mStopBtn;
	private Button mToggleBtn;
	private Button mNextBtn;

	private void initView() {
		setContentView(R.layout.main);
		mNowPlayingTitle = (TextView) findViewById(R.id.nowPlayingTitle);
		mNowPlayingArtist = (TextView) findViewById(R.id.nowPlayingArtist);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mProgressTxt = (TextView) findViewById(R.id.progress);
		mProgressLength = (TextView) findViewById(R.id.tracktime);
		mStopBtn = (Button) findViewById(R.id.stopBtn);
		mToggleBtn = (Button) findViewById(R.id.toggleBtn);
		mNextBtn = (Button) findViewById(R.id.Next);
	}
	
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
				if(pBound) {
					mService.next();
				}
			}
		});
	}
	
	/* Service Connection */
	private PlaybackService mService;
	public boolean pBound = false;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((PlaybackService.LocalBinder) service).getService();
			pBound = true;
		}

		public void onServiceDisconnected(ComponentName name) {
			pBound = false;
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		setListeners();
	}

	public void onStart() {
		super.onStart();
		Intent intent = new Intent(this, PlaybackService.class);
		bindService(intent, mConnection, BIND_AUTO_CREATE);
	}

	public void onDestroy() {
		unbindService(mConnection);
		super.onDestroy();
	}
}