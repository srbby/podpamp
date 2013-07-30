package com.serb.podpamp.model.operations;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService;
import com.serb.podpamp.model.managers.FeedsManager;
import com.serb.podpamp.model.provider.Contract;

public class DownloadNewEpisodesOperation implements RequestService.Operation {
	@Override
	public Bundle execute(Context context, Request request)
		throws ConnectionException, DataException, CustomRequestException {

		final String[] projection = {
			Contract.FeedItems._ID,
			Contract.FeedItems.TITLE,
			Contract.FeedItems.MEDIA_URL
		};

		final String selection = Contract.FeedItems.IS_READ + " = 0 and "
			+ Contract.FeedItems.FILE_PATH + " is null";

		Cursor cursor = context.getContentResolver().query(Contract.FeedItems.CONTENT_URI,
			projection, selection, null, null);

		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				FeedsManager.waitForDownload(context, FeedsManager.getEpisodeMetadata(cursor));
			}

			int index = 1;
			cursor.moveToFirst();

			do
			{
				FeedsManager.downloadEpisode(context, FeedsManager.getEpisodeMetadata(cursor), index, cursor.getCount());
				index++;
			}
			while (cursor.moveToNext());

			cursor.close();
		}

		return null;
	}
}
