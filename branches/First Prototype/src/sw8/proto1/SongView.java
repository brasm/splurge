package sw8.proto1;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SongView extends LinearLayout {

	TextView text;
	CheckBox check;
	
	public TextView getText() {
		return text;
	}


	public void setText(String text) {
		this.text.setText(text);
	}


	public CheckBox getCheck() {
		return check;
	}


	public void setCheck(CheckBox check) {
		this.check = check;
	}

	public SongView(Context context, Song track) {
		super(context);
		text = new TextView(context);
		check = new CheckBox(context);
		this.text.setText(track.getName());
		check.setChecked(false);
		
		addView(check, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addView(text, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
	

}