package com.serb.podpamp.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.ui.adapters.FeedItemsCursorAdapter;
import com.serb.podpamp.utils.Utils;

public class FeedItemsActivity extends FragmentActivity {
	private static final int LOADER_ID = 0;

	private long feed_id = -1;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_items);

		Bundle extras = getIntent().getExtras();
		if (extras != null)
			feed_id = extras.getLong("feed_id");

		if (feed_id > -1)
		{
			setupFeedInfoPanel();
			setupFeedItemsList();
		}
	}



	//region Private Methods.

	private void setupFeedInfoPanel() {
		String[] projection = {
			Contract.Feeds.ICON,
			Contract.Feeds.TITLE,
			Contract.Feeds.SUBTITLE
		};

		String selection = Contract.Feeds._ID + " = ?";
		String[] selectionArgs = { String.valueOf(feed_id) };

		Cursor cursor = getContentResolver().query(Contract.Feeds.CONTENT_URI,
			projection, selection, selectionArgs, null);

		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				Utils.setImageView((ImageView) findViewById(R.id.img_feed_icon),
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



	private void setupFeedItemsList()
	{
		final String[] projection = {
			Contract.FeedItems._ID,
			Contract.FeedItems.TITLE,
			Contract.FeedItems.DESC,
			Contract.FeedItems.PUBLISHED,
			Contract.FeedItems.LENGTH
		};

		final String selection = Contract.FeedItems.FEED_ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feed_id) };

		final FeedItemsCursorAdapter adapter = new FeedItemsCursorAdapter(
			this, // Context.
			R.layout.feed_items_list_item,
			null,
			new String[] {},
			new int[] {},
			0);

		LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {
				return new CursorLoader(
					FeedItemsActivity.this,
					Contract.FeedItems.CONTENT_URI,
					projection,
					selection,
					selectionArgs,
					null
				);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
				adapter.swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(Loader<Cursor> arg0) {
				adapter.swapCursor(null);
			}
		};

		ListView listView = (ListView)findViewById(R.id.feed_items_list);

		listView.setAdapter(adapter);

		getSupportLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);
	}

	//endregion
}