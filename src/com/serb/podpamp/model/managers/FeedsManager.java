package com.serb.podpamp.model.managers;

import android.content.ContentValues;
import android.content.Context;
import com.serb.podpamp.model.provider.Contract;

public abstract class FeedsManager {
	public static void MarkFeedItemAsReadOrUnread(Context context, long feedItemId, boolean isRead) {
		ContentValues values = new ContentValues();
		values.put(Contract.FeedItemsColumns.IS_READ, isRead);

		final String selection = Contract.FeedItems._ID + " = ?";
		final String[] selectionArgs = { String.valueOf(feedItemId) };

		context.getContentResolver().update(Contract.FeedItems.CONTENT_URI, values, selection, selectionArgs);
	}
}
