package com.serb.podpamp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.serb.podpamp.R;
import com.serb.podpamp.model.managers.FeedsManager;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.ui.adapters.FeedItemsCursorAdapter;
import com.serb.podpamp.utils.Utils;

public class FeedItemsActivity extends FragmentActivity {
	private static final int LOADER_ID = 0;

	private long feedId = -1;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_items);

		Bundle extras = getIntent().getExtras();
		if (extras != null)
			feedId = extras.getLong("feed_id");

		if (feedId > -1)
		{
			setupFeedInfoPanel();
			setupFeedItemsList();
		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.feeds_menu, menu);
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.mi_mark_all_listened:
				FeedsManager.markAllFeedItemAsReadOrUnread(this, feedId, true);
				return true;
			case R.id.mi_delete_feed:
				Utils.showConfirmationDialog(this,
					getResources().getString(R.string.delete_feed_dialog_title),
					getResources().getString(R.string.delete_feed_dialog_message),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						FeedsManager.deleteFeed(FeedItemsActivity.this, feedId);
						finish();
						}
					});
				return true;
			case R.id.mi_refresh_icon:
				if (Utils.isNetworkAvailable(this, true))
				{
					FeedsManager.refreshIcon(this, feedId);
					setupFeedInfoPanel();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
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
		String[] selectionArgs = { String.valueOf(feedId) };

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
			Contract.FeedItems.SIZE,
			Contract.FeedItems.IS_READ,
			Contract.FeedItems.DOWNLOADED
		};

		final String selection = Contract.FeedItems.FEED_ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedId) };
		final String sortOrder = Contract.FeedItems.PUBLISHED + " desc Limit 50";

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
					sortOrder
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

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				showFeedItemDetails(id);
			}
		});
	}



	private void showFeedItemDetails(long itemId) {
		Intent intent = new Intent(this, FeedItemDetailsActivity.class);
		intent.putExtra("item_id", itemId);
		startActivity(intent);
	}

	//endregion
}