package com.serb.podpamp.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.utils.Utils;

public class FeedItemsCursorAdapter extends SimpleCursorAdapter {
	private Context context;



	public FeedItemsCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
		super(context, layout, cursor, from, to, flags);
		this.context = context;
	}



	public View getView(int pos, View inView, ViewGroup parent) {
		View view = inView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.feed_items_list_item, null);
		}

		Cursor cursor = getCursor();
		cursor.moveToPosition(pos);

		String title = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.TITLE));
		String desc = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.DESC));
		long published = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.PUBLISHED));
		long size = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.SIZE));
		boolean isRead = cursor.getInt(cursor.getColumnIndex(Contract.FeedItems.IS_READ)) > 0;

		TextView titleView = (TextView) view.findViewById(R.id.txt_feed_item_title);
		titleView.setText(title);
		titleView.setTextColor(context.getResources().getColor(isRead ? R.color.read_item_color : R.color.unread_item_color));

		TextView descView = (TextView) view.findViewById(R.id.txt_feed_item_desc);
		descView.setText(desc);

		TextView publishedView = (TextView) view.findViewById(R.id.txt_feed_item_published);
		publishedView.setText(Utils.getDateText(published));
		publishedView.setTextColor(context.getResources().getColor(isRead ? R.color.read_item_color : R.color.unread_item_color));

		TextView sizeView = (TextView) view.findViewById(R.id.txt_feed_item_size);
		sizeView.setText(Utils.getFileSizeText(size));
		sizeView.setTextColor(context.getResources().getColor(isRead ? R.color.read_item_color : R.color.unread_item_color));

		return(view);
	}
}
