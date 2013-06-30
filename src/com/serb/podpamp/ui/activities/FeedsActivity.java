package com.serb.podpamp.ui.activities;

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
import com.serb.podpamp.ui.AddFeedDialog;
import com.serb.podpamp.ui.adapters.FeedsCursorAdapter;
import com.serb.podpamp.utils.Utils;

public class FeedsActivity extends FragmentActivity implements View.OnClickListener,
		AddFeedDialog.AddFeedDialogListener {
	private static final int LOADER_ID = 0;

	String[] projection = {
		Contract.Feeds._ID,
		Contract.Feeds.ICON,
		Contract.Feeds.TITLE,
		Contract.Feeds.UNREAD_ITEMS_COUNT
	};

	String sortOrder = Contract.Feeds.TITLE;

	FeedsCursorAdapter adapter;

	private FeedsRequestManager requestManager;



	private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {
			return new CursorLoader(
				FeedsActivity.this,
				Contract.Feeds.CONTENT_URI,
					projection,
				null,
				null,
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

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				showFeedItemsList(id);
			}
		});
	}



	private void showAddFeed() {
		if (Utils.isNetworkAvailable(this, false))
		{
			DialogFragment newFragment = new AddFeedDialog();
			newFragment.show(getSupportFragmentManager(), "add_feed");
		}
	}



	private void addFeed(String feed_url) {
		requestManager.execute(RequestFactory.getAddFeedRequest(feed_url), requestListener);
	}



	private void showFeedItemsList(long id)
	{
		Intent intent = new Intent(this, FeedItemsActivity.class);
		intent.putExtra("feed_id", id);
		startActivity(intent);
	}

	//endregion
}