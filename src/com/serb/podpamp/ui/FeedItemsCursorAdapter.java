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

		TextView title_view = (TextView) view.findViewById(R.id.txt_feed_item_title);
		title_view.setText(title);

		TextView desc_view = (TextView) view.findViewById(R.id.txt_feed_item_desc);
		desc_view.setText(desc);

		return(view);
	}
}
