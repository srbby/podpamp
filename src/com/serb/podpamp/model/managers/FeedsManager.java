package com.serb.podpamp.model.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.foxykeep.datadroid.exception.DataException;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.utils.Utils;
import org.mcsoxford.rss.Enclosure;
import org.mcsoxford.rss.RSSItem;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public abstract class FeedsManager {
	public static void markFeedItemAsReadOrUnread(Context context, long feedItemId, boolean isRead) {
		final String[] projection = {
			Contract.FeedItems.FEED_ID
		};

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedItemId) };

		Cursor cursor = context.getContentResolver().query(Contract.FeedItems.CONTENT_URI,
			projection, selection, selectionArgs, null);

		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				long feedId = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.FEED_ID));
				markFeedItemAsReadOrUnread(context, feedId, feedItemId, isRead);
			}
			cursor.close();
		}
	}



	public static void markFeedItemAsReadOrUnread(Context context, long feedId, long feedItemId, boolean isRead) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.IS_READ, isRead);

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedItemId) };

		context.getContentResolver().update(Contract.FeedItems.CONTENT_URI, values, selection, selectionArgs);

		updateUnreadFeedItemsCount(context, feedId, isRead ? -1 : 1);
	}



	public static void markAllFeedItemAsReadOrUnread(Context context, long feedId, boolean isRead) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.IS_READ, isRead);

		final String selection = Contract.FeedItems.FEED_ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedId) };

		context.getContentResolver().update(Contract.FeedItems.CONTENT_URI, values, selection, selectionArgs);

		updateUnreadFeedItemsCount(context, feedId, 0);
	}



	public static void deleteFeed(Context context, long feedId) {
		String selection = Contract.FeedItems.FEED_ID + " = ?";
		String[] selectionArgs = { String.valueOf(feedId) };

		context.getContentResolver().delete(
			Contract.FeedItems.CONTENT_URI,
			selection,
			selectionArgs
		);

		selection = Contract.Feeds._ID + " = ?";

		context.getContentResolver().delete(
			Contract.Feeds.CONTENT_URI,
			selection,
			selectionArgs
		);
	}



	public static void updateUnreadFeedItemsCount(Context context, long feedId, int diff)
	{
		int unreadItemsCount = diff;
		if (diff != 0)
		{
			String[] projection = {
				Contract.Feeds.UNREAD_ITEMS_COUNT
			};

			String selection = Contract.Feeds._ID + " = ?";
			String[] selectionArgs = { String.valueOf(feedId) };

			Cursor cursor = context.getContentResolver().query(Contract.Feeds.CONTENT_URI,
				projection, selection, selectionArgs, null);

			if (cursor != null)
			{
				if (cursor.moveToNext())
				{
					unreadItemsCount = cursor.getInt(cursor.getColumnIndex(Contract.Feeds.UNREAD_ITEMS_COUNT)) + diff;
				}
				cursor.close();
			}
		}

		ContentValues values = new ContentValues();
		values.put(Contract.FeedsColumns.UNREAD_ITEMS_COUNT, unreadItemsCount);

		final String selection = Contract.Feeds._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedId) };

		context.getContentResolver().update(Contract.Feeds.CONTENT_URI, values, selection, selectionArgs);
	}



	public static void addFeedItem(Context context, long feedId, RSSItem item, boolean isRead) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.FEED_ID, feedId);
		values.put(Contract.FeedItemsColumns.GUID, item.getGuid());
		values.put(Contract.FeedItemsColumns.PUBLISHED, item.getPubDate().getTime());
		values.put(Contract.FeedItemsColumns.TITLE, item.getTitle());
		values.put(Contract.FeedItemsColumns.DESC, item.getSummary());

		final Enclosure enclosure = item.getEnclosure();
		if (enclosure != null)
		{
			values.put(Contract.FeedItemsColumns.MEDIA_URL, enclosure.getUrl().toString());
			values.put(Contract.FeedItemsColumns.SIZE, enclosure.getLength());
		}

		values.put(Contract.FeedItemsColumns.IS_READ, isRead);

		context.getContentResolver().insert(Contract.FeedItems.CONTENT_URI, values);
	}



	public static EpisodeMetadata getEpisodeMetadata(Context context, long feedItemId)
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
				result = getEpisodeMetadata(cursor);
			}
			cursor.close();
		}

		return result;
	}



	public static EpisodeMetadata getEpisodeMetadata(Cursor cursor) {
		EpisodeMetadata result = new EpisodeMetadata();
		result.feedItemId = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems._ID));
		result.url = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.MEDIA_URL));
		//result.fileName = Utils.getDownloadFolder() + cursor.getString(cursor.getColumnIndex(Contract.FeedItems.TITLE));
		result.fileName = Utils.getDownloadFolder() + result.feedItemId;
		result.file = new File(result.fileName);
		return result;
	}



	public static void downloadEpisode(Context context, long feedItemId) {
		EpisodeMetadata metadata = getEpisodeMetadata(context, feedItemId);

		downloadEpisode(context, metadata);
	}



	public static void downloadEpisode(Context context, EpisodeMetadata metadata) {
		try {
			URL url = new URL(metadata.url);

			URLConnection connection = url.openConnection();
			connection.connect();
			// this will be useful so that you can show a typical 0-100% progress bar
			//int fileLength = connection.getContentLength();

			// download the file
			InputStream input = new BufferedInputStream(url.openStream());

			if (metadata.file.createNewFile())
			{
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

				metadata.size = total;

				output.flush();
				output.close();
				input.close();
			}
			else
			{
				throw new DataException("Unable to create a file: " + metadata.fileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DataException e) {
			e.printStackTrace();
		}

		updateFeedItem(context, metadata);
	}

	//region Private Methods.

	private static void updateFeedItem(Context context, EpisodeMetadata metadata) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.FILE_PATH, metadata.fileName);
		values.put(Contract.FeedItemsColumns.SIZE, metadata.size);

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(metadata.feedItemId) };

		context.getContentResolver().update(Contract.FeedItems.CONTENT_URI, values, selection, selectionArgs);
	}

	//endregion
}
