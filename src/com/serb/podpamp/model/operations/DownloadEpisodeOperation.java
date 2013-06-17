package com.serb.podpamp.model.operations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.model.request.RequestFactory;
import com.serb.podpamp.utils.Utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class DownloadEpisodeOperation implements RequestService.Operation {
	@Override
	public Bundle execute(Context context, Request request)
		throws ConnectionException, DataException, CustomRequestException {

		long feedItemId = request.getLong(RequestFactory.FEED_ITEM_ID);
		EpisodeMetadata metadata = getEpisodeMetadata(context, feedItemId);

		try {
			URL url = new URL(metadata.url);

			URLConnection connection = url.openConnection();
			connection.connect();
			// this will be useful so that you can show a typical 0-100% progress bar
			//int fileLength = connection.getContentLength();

			// download the file
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(metadata.file);

			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				// publishing the progress....
//				Bundle resultData = new Bundle();
//				resultData.putInt("progress" ,(int) (total * 100 / fileLength));
//				receiver.send(UPDATE_PROGRESS, resultData);
				output.write(data, 0, count);
			}

			metadata.length = total;

			output.flush();
			output.close();
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		updateFeedItem(context, metadata);
//		RSSReader reader = new RSSReader();
//
//		try {
//			RSSFeed rss_feed = reader.load(url);
//			List<RSSItem> items = rss_feed.getItems();
//
//			ContentValues values = new ContentValues();
//			values.put(Contract.FeedsColumns.TITLE, rss_feed.getTitle());
//			values.put(Contract.FeedsColumns.SUBTITLE, rss_feed.getSubtitle());
//			values.put(Contract.FeedsColumns.ICON_URL, rss_feed.getIconUrl().toString());
//			values.put(Contract.FeedsColumns.ICON, Utils.downloadImage(rss_feed.getIconUrl().toString()));
//			values.put(Contract.FeedsColumns.URL, url);
//			values.put(Contract.FeedsColumns.NEW_ITEMS_COUNT, items.size());
//
//			Uri feedUri = context.getContentResolver().insert(Contract.Feeds.CONTENT_URI, values);
//
//			long feedItemId = ContentUris.parseId(feedUri);
//			for (RSSItem item : items) {
//				addFeedItem(context, feedItemId, item);
//			}
//		} catch (RSSReaderException e) {
//			e.printStackTrace();
//		}
		return null;
	}



	private EpisodeMetadata getEpisodeMetadata(Context context, long feedItemId)
	{
		final String[] projection = {
			Contract.FeedItems._ID,
			Contract.FeedItems.TITLE,
			Contract.FeedItems.MEDIA_URL
		};

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedItemId) };

		Cursor cursor = context.getContentResolver().query(Contract.FeedItems.CONTENT_URI,
			projection, selection, selectionArgs, null);

		EpisodeMetadata result = null;

		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				result = new EpisodeMetadata();
				result.feedItemId = feedItemId;
				result.url = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.MEDIA_URL));
				result.fileName = Utils.getDownloadFolder() + cursor.getString(cursor.getColumnIndex(Contract.FeedItems.TITLE));
				result.file = new File(result.fileName);
			}
			cursor.close();
		}

		return result;
	}



	private void updateFeedItem(Context context, EpisodeMetadata metadata) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.FILE_PATH, metadata.fileName);
		values.put(Contract.FeedItemsColumns.LENGTH, metadata.length);

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(metadata.feedItemId) };

		context.getContentResolver().update(Contract.FeedItems.CONTENT_URI, values, selection, selectionArgs);
	}



	private class EpisodeMetadata {
		public long feedItemId;
		public String url;
		public String fileName;
		public File file;
		public long length;
	}



//	private void addFeedItem(Context context, long feedItemId, RSSItem item) {
//		ContentValues values = new ContentValues();
//		values.put(Contract.FeedItemsColumns.FEED_ID, feedItemId);
//		values.put(Contract.FeedItemsColumns.TITLE, item.getTitle());
//		values.put(Contract.FeedItemsColumns.DESC, item.getSummary());
//
//		final Enclosure enclosure = item.getEnclosure();
//		if (enclosure != null)
//		{
//			values.put(Contract.FeedItemsColumns.MEDIA_URL, enclosure.getUrl().toString());
//			values.put(Contract.FeedItemsColumns.LENGTH, enclosure.getLength());
//		}
//
//		values.put(Contract.FeedItemsColumns.IS_READ, false);
//		values.put(Contract.FeedItemsColumns.PUBLISHED, item.getPubDate().getTime());
//
//		context.getContentResolver().insert(Contract.FeedItems.CONTENT_URI, values);
//	}
}
