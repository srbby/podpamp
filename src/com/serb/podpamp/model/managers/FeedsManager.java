package com.serb.podpamp.model.managers;

import android.content.*;
import android.database.Cursor;
import android.os.IBinder;
import android.text.TextUtils;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.model.provider.DatabaseHelper;
import com.serb.podpamp.model.provider.FeedsProvider;
import com.serb.podpamp.utils.DownloadService;
import com.serb.podpamp.utils.Utils;
import org.mcsoxford.rss.Enclosure;
import org.mcsoxford.rss.RSSItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public abstract class FeedsManager {
	private static DownloadService downloadService;
	private static boolean isDownloadServiceBound;

	public static void updateFeedItemElapsed(Context context, long feedItemId, int elapsed) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.ELAPSED, elapsed);

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedItemId) };

		context.getContentResolver().update(Contract.FeedItems.CONTENT_URI, values, selection, selectionArgs);
	}



	public static void markFeedItemAsReadOrUnread(Context context, long feedItemId, boolean isRead) {
		long feedId = getFeedId(context, feedItemId);
		if (feedId > -1)
		{
			markFeedItemAsReadOrUnread(context, feedId, feedItemId, isRead);
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



	public static void star(Context context, long feedItemId, boolean isStarred, long feedId) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.IS_STARRED, isStarred);

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedItemId) };

		context.getContentResolver().update(Contract.FeedItems.CONTENT_URI, values, selection, selectionArgs);

		if (feedId > -1)
		{
			updateStarredFeedItemsCount(context, feedId, isStarred ? 1 : -1);
		}
	}



	public static void updateStarredFeedItemsCount(Context context, long feedId, int diff)
	{
		int starredItemsCount = diff;
		if (diff != 0)
		{
			String[] projection = {
				Contract.Feeds.STARRED_ITEMS_COUNT
			};

			String selection = Contract.Feeds._ID + " = ?";
			String[] selectionArgs = { String.valueOf(feedId) };

			Cursor cursor = context.getContentResolver().query(Contract.Feeds.CONTENT_URI,
				projection, selection, selectionArgs, null);

			if (cursor != null)
			{
				if (cursor.moveToNext())
				{
					starredItemsCount = cursor.getInt(cursor.getColumnIndex(Contract.Feeds.STARRED_ITEMS_COUNT)) + diff;
				}
				cursor.close();
			}
		}

		ContentValues values = new ContentValues();
		values.put(Contract.FeedsColumns.STARRED_ITEMS_COUNT, starredItemsCount);

		final String selection = Contract.Feeds._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedId) };

		context.getContentResolver().update(Contract.Feeds.CONTENT_URI, values, selection, selectionArgs);
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
					if (unreadItemsCount < 0)
						unreadItemsCount = 0;
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
		values.put(Contract.FeedItemsColumns.TITLE, item.getTitle().trim());

		String desc = item.getSummary();
		if (TextUtils.isEmpty(desc))
			desc = item.getContent();
		if (TextUtils.isEmpty(desc))
			desc = item.getDescription();
		values.put(Contract.FeedItemsColumns.DESC, desc.trim());

		final Enclosure enclosure = item.getEnclosure();
		if (enclosure != null)
		{
			values.put(Contract.FeedItemsColumns.MEDIA_URL, enclosure.getUrl().toString().trim());
			values.put(Contract.FeedItemsColumns.SIZE, enclosure.getLength());
		}

		values.put(Contract.FeedItemsColumns.IS_READ, isRead);

		context.getContentResolver().insert(Contract.FeedItems.CONTENT_URI, values);
	}



	public static void refreshIcon(Context context, long feedId) {
		String[] projection = {
			Contract.Feeds.ICON_URL
		};

		final String selection = Contract.Feeds._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedId) };

		Cursor cursor = context.getContentResolver().query(Contract.Feeds.CONTENT_URI,
			projection, selection, selectionArgs, null);

		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				String url = cursor.getString(cursor.getColumnIndex(Contract.Feeds.ICON_URL));
				setIcon(context, feedId, DownloadManager.downloadImage(url));
			}
			cursor.close();
		}
	}



	public static void setIcon(Context context, long feedId, byte[] icon) {
		final String selection = Contract.Feeds._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedId) };

		ContentValues values = new ContentValues();
		values.put(Contract.FeedsColumns.ICON, icon);

		context.getContentResolver().update(Contract.Feeds.CONTENT_URI, values, selection, selectionArgs);
	}



	public static EpisodeMetadata getEpisodeMetadata(Context context, long feedItemId)
	{
		final String[] projection = {
			Contract.FeedItems._ID,
			Contract.FeedItems.FEED_ID,
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
		long feedId = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.FEED_ID));
		result.title = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.TITLE));
		result.url = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.MEDIA_URL));
		result.fileName = Utils.getDownloadFolder() + composeFileName(feedId, result.feedItemId, result.title, result.url);
		result.file = new File(result.fileName);
		return result;
	}



	public static void downloadEpisode(final Context context, long feedItemId) {
		final EpisodeMetadata metadata = getEpisodeMetadata(context, feedItemId);

		ServiceConnection downloadServiceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName className, IBinder service) {
				DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
				downloadService = binder.getService();
				isDownloadServiceBound = true;
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				isDownloadServiceBound = false;
			}
		};

		Intent downloaderIntent = new Intent(context, DownloadService.class);
		context.bindService(downloaderIntent, downloadServiceConnection, Context.BIND_AUTO_CREATE);

		if (isDownloadServiceBound)
			downloadService.updateDownloadProgress(context, metadata);

		DownloadManager.downloadEpisode(metadata, new DownloadManager.OnProgressUpdateListener() {
			@Override
			public void updateProgress(EpisodeMetadata m) {
			if (isDownloadServiceBound)
				downloadService.updateDownloadProgress(context, m);
			}
		});

		context.unbindService(downloadServiceConnection);
		updateDownloaded(context, metadata);
	}



	public static boolean removeDownload(Context context, long feedItemId, String filePath) {
		boolean result = false;

		if (!TextUtils.isEmpty(filePath))
		{
			File file = new File(filePath);
			result = file.delete();
		}

		if (feedItemId > -1)
		{
			ContentValues values = new ContentValues();
			values.put(Contract.FeedItemsColumns.FILE_PATH, (String)null);
			values.put(Contract.FeedItemsColumns.DOWNLOADED, 0);

			final String selection = Contract.FeedItems._ID + " = ?";
			final String[] selectionArgs = { String.valueOf(feedItemId) };

			context.getContentResolver().update(Contract.FeedItems.CONTENT_URI, values, selection, selectionArgs);

			result = true;
		}

		return result;
	}



	public static void waitForDownload(Context context, long feedItemId) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.DOWNLOADED, -1);

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedItemId) };

		context.getContentResolver().update(Contract.FeedItems.CONTENT_URI, values, selection, selectionArgs);
	}



	public static Collection<Long> getNewEpisodes(Context context) {
		final String[] projection = {
			Contract.FeedItems._ID
		};

		final String selection = Contract.FeedItems.IS_READ + " = 0 and "
			+ Contract.FeedItems.FILE_PATH + " is null";

		Cursor cursor = context.getContentResolver().query(Contract.FeedItems.CONTENT_URI,
			projection, selection, null, null);

		ArrayList<Long> result = new ArrayList<Long>();

		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				long feedItemId = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems._ID));
				result.add(feedItemId);
				waitForDownload(context, feedItemId);
			}

			cursor.close();
		}

		return result;
	}



	public static int cleanUp(Context context) {
		final String[] projection = {
			Contract.FeedItems._ID,
			Contract.FeedItems.FILE_PATH
		};

		final String selection = Contract.FeedItems.IS_READ + " = 1 and " +
			"ifnull(" + Contract.FeedItems.IS_STARRED + ", 0) = 0 and " +
			Contract.FeedItems.PUBLISHED + " < ? and " +
			Contract.FeedItems.FILE_PATH + " is not null";
		final String[] selectionArgs = { String.valueOf(new Date().getTime() - Utils.getEpisodeKeepDays(context) * 1000 * 60 * 60 * 24) };

		Cursor cursor = context.getContentResolver().query(Contract.FeedItems.CONTENT_URI,
			projection, selection, selectionArgs, null);

		int result = 0;

		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				long id = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems._ID));
				String filePath = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.FILE_PATH));

				if (removeDownload(context, id, filePath))
					result++;
			}
			cursor.close();
		}

		return result;
	}



	public static boolean isFeedAdded(Context context, String feedUrl) {
		String[] projection = {
			Contract.Feeds._ID
		};

		final String selection = Contract.Feeds.URL + " = ?";
		final String[] selectionArgs = { feedUrl };

		Cursor cursor = context.getContentResolver().query(Contract.Feeds.CONTENT_URI,
			projection, selection, selectionArgs, null);

		if (cursor == null)
			return false;

		boolean result = cursor.getCount() > 0;
		cursor.close();
		return result;
	}



	public static void checkIntegrity(Context context) {
		DatabaseHelper mDBHelper = new DatabaseHelper(context, FeedsProvider.DB_NAME, null, FeedsProvider.DB_VERSION);
		String sql = "update " + Contract.TABLE_FEEDS + " set " + Contract.Feeds.UNREAD_ITEMS_COUNT +
			" = (select count(*) from " + Contract.TABLE_FEED_ITEMS + " fi where fi." + Contract.FeedItems.FEED_ID +
			" = " + Contract.TABLE_FEEDS + "." + Contract.Feeds._ID + " and fi." + Contract.FeedItems.IS_READ + " = 0)";

		mDBHelper.getWritableDatabase().execSQL(sql);
		mDBHelper.close();
	}

	//region Private Methods.

	private static void updateDownloaded(Context context, EpisodeMetadata metadata) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.FILE_PATH, metadata.fileName);
		values.put(Contract.FeedItemsColumns.SIZE, metadata.getSize());
		values.put(Contract.FeedItemsColumns.DOWNLOADED, metadata.getDownloaded());

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(metadata.feedItemId) };

		context.getContentResolver().update(Contract.FeedItems.CONTENT_URI, values, selection, selectionArgs);
	}



	private static long getFeedId(Context context, long feedItemId) {
		final String[] projection = {
			Contract.FeedItems.FEED_ID
		};

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedItemId) };

		Cursor cursor = context.getContentResolver().query(Contract.FeedItems.CONTENT_URI,
			projection, selection, selectionArgs, null);

		long result = -1;

		if (cursor != null)
		{
			if (cursor.moveToNext())
			{
				result = cursor.getLong(cursor.getColumnIndex(Contract.FeedItems.FEED_ID));
			}
			cursor.close();
		}
		return result;
	}



	private static String composeFileName(long feedId, long feedItemId, String title, String url) {
		//todo make filename of first word of the feed url + title + guid + ext
		return feedItemId + ".mp3";
	}

	//endregion
}
