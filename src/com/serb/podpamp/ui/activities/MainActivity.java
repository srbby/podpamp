package com.serb.podpamp.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.serb.podpamp.R;
import com.serb.podpamp.model.managers.FeedsManager;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.model.request.FeedsRequestManager;
import com.serb.podpamp.model.request.RequestFactory;
import com.serb.podpamp.ui.FeedItemFilter;
import com.serb.podpamp.ui.adapters.QueueItemsCursorAdapter;
import com.serb.podpamp.utils.Utils;

public class MainActivity extends DownloadActivity implements View.OnClickListener {
	private static final int LOADER_ID = 0;

	private FeedsRequestManager requestManager;

	private ProgressBar progressBar;

	private Button btnRefresh;
	Button btnFilterStarred;
	Button btnClearFilterStarred;

	private final FeedItemFilter filter = new FeedItemFilter();

	private QueueItemsCursorAdapter adapter;

	RequestManager.RequestListener refreshRequestListener = new RequestManager.RequestListener() {
		@Override
		public void onRequestFinished(Request request, Bundle resultData) {
			hideProgress();
			Toast.makeText(MainActivity.this, getString(R.string.refresh_complete), Toast.LENGTH_LONG).show();
			if (Utils.isInstantDownloadSet(MainActivity.this))
				downloadNewEpisodes();
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
			hideProgress();
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle(android.R.string.dialog_alert_title)
				.setMessage(getString(R.string.refresh_failed))
				.create()
				.show();
		}
	};



	RequestManager.RequestListener cleanUpRequestListener = new RequestManager.RequestListener() {
		@Override
		public void onRequestFinished(Request request, Bundle resultData) {
			String text = getString(R.string.clean_up_complete);
			if (resultData != null)
				text += " " + String.valueOf(resultData.getInt("count")) + " " + getString(R.string.episodes_deleted);
			Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
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
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle(android.R.string.dialog_alert_title)
				.setMessage(getString(R.string.cleanup_failed))
				.create()
				.show();
		}
	};



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		btnRefresh = (Button) findViewById(R.id.btn_refresh);
		btnFilterStarred = (Button) findViewById(R.id.btn_filter_starred);
		btnClearFilterStarred = (Button) findViewById(R.id.btn_clear_filter_starred);

		btnRefresh.setOnClickListener(this);
		findViewById(R.id.btn_feeds).setOnClickListener(this);
		btnFilterStarred.setOnClickListener(this);
		btnClearFilterStarred.setOnClickListener(this);

		setupQueueList();

		requestManager = FeedsRequestManager.from(this);
	}



	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_feeds:
				showFeedsList();
				break;
			case R.id.btn_refresh:
				refreshFeeds();
				break;
			case R.id.btn_filter_starred:
				switchFilterStarred(true);
				break;
			case R.id.btn_clear_filter_starred:
				switchFilterStarred(false);
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
			case R.id.mi_cleanup:
				cleanUp();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}



	@Override
	protected void onDownloadServiceConnected() {
		if (downloadService.isDownloading())
			showProgress();
	}



	@Override
	protected void onDownloadServiceDisconnected() {
		hideProgress();
	}



	@Override
	protected void onAllDownloadsCompleted() {
		hideProgress();
		Toast.makeText(this, getString(R.string.download_complete), Toast.LENGTH_LONG).show();
	}



	@Override
	protected void onDownloadErrorOccurred(long feedItemId) {
		if (!downloadService.isDownloading())
			hideProgress();
	}

	//region Private Methods.

	private void showFeedsList() {
		Intent intent = new Intent(this, FeedsActivity.class);
		startActivity(intent);
	}



	private void refreshFeeds() {
		if (Utils.isNetworkAvailable(this, true))
		{
			showProgress();
			requestManager.execute(RequestFactory.getRefreshFeedsRequest(), refreshRequestListener);
		}
	}



	private void downloadNewEpisodes() {
		if (downloadService.downloadNewEpisodes(FeedsManager.getNewEpisodes(this)))
		{
			showProgress();
		}
	}



	private void cleanUp() {
		requestManager.execute(new Request(RequestFactory.REQUEST_CLEANUP), cleanUpRequestListener);
	}



	private void setupQueueList() {
		adapter = new QueueItemsCursorAdapter(
			this,
			R.layout.queue_list_item,
			null,
			new String[] {},
			new int[] {},
			0);

		ListView listView = (ListView)findViewById(R.id.feed_queue_list);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				showFeedItemDetails(id);
			}
		});

		updateQueueList();
	}



	private void updateQueueList() {
		final String[] projection = {
			Contract.FeedItems._ID,
			Contract.FeedItems.TITLE,
			Contract.FeedItems.PUBLISHED,
			Contract.FeedItems.SIZE,
			Contract.FeedItems.DOWNLOADED,
			Contract.FeedItems.FEED_ID,
			Contract.FeedItems.DURATION,
			Contract.FeedItems.IS_STARRED
		};

		filter.setShowUnreadOnly(!filter.isShowStarred());
		final String selection = filter.setupSelection("");

		final String sortOrder = Contract.FeedItems.PUBLISHED + " desc";

		LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {
				return new CursorLoader(
					MainActivity.this,
					Contract.FeedItems.CONTENT_URI,
					projection,
					selection,
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

		getSupportLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks);
	}



	private void showFeedItemDetails(long item_id) {
		Intent intent = new Intent(this, FeedItemDetailsActivity.class);
		intent.putExtra("item_id", item_id);
		intent.putExtra("filter", filter);
		startActivity(intent);
	}



	private void showSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}



	private void showProgress() {
		btnRefresh.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);
	}



	private void hideProgress() {
		progressBar.setVisibility(View.INVISIBLE);
		btnRefresh.setVisibility(View.VISIBLE);
	}



	private void switchFilterStarred(boolean isStarred) {
		filter.setShowStarred(isStarred);

		btnFilterStarred.setVisibility(isStarred ? View.INVISIBLE : View.VISIBLE);
		btnClearFilterStarred.setVisibility(isStarred ? View.VISIBLE : View.INVISIBLE);

		updateQueueList();
	}

	//endregion
}
