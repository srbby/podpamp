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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.serb.podpamp.R;
import com.serb.podpamp.model.domain.FeedUrlsContainer;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.model.request.FeedsRequestManager;
import com.serb.podpamp.model.request.RequestFactory;
import com.serb.podpamp.ui.adapters.FeedsCursorAdapter;
import com.serb.podpamp.ui.dialogs.AddFeedDialog;
import com.serb.podpamp.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class FeedsActivity extends FragmentActivity implements View.OnClickListener,
	AddFeedDialog.AddFeedDialogListener {

	private static final String TAG = FeedsActivity.class.toString();

	private static final int LOADER_ID = 0;

	String[] projection = {
		Contract.Feeds._ID,
		Contract.Feeds.ICON,
		Contract.Feeds.TITLE,
		Contract.Feeds.UNREAD_ITEMS_COUNT,
		Contract.Feeds.STARRED_ITEMS_COUNT
	};

	String sortOrder = Contract.Feeds.TITLE;

	FeedsCursorAdapter adapter;

	private FeedsRequestManager requestManager;

	private ProgressBar progressBar;



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
			progressBar.setVisibility(View.INVISIBLE);
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
			progressBar.setVisibility(View.INVISIBLE);
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

		progressBar = (ProgressBar) findViewById(R.id.progress_bar);

		findViewById(R.id.btn_add_feed).setOnClickListener(this);

		setupFeedsList();

		requestManager = FeedsRequestManager.from(this);
	}



	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_add_feed:
				showAddFeedDialog();
				break;
		}
	}



	@Override
	public void onDialogPositiveClick(String feedUrl) {
		if (feedUrl.length() > 0) {
			if (feedUrl.startsWith("http"))
				addFeed(feedUrl);
			else {
				try {
					File file = new File(new URI(feedUrl));
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line;

					List<String> feedUrls = new ArrayList<String>();
					while ((line = br.readLine()) != null) {
						feedUrls.add(line);
					}
					addFeeds(feedUrls);
				}
				catch (IOException e) {
					Log.e(TAG, e.getMessage());
					Utils.showAlert(this, "Error",
						String.format(getString(R.string.subscriptions_parse_error), feedUrl));
				} catch (URISyntaxException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case AddFeedDialog.FILE_SELECT_CODE:
				if (data != null) {
					String filename = data.getDataString();
					if (!TextUtils.isEmpty(filename))
						onDialogPositiveClick(filename);
				}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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



	private void showAddFeedDialog() {
		if (Utils.isNetworkAvailable(this, false))
		{
			DialogFragment newFragment = new AddFeedDialog();
			newFragment.show(getSupportFragmentManager(), "add_feed");
		}
	}



	private void addFeed(String feedUrl) {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add(feedUrl);
		addFeeds(ar);
	}



	private void addFeeds(List<String> feedUrls) {
		progressBar.setVisibility(View.VISIBLE);
		requestManager.execute(RequestFactory.getAddFeedRequest(new FeedUrlsContainer(feedUrls)), requestListener);
	}



	private void showFeedItemsList(long id)
	{
		Intent intent = new Intent(this, FeedItemsActivity.class);
		intent.putExtra("feed_id", id);
		startActivity(intent);
	}

	//endregion
}