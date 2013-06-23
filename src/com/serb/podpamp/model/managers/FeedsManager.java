package com.serb.podpamp.model.managers;

import android.content.ContentValues;
import android.content.Context;
import com.serb.podpamp.model.provider.Contract;

public abstract class FeedsManager {
	public static void markFeedItemAsReadOrUnread(Context context, long feedItemId, boolean isRead) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.IS_READ, isRead);

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedItemId) };

		context.getContentResolver().update(Contract.FeedItems.CONTENT_URI, values, selection, selectionArgs);
	}



	public static void markAllFeedItemAsReadOrUnread(Context context, long feedId, boolean isRead) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.IS_READ, isRead);

		final String selection = Contract.FeedItems.FEED_ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedId) };

		context.getContentResolver().update(Contract.FeedItems.CONTENT_URI, values, selection, selectionArgs);

		updateUnreadItemsCount(context, feedId, 0);
	}

	//region Private Methods.

	private static void updateUnreadItemsCount(Context context, long feedId, int diff)
	{
		int unreadItemsCount = diff;//todo if diff is not 0, use current value + diff

		ContentValues values = new ContentValues();
		values.put(Contract.FeedsColumns.NEW_ITEMS_COUNT, unreadItemsCount);

		final String selection = Contract.Feeds._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedId) };

		context.getContentResolver().update(Contract.Feeds.CONTENT_URI, values, selection, selectionArgs);
	}

	//endregion
}
