package com.serb.podpamp.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.utils.ImageUtils;

public class FeedItemsActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_items);

		setupFeedItemsList();
	}



	//region Private Methods.

	private void setupFeedItemsList() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			long feed_id = extras.getLong("feed_id");

			String[] projection = {
				Contract.Feeds.ICON,
				Contract.Feeds.TITLE,
				Contract.Feeds.SUBTITLE
			};

			String selection = Contract.FeedItems._ID + " = ?";
			String[] selectionArgs = { String.valueOf(feed_id) };

			Cursor cursor = getContentResolver().query(Contract.Feeds.CONTENT_URI,
				projection, selection, selectionArgs, null);

			if (cursor != null)
			{
				if (cursor.moveToNext())
				{
					ImageUtils.setImageView((ImageView)findViewById(R.id.img_feed_icon),
						cursor.getBlob(cursor.getColumnIndex(Contract.Feeds.ICON)),
						R.drawable.icon_rss);

					TextView title_view = (TextView) findViewById(R.id.txt_feed_title);
					title_view.setText(cursor.getString(cursor.getColumnIndex(Contract.Feeds.TITLE)));

					TextView subtitle_view = (TextView) findViewById(R.id.txt_feed_subtitle);
					subtitle_view.setText(cursor.getString(cursor.getColumnIndex(Contract.Feeds.SUBTITLE)));
				}
				cursor.close();
			}
		}
	}

	//endregion
}