package com.serb.podpamp.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
	private long itemId = -1;
	String filePath;
	boolean isRead;

	private FeedsRequestManager requestManager;



	RequestManager.RequestListener requestListener = new RequestManager.RequestListener() {
		@Override
		public void onRequestFinished(Request request, Bundle resultData) {
			setupItemInfoPanel();
			Toast.makeText(FeedItemDetailsActivity.this, "Download complete", Toast.LENGTH_LONG).show();
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
			itemId = extras.getLong("item_id");

		if (itemId > -1)
		{
			setupItemInfoPanel();
		}

		findViewById(R.id.btn_download).setOnClickListener(this);
		findViewById(R.id.btn_mark_listened).setOnClickListener(this);
		findViewById(R.id.btn_mark_not_listened).setOnClickListener(this);
		findViewById(R.id.btn_play).setOnClickListener(this);

		requestManager = FeedsRequestManager.from(this);
	}



	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_download:
				downloadFeed();
				break;
			case R.id.btn_mark_listened:
				FeedsManager.markFeedItemAsReadOrUnread(this, itemId, true);
				setupItemInfoPanel();
				break;
			case R.id.btn_mark_not_listened:
				FeedsManager.markFeedItemAsReadOrUnread(this, itemId, false);
				setupItemInfoPanel();
				break;
			case R.id.btn_play:
				playFile();
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
			Contract.FeedItems.SIZE,
			Contract.FeedItems.IS_READ,
			Contract.FeedItems.FILE_PATH
		};

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(itemId) };

		Cursor cursor = getContentResolver().query(Contract.FeedItems.CONTENT_URI,
			projection, selection, selectionArgs, null);

		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				String title = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.TITLE));
				String desc = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.DESC));
				long published = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.PUBLISHED));
				long size = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.SIZE));
				long feedId = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.FEED_ID));
				filePath = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.FILE_PATH));
				isRead = cursor.getInt(cursor.getColumnIndex(Contract.FeedItems.IS_READ)) > 0;

				TextView titleView = (TextView) findViewById(R.id.txt_feed_item_title);
				titleView.setText(title);
				titleView.setTextColor(getResources().getColor(isRead ? R.color.read_item_color : R.color.unread_item_color));

				TextView descView = (TextView) findViewById(R.id.txt_feed_item_desc);
				descView.setText(desc);

				TextView publishedView = (TextView) findViewById(R.id.txt_feed_item_published);
				publishedView.setText(Utils.getDateText(published));
				publishedView.setTextColor(getResources().getColor(isRead ? R.color.read_item_color : R.color.unread_item_color));

				TextView sizeView = (TextView) findViewById(R.id.txt_feed_item_size);
				sizeView.setText(Utils.getFileSizeText(size));
				sizeView.setTextColor(getResources().getColor(isRead ? R.color.read_item_color : R.color.unread_item_color));

				Utils.setImageView(this,
					(ImageView) findViewById(R.id.img_feed_icon),
					feedId,
					R.drawable.icon_rss);

				Button downloadButton = (Button) findViewById(R.id.btn_download);
				Button playButton = (Button) findViewById(R.id.btn_play);

				if (TextUtils.isEmpty(filePath))
				{
					playButton.setVisibility(View.INVISIBLE);
					downloadButton.setVisibility(View.VISIBLE);
				}
				else
				{
					downloadButton.setVisibility(View.INVISIBLE);
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
		if (itemId > -1 && Utils.isNetworkAvailable(this, true))
		{
			requestManager.execute(RequestFactory.getDownloadEpisodeRequest(itemId), requestListener);
		}
	}



	private void playFile()
	{
//		if (!TextUtils.isEmpty(filePath))
//		{
//			startService(new Intent(PowerAMPiAPI.ACTION_API_COMMAND)
//				.putExtra(PowerAMPiAPI.COMMAND, PowerAMPiAPI.Commands.OPEN_TO_PLAY)
//				//.putExtra(PowerAMPiAPI.Track.POSITION, 10) // Play from 10th second.
//				.setData(Uri.parse("file://" + filePath)));
//		}
		if (!TextUtils.isEmpty(filePath))
		{
			startService(new Intent("com.maxmpz.audioplayer.API_COMMAND")
				.putExtra("cmd", 20)
				//.putExtra(PowerAMPiAPI.Track.POSITION, 10) // Play from 10th second.
				.setData(Uri.parse("file://" + filePath)));
			if (!isRead)
			{
				FeedsManager.markFeedItemAsReadOrUnread(this, itemId, true);
				setupItemInfoPanel();
			}
		}
	}

	//endregion
}