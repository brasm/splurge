package sw8.proto1;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class SongView extends TextView {

	public SongView(Context context, Song track) {
		super(context);
		this.setText(track.getName());
	}

}