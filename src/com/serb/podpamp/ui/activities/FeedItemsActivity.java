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
import android.widget.*;
import com.serb.podpamp.R;
import com.serb.podpamp.model.managers.FeedsManager;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.ui.FeedItemFilter;
import com.serb.podpamp.ui.adapters.FeedItemsCursorAdapter;
import com.serb.podpamp.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FeedItemsActivity extends FragmentActivity implements View.OnClickListener {
	private static final int LOADER_ID = 0;
	private static final int REQUEST_CODE = 1;

	private long feedId = -1;

	Button btnFilterStarred;
	Button btnClearFilterStarred;

	private final FeedItemFilter filter = new FeedItemFilter();

	private FeedItemsCursorAdapter adapter;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_items);

		btnFilterStarred = (Button) findViewById(R.id.btn_filter_starred);
		btnClearFilterStarred = (Button) findViewById(R.id.btn_clear_filter_starred);

		btnFilterStarred.setOnClickListener(this);
		btnClearFilterStarred.setOnClickListener(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			feedId = extras.getLong("feed_id");
			filter.setFeedId(feedId);
		}

		if (feedId > -1)
		{
			setupFeedInfoPanel();
			setupFeedItemsList();
		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.feed_items_menu, menu);
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
				if (Utils.isNetworkAvailable(this, false))
				{
					FeedsManager.refreshIcon(this, feedId);
					setupFeedInfoPanel();
				}
				return true;
			case R.id.mi_change_icon:
				changeIcon();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}



	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_filter_starred:
				switchFilterStarred(true);
				break;
			case R.id.btn_clear_filter_starred:
				switchFilterStarred(false);
				break;
		}
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
			try {
				InputStream is = getContentResolver().openInputStream(data.getData());
				FeedsManager.setIcon(this, feedId, Utils.compressImage(is));
				is.close();
				setupFeedInfoPanel();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		super.onActivityResult(requestCode, resultCode, data);
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
		adapter = new FeedItemsCursorAdapter(
			this, // Context.
			R.layout.feed_items_list_item,
			null,
			new String[] {},
			new int[] {},
			0);

		ListView listView = (ListView)findViewById(R.id.feed_items_list);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				showFeedItemDetails(id);
			}
		});

		updateList();
	}



	private void updateList()
	{
		final String[] projection = {
			Contract.FeedItems._ID,
			Contract.FeedItems.TITLE,
			Contract.FeedItems.DESC,
			Contract.FeedItems.PUBLISHED,
			Contract.FeedItems.SIZE,
			Contract.FeedItems.IS_READ,
			Contract.FeedItems.DOWNLOADED,
			Contract.FeedItems.IS_STARRED
		};

		final String selection = filter.setupSelection(Contract.FeedItems.FEED_ID + " = ?");
		final String[] selectionArgs = { String.valueOf(feedId) };
		final String sortOrder = Contract.FeedItems.IS_READ + ", " + Contract.FeedItems.PUBLISHED + " desc limit 50";

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

		getSupportLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks);
	}



	private void showFeedItemDetails(long itemId) {
		Intent intent = new Intent(this, FeedItemDetailsActivity.class);
		intent.putExtra("item_id", itemId);
		intent.putExtra("filter", filter);
		startActivity(intent);
	}



	private void switchFilterStarred(boolean isStarred) {
		filter.setShowStarred(isStarred);

		btnFilterStarred.setVisibility(isStarred ? View.INVISIBLE : View.VISIBLE);
		btnClearFilterStarred.setVisibility(isStarred ? View.VISIBLE : View.INVISIBLE);

		updateList();
	}



	private void changeIcon() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, REQUEST_CODE);
	}

	//endregion
}