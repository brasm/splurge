package sw8.proto1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SongListAdapter extends BaseAdapter {

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
		SongView sv;
		if (arg1 == null)
			sv = new SongView(mContext, songs.get(arg0));
		else {
			sv = (SongView) arg1;
			sv.setText(songs.get(arg0).getName());
		}
		return sv;
	}

}
