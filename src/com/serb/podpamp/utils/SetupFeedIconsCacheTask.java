package com.serb.podpamp.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import com.serb.podpamp.model.provider.Contract;

public class SetupFeedIconsCacheTask extends AsyncTask<Context, Void, Void> {
	@Override
	protected Void doInBackground(Context... context) {
		String[] projection = {
			Contract.Feeds._ID,
			Contract.Feeds.ICON
		};

		Cursor cursor = context[0].getContentResolver().query(Contract.Feeds.CONTENT_URI,
			projection, null, null, null);

		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				long feedId = cursor.getLong(cursor.getColumnIndex(Contract.Feeds._ID));
				byte[] icon = cursor.getBlob(cursor.getColumnIndex(Contract.Feeds.ICON));
				Utils.putFeedIcon(feedId, icon);
			}
			cursor.close();
		}
		return null;
	}
}
