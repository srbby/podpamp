package com.serb.podpamp.model.managers;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import com.serb.podpamp.model.domain.FeedItem;
import com.serb.podpamp.model.provider.Contract;
import com.serb.podpamp.ui.FeedItemFilter;

import java.util.LinkedList;
import java.util.Queue;

public abstract class PlaylistManager {
	public static Queue<FeedItem> getPlaylist(Context context, long feedItemId, FeedItemFilter filter) {
		final String[] projection = {
			Contract.FeedItems._ID,
			Contract.FeedItems.FILE_PATH,
			Contract.FeedItems.ELAPSED
		};

		final String selection = filter.setupSelection(Contract.FeedItems._ID + " != ?");
		final String[] selectionArgs = { String.valueOf(feedItemId) };

		final String sortOrder = Contract.FeedItems.PUBLISHED;

		Queue<FeedItem> result = new LinkedList<FeedItem>();

		Cursor cursor = context.getContentResolver().query(Contract.FeedItems.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				String filePath = cursor.getString(cursor.getColumnIndex(Contract.FeedItems.FILE_PATH));
				if (!TextUtils.isEmpty(filePath))
				{
					result.add(new FeedItem(cursor.getLong(cursor.getColumnIndex(Contract.FeedItems._ID)),
						filePath,
						cursor.getInt(cursor.getColumnIndex(Contract.FeedItems.ELAPSED))));
				}
			}
			cursor.close();
		}

		return result;
	}
}
