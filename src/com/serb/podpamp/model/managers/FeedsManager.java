package com.serb.podpamp.model.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.serb.podpamp.model.provider.Contract;
import org.mcsoxford.rss.Enclosure;
import org.mcsoxford.rss.RSSItem;

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
}
