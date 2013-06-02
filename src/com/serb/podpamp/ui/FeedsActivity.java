package com.serb.podpamp.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.model.request.FeedsRequestManager;
import com.serb.podpamp.model.request.RequestFactory;

public class FeedsActivity extends FragmentActivity implements View.OnClickListener,
		AddFeedDialog.AddFeedDialogListener {
	private static final int LOADER_ID = 0;

	String[] PROJECTION = {
		Contract.Feeds._ID,
		Contract.Feeds.ICON,
		Contract.Feeds.TITLE,
		Contract.Feeds.NEW_ITEMS_COUNT
	};

	FeedsCursorAdapter adapter;

	private FeedsRequestManager requestManager;



	private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {
			return new CursorLoader(
				FeedsActivity.this,
				Contract.Feeds.CONTENT_URI,
				PROJECTION,
				null,
				null,
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



	RequestManager.RequestListener requestListener = new RequestManager.RequestListener() {
		@Override
		public void onRequestFinished(Request request, Bundle resultData) {
			//listView.onRefreshComplete();
		}

		@Override
		public void onRequestDataError(Request request) {
			showError();
		}

		@Override
		public void onRequestCustomError(Request request, Bundle resultData) {
			showError();
		}

		@Override
		public void onRequestConnectionError(Request request, int statusCode) {
			showError();
		}

		void showError() {
			//listView.onRefreshComplete();
			AlertDialog.Builder builder = new AlertDialog.Builder(FeedsActivity.this);
			builder.setTitle(android.R.string.dialog_alert_title)
				.setMessage(getString(R.string.failed_to_add_feed))
				.create()
				.show();
		}
	};



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feeds);

		findViewById(R.id.btn_add_feed).setOnClickListener(this);

		setupFeedsList();

		requestManager = FeedsRequestManager.from(this);
	}



	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_add_feed:
				showAddFeed();
				break;
		}
	}



	@Override
	public void onDialogPositiveClick(String feed_url) {
		if (feed_url.length() > 0 && feed_url.startsWith("http"))
			addFeed(feed_url);
	}

	//region Private Methods.

	private void setupFeedsList() {
		ListView listView = (ListView)findViewById(R.id.feeds_list);

		adapter = new FeedsCursorAdapter(
			this, // Context.
			R.layout.feed_list_item,
			null,
			new String[] {},
			new int[] {},
			0);

		listView.setAdapter(adapter);

		getSupportLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);

		listView.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
			deleteFeed(id);
			return false;
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				showFeedItemsList(id);
			}
		});
	}



	private void deleteFeed(long id) {
		String selection = Contract.FeedItems.FEED_ID + " = ?";
		String[] selectionArgs = { String.valueOf(id) };

		getContentResolver().delete(
			Contract.FeedItems.CONTENT_URI,
			selection,
			selectionArgs
		);

		selection = Contract.Feeds._ID + " = ?";

		getContentResolver().delete(
			Contract.Feeds.CONTENT_URI,
			selection,
			selectionArgs
		);
	}



	private void showAddFeed() {
		DialogFragment newFragment = new AddFeedDialog();
		newFragment.show(getSupportFragmentManager(), "add_feed");
	}



	private void addFeed(String feed_url) {
		Request updateRequest = new Request(RequestFactory.REQUEST_ADD_FEED);
		updateRequest.put(RequestFactory.FEED_URL, feed_url);
		requestManager.execute(updateRequest, requestListener);
	}



	private void showFeedItemsList(long id)
	{
		Intent intent = new Intent(this, FeedItemsActivity.class);
		intent.putExtra("feed_id", id);
		startActivity(intent);
	}

	//endregion
}