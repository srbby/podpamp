package com.serb.podpamp.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.serb.podpamp.R;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.model.request.FeedsRequestManager;
import com.serb.podpamp.model.request.RequestFactory;

public class FeedsActivity extends FragmentActivity implements View.OnClickListener,
		AddFeedDialog.AddFeedDialogListener {
	private FeedsRequestManager requestManager;



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
		String[] projection = {
			Contract.Feeds._ID,
			Contract.Feeds.ICON,
			Contract.Feeds.TITLE,
			Contract.Feeds.NEW_ITEMS_COUNT,
			Contract.Feeds.URL
		};

		Cursor cursor = this.getContentResolver().query(Contract.Feeds.CONTENT_URI,
			projection, null, null, null);
		startManagingCursor(cursor);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
			this, // Context.
			android.R.layout.two_line_list_item,
			cursor,
			new String[] { Contract.Feeds.TITLE, Contract.Feeds.URL },
			new int[] {android.R.id.text1, android.R.id.text2});

		ListView listView = (ListView)findViewById(R.id.feeds_list);
		listView.setAdapter(adapter);
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

	//endregion
}