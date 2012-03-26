package dk.aau.sw802f12.king;

import dk.aau.sw802f12.king.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
//import android.widget.TextView;

public class PlayerActivity extends Activity {
	/* UI Elements */
	private ImageButton kingBtn;
	// private TextView nowPlaying;

	/* Service Connection */
	private PlaybackService mService;
	public boolean pBound = false;

	public ServiceConnection mConnection = new ServiceConnection() {
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

	private void initView() {
		setContentView(R.layout.main);
		kingBtn = (ImageButton) findViewById(R.id.kingBtn);
		// nowPlaying = (TextView) findViewById(R.id.nowPlaying);
	}

	private void setListeners() {
		kingBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (pBound) {
					mService.play();
					// nowPlaying.setText(song);
				}
			}
		});
	}
}
