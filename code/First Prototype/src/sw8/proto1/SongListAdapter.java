package sw8.proto1;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class SongListAdapter extends BaseAdapter {

	private final static String tag = "sw8tag.SongListAdapter";
	Context mContext;
	Playlist songs;

	public SongListAdapter(Context context, Playlist _songs) {
		mContext = context;
		songs = _songs;
	}

	public int getCount() {
		if (songs != null)
			return songs.size();
		else
			return 0;
	}

	public Object getItem(int arg0) {
		return songs.get(arg0);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		LinearLayout l = new LinearLayout(mContext);
		SongView sv;
		Log.d(tag, "arg0 is: " + arg0);
		Log.d(tag, "song name is: " + songs.get(arg0).getName());
		Log.d(tag, "song path is: " + songs.get(arg0).getAbsPath());

		sv = new SongView(mContext, songs.get(arg0));
		l.addView(sv);

		return l;
	}

}
