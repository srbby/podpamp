package com.serb.podpamp.ui.activities;

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
import android.widget.ListView;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.ui.adapters.QueueItemsCursorAdapter;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
	private static final int LOADER_ID = 0;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		findViewById(R.id.btn_feeds).setOnClickListener(this);

		setupQueueList();
	}



	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_feeds:
				showFeedsList();
				break;
		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.mi_settings:
				showSettings();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	//region Private Methods.

	private void showFeedsList()
	{
		Intent intent = new Intent(this, FeedsActivity.class);
		startActivity(intent);
	}



	private void setupQueueList()
	{
		final String[] projection = {
			Contract.FeedItems._ID,
			Contract.FeedItems.TITLE,
			Contract.FeedItems.DESC,
			Contract.FeedItems.PUBLISHED,
			Contract.FeedItems.SIZE,
			Contract.FeedItems.FEED_ID
		};

		final String selection = Contract.FeedItems.IS_READ + " = ?";
		final String[] selectionArgs = { "0" };

		final String sortOrder = Contract.FeedItems.PUBLISHED + " desc";

		final QueueItemsCursorAdapter adapter = new QueueItemsCursorAdapter(
			this,
			R.layout.queue_list_item,
			null,
			new String[] {},
			new int[] {},
			0);

		LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {
				return new CursorLoader(
					MainActivity.this,
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

		ListView listView = (ListView)findViewById(R.id.feed_queue_list);

		listView.setAdapter(adapter);

		getSupportLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				showFeedItemDetails(id);
			}
		});
	}



	private void showFeedItemDetails(long item_id) {
		Intent intent = new Intent(this, FeedItemDetailsActivity.class);
		intent.putExtra("item_id", item_id);
		startActivity(intent);
	}



	private void showSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	//endregion
}
