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

		long feedId = cursor.getLong(cursor.getColumnIndex(Contract.Feeds._ID));
		String feed_title = cursor.getString(cursor.getColumnIndex(Contract.Feeds.TITLE));
		int feedsCount = cursor.getInt(cursor.getColumnIndex(Contract.Feeds.UNREAD_ITEMS_COUNT));
		int starredCount = cursor.getInt(cursor.getColumnIndex(Contract.Feeds.STARRED_ITEMS_COUNT));

		TextView title_view = (TextView) view.findViewById(R.id.txt_feed_title);
		title_view.setText(feed_title);

		TextView count_view = (TextView) view.findViewById(R.id.txt_new_feeds_count);
		if (feedsCount > 0)
		{
			count_view.setText(String.valueOf(feedsCount) + " " +
				view.getResources().getText(feedsCount > 1 ? R.string.episodes_count_text : R.string.one_episodes_text));
			count_view.setTextColor(context.getResources().getColor(R.color.unread_item_color));
		}
		else
		{
			count_view.setText(R.string.no_new_episodes);
			count_view.setTextColor(context.getResources().getColor(R.color.read_item_color));
		}

		Utils.setImageView(context,
			(ImageView) view.findViewById(R.id.img_feed_icon),
			feedId,
			R.drawable.icon_rss);

		TextView txtStarredItemsCount = (TextView) view.findViewById(R.id.txt_starred_items_count);
		if (starredCount > 0)
		{
			txtStarredItemsCount.setText(String.valueOf(starredCount));
			txtStarredItemsCount.setVisibility(View.VISIBLE);
		}
		else
		{
			txtStarredItemsCount.setVisibility(View.INVISIBLE);
		}

		return(view);
	}
}
