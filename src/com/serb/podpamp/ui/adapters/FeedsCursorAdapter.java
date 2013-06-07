package com.serb.podpamp.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.utils.Utils;

public class FeedsCursorAdapter extends SimpleCursorAdapter {
	private Context context;



	public FeedsCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
		super(context, layout, cursor, from, to, flags);
		this.context = context;
	}



	public View getView(int pos, View inView, ViewGroup parent) {
		View view = inView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.feed_list_item, null);
		}

		Cursor cursor = getCursor();
		cursor.moveToPosition(pos);

		String feed_title = cursor.getString(cursor.getColumnIndex(Contract.Feeds.TITLE));
		int feeds_count = cursor.getInt(cursor.getColumnIndex(Contract.Feeds.NEW_ITEMS_COUNT));
		byte[] feed_icon = cursor.getBlob(cursor.getColumnIndex(Contract.Feeds.ICON));

		TextView title_view = (TextView) view.findViewById(R.id.txt_feed_title);
		title_view.setText(feed_title);

		TextView count_view = (TextView) view.findViewById(R.id.txt_new_feeds_count);
		count_view.setText(String.valueOf(feeds_count));

		Utils.setImageView((ImageView) view.findViewById(R.id.img_feed_icon),
			feed_icon,
			R.drawable.icon_rss);

		return(view);
	}
}