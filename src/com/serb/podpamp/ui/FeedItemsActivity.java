package com.serb.podpamp.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;

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
				Contract.Feeds.TITLE
			};

			String selection = Contract.FeedItems._ID + " = ?";
			String[] selectionArgs = { String.valueOf(feed_id) };

			Cursor cursor = getContentResolver().query(Contract.Feeds.CONTENT_URI,
				projection, selection, selectionArgs, null);

			if (cursor != null)
			{
				if (cursor.getCount() == 1)
				{
					cursor.moveToNext();
					//ImageView feedIcon = (ImageView)findViewById(R.id.img_feed_icon);
					//feedIcon.setImageBitmap(cursor.g);
					TextView feedTitle = (TextView) findViewById(R.id.txt_feed_title);
					feedTitle.setText(cursor.getString(1));
				}
				cursor.close();
			}
		}
	}

	//endregion
}