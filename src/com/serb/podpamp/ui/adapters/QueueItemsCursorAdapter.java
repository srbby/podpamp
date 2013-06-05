package com.serb.podpamp.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.utils.Utils;

public class QueueItemsCursorAdapter extends SimpleCursorAdapter {
	private Context context;



	public QueueItemsCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
		super(context, layout, cursor, from, to, flags);
		this.context = context;
	}



	public View getView(int pos, View inView, ViewGroup parent) {
		View view = inView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.queue_list_item, null);
		}

		Cursor cursor = getCursor();
		cursor.moveToPosition(pos);

		String title = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.TITLE));
		String desc = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.DESC));
		long published = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.PUBLISHED));
		long length = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.LENGTH));
		long feed_id = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.FEED_ID));

		TextView title_view = (TextView) view.findViewById(R.id.txt_feed_item_title);
		title_view.setText(title);

		TextView desc_view = (TextView) view.findViewById(R.id.txt_feed_item_desc);
		desc_view.setText(desc);

		TextView published_view = (TextView) view.findViewById(R.id.txt_feed_item_published);
		published_view.setText(Utils.getDateText(published));

		TextView length_view = (TextView) view.findViewById(R.id.txt_feed_item_length);
		length_view.setText(Utils.getFileSizeText(length));

		Utils.setImageView(view.getContext(),
			(ImageView) view.findViewById(R.id.img_feed_icon),
			feed_id,
			R.drawable.icon_rss);

		return(view);
	}
}
