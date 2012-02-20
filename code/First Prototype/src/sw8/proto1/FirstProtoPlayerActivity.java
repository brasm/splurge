package sw8.proto1;

import java.util.ArrayList;

import sw8.proto1.PlayService.LocalBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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
	
	@Override
    protected void onStart() {
        super.onStart();
        // Bind to PlayService
        Intent intent = new Intent(this, PlayService.class);
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
		progressBar = (SeekBar) findViewById(R.id.playProgress);
		final TextView t = (TextView) findViewById(R.id.trackName);

		final String songstring1 = "/sdcard/Music/Alphabeat/The Best of Blue Magic_ Soulful Spell/01 The Spell.wma";
		final String songstring2 = "/sdcard/Music/Cobra Starship/You Make Me Feel... (feat. Sabi) - Singl/01 You Make Me Feel... (feat. Sabi).wma";
		final String songstring3 = "/sdcard/Music/De Eneste To/De eneste to/02 Hvem springer du for.wma";
		final ArrayList<String> songstrings = new ArrayList<String>();
		songstrings.add(songstring1);
		songstrings.add(songstring2);
		songstrings.add(songstring3);

		

		final Button play = (Button) findViewById(R.id.play);
		play.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (pBound)				
					player.playTrack(Uri.parse(songstrings.get(0)));

			}
		});

		final Button next = (Button) findViewById(R.id.playNext);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		
		final Button previous = (Button) findViewById(R.id.playNext);
		previous.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
			}

		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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

}