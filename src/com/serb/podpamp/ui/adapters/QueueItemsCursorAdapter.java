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
		long published = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.PUBLISHED));
		long size = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.SIZE));
		long downloaded = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.DOWNLOADED));
		long feed_id = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.FEED_ID));
		boolean isStarred = cursor.getInt(cursor.getColumnIndex(Contract.FeedItems.IS_STARRED)) > 0;
		//todo long duration = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.DURATION));

		TextView titleView = (TextView) view.findViewById(R.id.txt_feed_item_title);
		titleView.setText(title);

		TextView publishedView = (TextView) view.findViewById(R.id.txt_feed_item_published);
		publishedView.setText(Utils.getDateText(published));

		TextView sizeView = (TextView) view.findViewById(R.id.txt_feed_item_size);
		TextView downloadedView = (TextView) view.findViewById(R.id.txt_downloaded);
		TextView waitingDownloadView = (TextView) view.findViewById(R.id.txt_waiting_download);
		ImageView starView = (ImageView) view.findViewById(R.id.img_star);

		sizeView.setText(Utils.getFileSizeText(size));

		if (downloaded == -1)
		{
			sizeView.setVisibility(View.INVISIBLE);
			downloadedView.setVisibility(View.INVISIBLE);
			waitingDownloadView.setVisibility(View.VISIBLE);
		}
		else if (downloaded > 0 && downloaded < size)
		{
			waitingDownloadView.setVisibility(View.INVISIBLE);
			downloadedView.setVisibility(View.VISIBLE);
			sizeView.setVisibility(View.VISIBLE);

			downloadedView.setText(Utils.getFileSizeText(downloaded) + "/");
		}
		else
		{
			waitingDownloadView.setVisibility(View.INVISIBLE);
			downloadedView.setVisibility(View.INVISIBLE);
			sizeView.setVisibility(View.VISIBLE);
		}

		sizeView.setTextColor(context.getResources().getColor(downloaded == size ? R.color.unread_item_color : R.color.read_item_color));

		starView.setVisibility(isStarred ? View.VISIBLE : View.INVISIBLE);

		Utils.setImageView(view.getContext(),
			(ImageView) view.findViewById(R.id.img_feed_icon),
			feed_id,
			R.drawable.icon_rss);

		return(view);
	}
}
