package com.serb.podpamp.ui.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.utils.Utils;

public class FeedItemDetailsActivity extends Activity {
	private long item_id = -1;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.feed_item_details);

		Bundle extras = getIntent().getExtras();
		if (extras != null)
			item_id = extras.getLong("item_id");

		if (item_id > -1)
		{
			setupItemInfoPanel();
		}
	}

	//region Private Methods.

	private void setupItemInfoPanel() {
		final String[] projection = {
			Contract.FeedItems._ID,
			Contract.FeedItems.FEED_ID,
			Contract.FeedItems.TITLE,
			Contract.FeedItems.DESC,
			Contract.FeedItems.PUBLISHED,
			Contract.FeedItems.LENGTH,
			Contract.FeedItems.IS_READ
		};

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(item_id) };

		Cursor cursor = getContentResolver().query(Contract.FeedItems.CONTENT_URI,
			projection, selection, selectionArgs, null);

		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				String title = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.TITLE));
				String desc = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.DESC));
				long published = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.PUBLISHED));
				long length = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.LENGTH));
				long feed_id = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.FEED_ID));

				TextView title_view = (TextView) findViewById(R.id.txt_feed_item_title);
				title_view.setText(title);

				TextView desc_view = (TextView) findViewById(R.id.txt_feed_item_desc);
				desc_view.setText(desc);

				TextView published_view = (TextView) findViewById(R.id.txt_feed_item_published);
				published_view.setText(Utils.getDateText(published));

				TextView length_view = (TextView) findViewById(R.id.txt_feed_item_length);
				length_view.setText(Utils.getFileSizeText(length));

				Utils.setImageView(this,
					(ImageView) findViewById(R.id.img_feed_icon),
					feed_id,
					R.drawable.icon_rss);
			}
			cursor.close();
		}
	}

	//endregion
}