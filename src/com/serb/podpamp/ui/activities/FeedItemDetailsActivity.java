package com.serb.podpamp.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.serb.podpamp.R;
import com.serb.podpamp.model.managers.FeedsManager;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.model.request.FeedsRequestManager;
import com.serb.podpamp.model.request.RequestFactory;
import com.serb.podpamp.utils.Utils;

public class FeedItemDetailsActivity extends Activity implements View.OnClickListener {
	private long item_id = -1;

	private FeedsRequestManager requestManager;



	RequestManager.RequestListener requestListener = new RequestManager.RequestListener() {
		@Override
		public void onRequestFinished(Request request, Bundle resultData) {
			setupItemInfoPanel();
			Toast.makeText(FeedItemDetailsActivity.this, "complete", Toast.LENGTH_LONG).show();
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
			AlertDialog.Builder builder = new AlertDialog.Builder(FeedItemDetailsActivity.this);
			builder.setTitle(android.R.string.dialog_alert_title)
				.setMessage(getString(R.string.download_failed))
				.create()
				.show();
		}
	};



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.feed_item_details);

		Bundle extras = getIntent().getExtras();
		if (extras != null)
			item_id = extras.getLong("item_id");

		if (item_id > -1)
		{
			setupItemInfoPanel();
		}

		findViewById(R.id.btn_download).setOnClickListener(this);
		findViewById(R.id.btn_mark_listened).setOnClickListener(this);
		findViewById(R.id.btn_mark_not_listened).setOnClickListener(this);

		requestManager = FeedsRequestManager.from(this);
	}



	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_download:
				downloadFeed();
				break;
			case R.id.btn_mark_listened:
				FeedsManager.MarkFeedItemAsReadOrUnread(this, item_id, true);
				setupItemInfoPanel();
				break;
			case R.id.btn_mark_not_listened:
				FeedsManager.MarkFeedItemAsReadOrUnread(this, item_id, false);
				setupItemInfoPanel();
				break;
		}
	}

	//region Private Methods.

	private void setupItemInfoPanel() {
		final String[] projection = {
			Contract.FeedItems._ID,
			Contract.FeedItems.FEED_ID,
			Contract.FeedItems.TITLE,
			Contract.FeedItems.DESC,
			Contract.FeedItems.PUBLISHED,
			Contract.FeedItems.LENGTH,
			Contract.FeedItems.IS_READ,
			Contract.FeedItems.FILE_PATH
		};

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(item_id) };

		Cursor cursor = getContentResolver().query(Contract.FeedItems.CONTENT_URI,
			projection, selection, selectionArgs, null);

		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				String title = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.TITLE));
				String desc = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.DESC));
				long published = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.PUBLISHED));
				long length = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.LENGTH));
				long feedId = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.FEED_ID));
				String filePath = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.FILE_PATH));
				boolean isRead = cursor.getInt(cursor.getColumnIndex(Contract.FeedItems.IS_READ)) > 0;

				TextView titleView = (TextView) findViewById(R.id.txt_feed_item_title);
				titleView.setText(title);

				TextView descView = (TextView) findViewById(R.id.txt_feed_item_desc);
				descView.setText(desc);

				TextView publishedView = (TextView) findViewById(R.id.txt_feed_item_published);
				publishedView.setText(Utils.getDateText(published));

				TextView lengthView = (TextView) findViewById(R.id.txt_feed_item_length);
				lengthView.setText(Utils.getFileSizeText(length));

				Utils.setImageView(this,
					(ImageView) findViewById(R.id.img_feed_icon),
					feedId,
					R.drawable.icon_rss);

				if (TextUtils.isEmpty(filePath))
				{
					Button downloadButton = (Button) findViewById(R.id.btn_download);
					downloadButton.setVisibility(View.VISIBLE);
				}
				else
				{
					Button playButton = (Button) findViewById(R.id.btn_play);
					playButton.setVisibility(View.VISIBLE);
				}

				Button btnMarkListened = (Button) findViewById(R.id.btn_mark_listened);
				Button btnMarkNotListened = (Button) findViewById(R.id.btn_mark_not_listened);
				if (isRead)
				{
					btnMarkListened.setVisibility(View.INVISIBLE);
					btnMarkNotListened.setVisibility(View.VISIBLE);
				}
				else
				{
					btnMarkListened.setVisibility(View.VISIBLE);
					btnMarkNotListened.setVisibility(View.INVISIBLE);
				}
			}
			cursor.close();
		}
	}



	private void downloadFeed() {
		if (item_id > -1 && Utils.isNetworkAvailable(this, true))
		{
			requestManager.execute(RequestFactory.getDownloadEpisodeRequest(item_id), requestListener);
		}
	}

	//endregion
}