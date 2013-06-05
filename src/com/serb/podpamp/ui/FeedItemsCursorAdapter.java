package com.serb.podpamp.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		long length = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.LENGTH));

		TextView title_view = (TextView) view.findViewById(R.id.txt_feed_item_title);
		title_view.setText(title);

		TextView desc_view = (TextView) view.findViewById(R.id.txt_feed_item_desc);
		desc_view.setText(desc);

		TextView published_view = (TextView) view.findViewById(R.id.txt_feed_item_published);
		published_view.setText(getPublishedText(published));

		TextView length_view = (TextView) view.findViewById(R.id.txt_feed_item_length);
		length_view.setText(getLengthText(length));

		return(view);
	}

	//region Private Methods.

	private String getLengthText(double length) {
		if (length > 0)
		{
			DecimalFormat form = new DecimalFormat("#.##");
			if (length >= 1048576)
				return form.format(length / 1048576) + " MB";
			return form.format(length / 1024) + " KB";
		}
		return "";
	}



	private String getPublishedText(long published) {
		if (published > 0)
		{
			Date pub_date = new Date(published);
			return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(pub_date);
		}
		return "";
	}

	//endregion
}
